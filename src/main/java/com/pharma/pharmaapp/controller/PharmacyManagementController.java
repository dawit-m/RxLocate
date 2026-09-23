package com.pharma.pharmaapp.controller;

import com.pharma.pharmaapp.entity.Medicine;
import com.pharma.pharmaapp.entity.Pharmacy;
import com.pharma.pharmaapp.entity.Sale;
import com.pharma.pharmaapp.service.MedicineService;
import com.pharma.pharmaapp.service.PharmacyDashboardService;
import com.pharma.pharmaapp.service.PharmacyService;
import com.pharma.pharmaapp.service.SaleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/pharmacy")
public class PharmacyManagementController {

    private static final String SESSION_PHARMACY = "loggedInPharmacy";
    private final MedicineService medicineService;
    private final PharmacyDashboardService dashboardService;
    private final PharmacyService pharmacyService;
    private final SaleService saleService;

    public PharmacyManagementController(MedicineService medicineService, PharmacyDashboardService dashboardService, PharmacyService pharmacyService, SaleService saleService) {
        this.medicineService = medicineService;
        this.dashboardService = dashboardService;
        this.pharmacyService = pharmacyService;
        this.saleService = saleService;
    }

    @GetMapping("/register")
    public String showRegisterPage() { return "pharmacy-register"; }

    @PostMapping("/register")
    public String registerPharmacy(@RequestParam String pharmacyName, @RequestParam String location,
                                   @RequestParam String tinNumber, @RequestParam String phoneNumber,
                                   @RequestParam String username, @RequestParam String password,
                                   @RequestParam(required = false) String googleMapLink,
                                   RedirectAttributes redirectAttributes) {
        if (password.length() < 8) {
            redirectAttributes.addFlashAttribute("error", "Password must contain at least 8 characters.");
            return "redirect:/pharmacy/register";
        }
        Pharmacy pharmacy = new Pharmacy();
        pharmacy.setPharmacyName(pharmacyName.trim()); pharmacy.setLocation(location.trim());
        pharmacy.setTinNumber(tinNumber.trim()); pharmacy.setPhoneNumber(phoneNumber.trim());
        pharmacy.setUsername(username.trim()); pharmacy.setPassword(password);
        pharmacy.setGoogleMapLink(googleMapLink == null ? null : googleMapLink.trim());
        try {
            pharmacyService.register(pharmacy);
            redirectAttributes.addFlashAttribute("success", "Registration complete. You can now sign in.");
            return "redirect:/pharmacy/login";
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
            return "redirect:/pharmacy/register";
        }
    }

    @GetMapping("/login")
    public String showLoginPage() { return "pharmacy-login"; }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password,
                        HttpSession session, RedirectAttributes redirectAttributes) {
        Pharmacy pharmacy = pharmacyService.login(username, password);
        if (pharmacy != null) {
            session.setAttribute(SESSION_PHARMACY, pharmacy);
            return "redirect:/pharmacy/dashboard";
        }
        redirectAttributes.addFlashAttribute("error", "Invalid username or password.");
        return "redirect:/pharmacy/login";
    }

    @GetMapping({"/dashboard", "/medicines", "/sales", "/reports"})
    public String dashboard(Model model, HttpSession session, jakarta.servlet.http.HttpServletRequest request,
                            @RequestParam(defaultValue = "month") String range) {
        Pharmacy pharmacy = currentPharmacy(session);
        if (pharmacy == null) return "redirect:/pharmacy/login";

        String path = request.getServletPath();
        String view = path.endsWith("/medicines") ? "medicines" : path.endsWith("/sales") ? "sales" : path.endsWith("/reports") ? "reports" : "dashboard";
        populateDashboard(model, pharmacy, view, range);
        return "pharmacy-dashboard";
    }

    private void populateDashboard(Model model, Pharmacy pharmacy, String view, String range) {
        PharmacyDashboardService.DashboardSnapshot snapshot = dashboardService.buildSnapshot(pharmacy);
        String normalizedRange = normalizeReportRange(range);

        model.addAttribute("medicines", snapshot.medicines());
        model.addAttribute("recentSales", snapshot.recentSales());
        model.addAttribute("pharmacyName", pharmacy.getPharmacyName());
        model.addAttribute("totalProducts", snapshot.totalProducts());
        model.addAttribute("totalUnits", snapshot.totalUnits());
        model.addAttribute("lowStockCount", snapshot.lowStockCount());
        model.addAttribute("outOfStockCount", snapshot.outOfStockCount());
        model.addAttribute("expiredCount", snapshot.expiredCount());
        model.addAttribute("inventoryCost", snapshot.inventoryCost());
        model.addAttribute("retailValue", snapshot.retailValue());
        model.addAttribute("potentialProfit", snapshot.potentialProfit());
        model.addAttribute("marginPercent", snapshot.marginPercent());
        model.addAttribute("realizedRevenue", snapshot.realizedRevenue());
        model.addAttribute("realizedGrossProfit", snapshot.realizedGrossProfit());
        model.addAttribute("soldUnits", snapshot.soldUnits());
        model.addAttribute("maxStock", snapshot.maxStock());
        model.addAttribute("maxRetailLineValue", snapshot.maxRetailLineValue());
        model.addAttribute("maxSaleRevenue", snapshot.maxSaleRevenue());
        model.addAttribute("allSales", snapshot.allSales());
        List<Sale> reportSales = "all".equals(normalizedRange) ? snapshot.allSales() : snapshot.allSales().stream()
                .filter(sale -> sale.getSoldAt() != null && sale.getSoldAt().toLocalDate().getMonth() == LocalDate.now().getMonth()
                        && sale.getSoldAt().toLocalDate().getYear() == LocalDate.now().getYear())
                .toList();
        double reportRevenue = reportSales.stream().mapToDouble(Sale::getRevenue).sum();
        double reportProfit = reportSales.stream().mapToDouble(Sale::getGrossProfit).sum();
        int reportUnits = reportSales.stream().mapToInt(Sale::getQuantity).sum();
        List<DailyRevenue> dailyRevenue = reportSales.stream()
                .filter(sale -> sale.getSoldAt() != null)
                .collect(Collectors.groupingBy(sale -> sale.getSoldAt().toLocalDate(), TreeMap::new,
                        Collectors.summingDouble(Sale::getRevenue)))
                .entrySet().stream()
                .map(entry -> new DailyRevenue(entry.getKey(), entry.getValue()))
                .toList();
        double reportMaxRevenue = dailyRevenue.stream().mapToDouble(DailyRevenue::revenue).max().orElse(1.0);
        model.addAttribute("reportSales", reportSales);
        model.addAttribute("dailyRevenue", dailyRevenue);
        model.addAttribute("reportRevenue", reportRevenue);
        model.addAttribute("reportProfit", reportProfit);
        model.addAttribute("reportUnits", reportUnits);
        model.addAttribute("reportMaxRevenue", reportMaxRevenue);
        model.addAttribute("reportRange", normalizedRange);
        model.addAttribute("reportMonthLabel", LocalDate.now().getMonth().toString().charAt(0) + LocalDate.now().getMonth().toString().substring(1).toLowerCase());
        model.addAttribute("activePage", view);
    }

    private record DailyRevenue(LocalDate date, double revenue) { }

    private String normalizeReportRange(String range) {
        String requested = range == null ? "" : range.trim().toLowerCase(Locale.ROOT);
        return "all".equals(requested) ? "all" : "month";
    }

    @PostMapping("/add-medicine")
    public String addMedicine(@RequestParam String name, @RequestParam double costPrice,
                               @RequestParam double price, @RequestParam int quantity,
                               @RequestParam String expiryDate, @RequestParam String brandCountry,
                               @RequestParam String dosage, HttpSession session,
                               RedirectAttributes redirectAttributes) {
        Pharmacy pharmacy = currentPharmacy(session);
        if (pharmacy == null) return "redirect:/pharmacy/login";
        if (name.isBlank() || costPrice < 0 || price < costPrice || quantity < 0 || expiryDate.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Enter valid prices and quantity. Selling price must be at least the cost price.");
            return "redirect:/pharmacy/medicines";
        }
        Medicine medicine = new Medicine();
        medicine.setName(name.trim()); medicine.setCostPrice(costPrice); medicine.setPrice(price); medicine.setQuantity(quantity);
        medicine.setBrandCountry(brandCountry == null ? null : brandCountry.trim());
        medicine.setDosage(dosage == null ? null : dosage.trim()); medicine.setExpiryDate(LocalDate.parse(expiryDate));
        medicine.setPharmacy(pharmacy); medicineService.save(medicine);
        redirectAttributes.addFlashAttribute("success", "Medicine added to inventory.");
        return "redirect:/pharmacy/medicines";
    }

    @PostMapping("/record-sale")
    public String recordSale(@RequestParam Long medicineId, @RequestParam int quantity,
                             HttpSession session, RedirectAttributes redirectAttributes) {
        Pharmacy pharmacy = currentPharmacy(session);
        if (pharmacy == null) return "redirect:/pharmacy/login";
        try {
            Sale sale = saleService.recordSale(medicineId, quantity, pharmacy);
            redirectAttributes.addFlashAttribute("success", "Sale recorded: " + quantity + " unit(s), " + String.format("%.2f ETB", sale.getRevenue()) + " revenue.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/pharmacy/sales";
    }

    @PostMapping("/delete-medicine/{id}")
    public String deleteMedicine(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Pharmacy pharmacy = currentPharmacy(session);
        if (pharmacy == null) return "redirect:/pharmacy/login";
        if (medicineService.deleteMedicineForPharmacy(id, pharmacy)) redirectAttributes.addFlashAttribute("success", "Medicine removed from inventory.");
        else redirectAttributes.addFlashAttribute("error", "Medicine not found in your inventory.");
        return "redirect:/pharmacy/medicines";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) { session.invalidate(); return "redirect:/pharmacy/login"; }

    private Pharmacy currentPharmacy(HttpSession session) { return (Pharmacy) session.getAttribute(SESSION_PHARMACY); }
}

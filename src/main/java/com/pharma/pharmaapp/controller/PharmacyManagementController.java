package com.pharma.pharmaapp.controller;

import com.pharma.pharmaapp.entity.Medicine;
import com.pharma.pharmaapp.entity.Pharmacy;
import com.pharma.pharmaapp.repository.PharmacyRepository;
import com.pharma.pharmaapp.service.MedicineService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/pharmacy")
public class PharmacyManagementController {

    @Autowired
    private MedicineService medicineService;

    @Autowired
    private PharmacyRepository pharmacyRepository;

    @GetMapping("/register")
    public String showRegisterPage() {
        return "pharmacy-register";
    }

    @PostMapping("/register")
    public String registerPharmacy(@RequestParam String pharmacyName,
            @RequestParam String location,
            @RequestParam String tinNumber,
            @RequestParam String phoneNumber,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam(required = false) String googleMapLink) {
        Pharmacy pharmacy = new Pharmacy();
        pharmacy.setPharmacyName(pharmacyName);
        pharmacy.setLocation(location);
        pharmacy.setTinNumber(tinNumber);
        pharmacy.setPhoneNumber(phoneNumber);
        pharmacy.setUsername(username);
        pharmacy.setPassword(password);
        pharmacy.setGoogleMapLink(googleMapLink);

        pharmacyRepository.save(pharmacy);
        return "redirect:/pharmacy/login?registered=true";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "pharmacy-login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        Optional<Pharmacy> pharmacyOpt = pharmacyRepository.findByUsername(username);

        if (pharmacyOpt.isPresent() && pharmacyOpt.get().getPassword().equals(password)) {
            session.setAttribute("loggedInPharmacy", pharmacyOpt.get());
            return "redirect:/pharmacy/dashboard";
        }

        model.addAttribute("error", true);
        return "pharmacy-login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        Pharmacy loggedInPharmacy = (Pharmacy) session.getAttribute("loggedInPharmacy");

        if (loggedInPharmacy == null) {
            return "redirect:/pharmacy/login";
        }

        List<Medicine> medicines = medicineService.findByPharmacy(loggedInPharmacy);
        model.addAttribute("medicines", medicines);
        model.addAttribute("pharmacyName", loggedInPharmacy.getPharmacyName());

        return "pharmacy-dashboard";
    }

    @PostMapping("/add-medicine")
    public String addMedicine(@RequestParam String name,
            @RequestParam double price,
            @RequestParam int quantity,
            @RequestParam String expiryDate,
            @RequestParam String brandCountry,
            @RequestParam String dosage,
            HttpSession session) {

        Pharmacy loggedInPharmacy = (Pharmacy) session.getAttribute("loggedInPharmacy");
        if (loggedInPharmacy == null)
            return "redirect:/pharmacy/login";

        Medicine medicine = new Medicine();
        medicine.setName(name);
        medicine.setPrice(price);
        medicine.setQuantity(quantity);

        medicine.setBrandCountry(brandCountry);
        medicine.setDosage(dosage);

        if (expiryDate != null && !expiryDate.isEmpty()) {
            medicine.setExpiryDate(LocalDate.parse(expiryDate));
        }

        medicine.setPharmacy(loggedInPharmacy);

        medicineService.save(medicine);
        return "redirect:/pharmacy/dashboard";
    }

    @GetMapping("/delete-medicine/{id}")
    public String deleteMedicine(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("loggedInPharmacy") == null)
            return "redirect:/pharmacy/login";

        medicineService.deleteMedicine(id);
        return "redirect:/pharmacy/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/pharmacy/login";
    }
}
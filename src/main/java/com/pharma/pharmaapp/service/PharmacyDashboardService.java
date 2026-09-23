package com.pharma.pharmaapp.service;

import com.pharma.pharmaapp.entity.Medicine;
import com.pharma.pharmaapp.entity.Pharmacy;
import com.pharma.pharmaapp.entity.Sale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class PharmacyDashboardService {
    private final MedicineService medicineService;
    private final SaleService saleService;

    public PharmacyDashboardService(MedicineService medicineService, SaleService saleService) {
        this.medicineService = medicineService;
        this.saleService = saleService;
    }

    @Transactional(readOnly = true)
    public DashboardSnapshot buildSnapshot(Pharmacy pharmacy) {
        List<Medicine> medicines = medicineService.findByPharmacy(pharmacy);
        List<Sale> recentSales = saleService.recentSales(pharmacy);
        List<Sale> allSales = saleService.allSales(pharmacy);
        LocalDate today = LocalDate.now();

        int totalUnits = medicines.stream().mapToInt(Medicine::getQuantity).sum();
        long lowStockCount = medicines.stream()
                .filter(medicine -> medicine.getQuantity() > 0 && medicine.getQuantity() < 10)
                .count();
        long outOfStockCount = medicines.stream()
                .filter(medicine -> medicine.getQuantity() == 0)
                .count();
        long expiredCount = medicines.stream()
                .filter(medicine -> medicine.getExpiryDate() != null && medicine.getExpiryDate().isBefore(today))
                .count();
        double inventoryCost = medicines.stream()
                .mapToDouble(medicine -> medicine.getCostPrice() * medicine.getQuantity())
                .sum();
        double retailValue = medicines.stream()
                .mapToDouble(medicine -> medicine.getPrice() * medicine.getQuantity())
                .sum();
        double potentialProfit = retailValue - inventoryCost;
        double marginPercent = retailValue == 0 ? 0 : (potentialProfit / retailValue) * 100;
        double realizedRevenue = allSales.stream().mapToDouble(Sale::getRevenue).sum();
        double realizedGrossProfit = allSales.stream().mapToDouble(Sale::getGrossProfit).sum();
        int soldUnits = allSales.stream().mapToInt(Sale::getQuantity).sum();

        return new DashboardSnapshot(
                medicines,
                recentSales,
                allSales,
                medicines.size(),
                totalUnits,
                lowStockCount,
                outOfStockCount,
                expiredCount,
                inventoryCost,
                retailValue,
                potentialProfit,
                marginPercent,
                realizedRevenue,
                realizedGrossProfit,
                soldUnits,
                medicines.stream().mapToInt(Medicine::getQuantity).max().orElse(1),
                medicines.stream().mapToDouble(medicine -> medicine.getPrice() * medicine.getQuantity()).max().orElse(1),
                recentSales.stream().mapToDouble(Sale::getRevenue).max().orElse(1)
        );
    }

    public record DashboardSnapshot(
            List<Medicine> medicines,
            List<Sale> recentSales,
            List<Sale> allSales,
            int totalProducts,
            int totalUnits,
            long lowStockCount,
            long outOfStockCount,
            long expiredCount,
            double inventoryCost,
            double retailValue,
            double potentialProfit,
            double marginPercent,
            double realizedRevenue,
            double realizedGrossProfit,
            int soldUnits,
            int maxStock,
            double maxRetailLineValue,
            double maxSaleRevenue
    ) {
    }
}

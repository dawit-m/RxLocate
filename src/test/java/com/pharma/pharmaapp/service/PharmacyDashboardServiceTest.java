package com.pharma.pharmaapp.service;

import com.pharma.pharmaapp.entity.Medicine;
import com.pharma.pharmaapp.entity.Pharmacy;
import com.pharma.pharmaapp.entity.Sale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PharmacyDashboardServiceTest {
    @Mock
    private MedicineService medicineService;
    @Mock
    private SaleService saleService;
    @InjectMocks
    private PharmacyDashboardService dashboardService;

    @Test
    void buildsConsistentOperationalSnapshot() {
        Pharmacy pharmacy = new Pharmacy();
        Medicine lowStock = medicine("Amoxicillin", 10, 16, 4, LocalDate.now().plusDays(60));
        Medicine expired = medicine("Ibuprofen", 8, 12, 0, LocalDate.now().minusDays(1));
        Sale sale = new Sale(pharmacy, lowStock, 2, 16, 10);

        when(medicineService.findByPharmacy(pharmacy)).thenReturn(List.of(lowStock, expired));
        when(saleService.recentSales(pharmacy)).thenReturn(List.of(sale));
        when(saleService.allSales(pharmacy)).thenReturn(List.of(sale));

        PharmacyDashboardService.DashboardSnapshot snapshot = dashboardService.buildSnapshot(pharmacy);

        assertThat(snapshot.totalProducts()).isEqualTo(2);
        assertThat(snapshot.totalUnits()).isEqualTo(4);
        assertThat(snapshot.lowStockCount()).isEqualTo(1);
        assertThat(snapshot.outOfStockCount()).isEqualTo(1);
        assertThat(snapshot.expiredCount()).isEqualTo(1);
        assertThat(snapshot.retailValue()).isEqualTo(64);
        assertThat(snapshot.inventoryCost()).isEqualTo(40);
        assertThat(snapshot.potentialProfit()).isEqualTo(24);
        assertThat(snapshot.realizedRevenue()).isEqualTo(32);
        assertThat(snapshot.realizedGrossProfit()).isEqualTo(12);
        assertThat(snapshot.soldUnits()).isEqualTo(2);
    }

    private Medicine medicine(String name, double cost, double price, int quantity, LocalDate expiry) {
        Medicine medicine = new Medicine();
        medicine.setName(name);
        medicine.setCostPrice(cost);
        medicine.setPrice(price);
        medicine.setQuantity(quantity);
        medicine.setExpiryDate(expiry);
        return medicine;
    }
}

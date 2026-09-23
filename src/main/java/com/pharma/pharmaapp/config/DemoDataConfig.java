package com.pharma.pharmaapp.config;

import com.pharma.pharmaapp.entity.Medicine;
import com.pharma.pharmaapp.entity.Pharmacy;
import com.pharma.pharmaapp.entity.Sale;
import com.pharma.pharmaapp.repository.MedicineRepository;
import com.pharma.pharmaapp.repository.PharmacyRepository;
import com.pharma.pharmaapp.repository.SaleRepository;
import com.pharma.pharmaapp.service.PharmacyService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Profile("demo")
public class DemoDataConfig {

    private static final String DEMO_PASSWORD = "demo12345";

    @Bean
    CommandLineRunner seedDemoData(PharmacyRepository pharmacyRepository,
                                   MedicineRepository medicineRepository,
                                   SaleRepository saleRepository,
                                   PharmacyService pharmacyService) {
        return args -> {
            List<PharmacySeed> pharmacies = List.of(
                    new PharmacySeed("Abyssinia Pharmacy", "DEMO-001", "Bole, Addis Ababa", "+251 911 234 567", "demo-pharmacy", "Bole+Addis+Ababa"),
                    new PharmacySeed("HealthPlus Pharmacy", "DEMO-002", "Piassa, Addis Ababa", "+251 922 345 678", "demo-healthplus", "Piassa+Addis+Ababa"),
                    new PharmacySeed("MediCare Pharmacy", "DEMO-003", "Kazanchis, Addis Ababa", "+251 933 456 789", "demo-medicare", "Kazanchis+Addis+Ababa"),
                    new PharmacySeed("Green Cross Pharmacy", "DEMO-004", "CMC, Addis Ababa", "+251 944 567 890", "demo-greencross", "CMC+Addis+Ababa"),
                    new PharmacySeed("Unity Pharmacy", "DEMO-005", "Megenagna, Addis Ababa", "+251 955 678 901", "demo-unity", "Megenagna+Addis+Ababa")
            );

            for (int pharmacyIndex = 0; pharmacyIndex < pharmacies.size(); pharmacyIndex++) {
                PharmacySeed seed = pharmacies.get(pharmacyIndex);
                Pharmacy pharmacy = pharmacyRepository.findByUsername(seed.username())
                        .orElseGet(() -> pharmacyService.register(new Pharmacy(
                                seed.name(), seed.tin(), seed.location(), seed.phone(), seed.username(), DEMO_PASSWORD,
                                "https://maps.google.com/?q=" + seed.mapQuery())));

                List<Medicine> medicines = ensureInventory(pharmacy, pharmacyIndex, medicineRepository);
                List<Sale> existingSales = saleRepository.findByPharmacyOrderBySoldAtDesc(pharmacy);
                if (existingSales.isEmpty()) {
                    seedSales(pharmacy, medicines, saleRepository, pharmacyIndex);
                } else if (existingSales.stream().filter(sale -> sale.getSoldAt() != null)
                        .map(sale -> sale.getSoldAt().toLocalDate()).distinct().count() <= 1) {
                    spreadSalesAcrossWeek(existingSales, saleRepository);
                }
            }
        };
    }

    private List<Medicine> ensureInventory(Pharmacy pharmacy, int pharmacyIndex, MedicineRepository medicineRepository) {
        List<MedicineSeed> catalogue = catalogueFor(pharmacyIndex);
        List<Medicine> existing = medicineRepository.findByPharmacy(pharmacy);
        List<Medicine> medicines = new ArrayList<>(existing);

        for (MedicineSeed item : catalogue) {
            boolean alreadyPresent = existing.stream().anyMatch(medicine -> medicine.getName().equalsIgnoreCase(item.name()));
            if (!alreadyPresent) {
                Medicine medicine = medicine(pharmacy, item);
                medicines.add(medicineRepository.save(medicine));
            }
        }
        return medicines;
    }

    private void seedSales(Pharmacy pharmacy, List<Medicine> medicines, SaleRepository saleRepository, int pharmacyIndex) {
        if (medicines.isEmpty()) return;
        int salesToCreate = Math.min(12, medicines.size() * 2 + 3);
        for (int saleIndex = 0; saleIndex < salesToCreate; saleIndex++) {
            Medicine medicine = medicines.get((saleIndex + pharmacyIndex) % medicines.size());
            int quantity = 1 + ((saleIndex + pharmacyIndex) % 4);
            Sale sale = new Sale(pharmacy, medicine, quantity, medicine.getPrice(), medicine.getCostPrice());
            sale.setSoldAt(demoSaleTime(saleIndex));
            saleRepository.save(sale);
        }
    }

    private void spreadSalesAcrossWeek(List<Sale> sales, SaleRepository saleRepository) {
        for (int index = 0; index < sales.size(); index++) {
            sales.get(index).setSoldAt(demoSaleTime(index));
        }
        saleRepository.saveAll(sales);
    }

    private LocalDateTime demoSaleTime(int index) {
        return LocalDateTime.now().minusDays(Math.min(7, index))
                .withHour(9 + (index % 8)).withMinute(15).withSecond(0).withNano(0);
    }

    private List<MedicineSeed> catalogueFor(int pharmacyIndex) {
        int shift = pharmacyIndex * 2;
        return List.of(
                new MedicineSeed("Paracetamol", "500mg", 16.00 + shift, 24.00 + shift, 40 - pharmacyIndex * 4, "Ethiopia", 2027, 3, 30),
                new MedicineSeed("Amoxicillin", "250mg", 59.00, 85.00 + shift, pharmacyIndex == 1 ? 0 : 6 + pharmacyIndex, "India", 2027, 1, 15),
                new MedicineSeed("Cetirizine", "10mg", 27.00, 42.50 + shift, pharmacyIndex == 2 ? 3 : 18, "Ethiopia", 2027, 5, 1),
                new MedicineSeed("Ibuprofen", "400mg", 22.00, 36.00 + shift, 28 - pharmacyIndex * 3, "Kenya", 2028, 2, 12),
                new MedicineSeed("Azithromycin", "500mg", 75.00, 110.00 + shift, 4 + pharmacyIndex, "India", 2026, 12, 20),
                new MedicineSeed("Omeprazole", "20mg", 31.00, 48.00 + shift, pharmacyIndex == 3 ? 0 : 15, "Ethiopia", 2027, 9, 18),
                new MedicineSeed("Metformin", "500mg", 18.00, 29.00 + shift, 22 - pharmacyIndex * 2, "Ethiopia", 2028, 6, 5),
                new MedicineSeed("ORS Sachets", "20.5g", 4.50, 8.00 + pharmacyIndex, 60 - pharmacyIndex * 5, "Ethiopia", 2027, 11, 25)
        );
    }

    private Medicine medicine(Pharmacy pharmacy, MedicineSeed item) {
        Medicine medicine = new Medicine();
        medicine.setPharmacy(pharmacy);
        medicine.setName(item.name());
        medicine.setCostPrice(item.costPrice());
        medicine.setDosage(item.dosage());
        medicine.setPrice(item.price());
        medicine.setQuantity(item.quantity());
        medicine.setBrandCountry(item.origin());
        medicine.setExpiryDate(LocalDate.of(item.expiryYear(), item.expiryMonth(), item.expiryDay()));
        return medicine;
    }

    private record PharmacySeed(String name, String tin, String location, String phone, String username, String mapQuery) { }

    private record MedicineSeed(String name, String dosage, double costPrice, double price, int quantity,
                                 String origin, int expiryYear, int expiryMonth, int expiryDay) { }
}

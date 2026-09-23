package com.pharma.pharmaapp.service;

import com.pharma.pharmaapp.entity.Medicine;
import com.pharma.pharmaapp.entity.Pharmacy;
import com.pharma.pharmaapp.entity.Sale;
import com.pharma.pharmaapp.repository.MedicineRepository;
import com.pharma.pharmaapp.repository.SaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final MedicineRepository medicineRepository;

    public SaleService(SaleRepository saleRepository, MedicineRepository medicineRepository) {
        this.saleRepository = saleRepository;
        this.medicineRepository = medicineRepository;
    }

    @Transactional
    public Sale recordSale(Long medicineId, int quantity, Pharmacy pharmacy) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Sale quantity must be greater than zero.");
        }
        Medicine medicine = medicineRepository.findByIdAndPharmacy(medicineId, pharmacy)
                .orElseThrow(() -> new IllegalArgumentException("Medicine was not found in your inventory."));
        if (medicine.getQuantity() < quantity) {
            throw new IllegalArgumentException("Not enough stock. Only " + medicine.getQuantity() + " units are available.");
        }
        medicine.setQuantity(medicine.getQuantity() - quantity);
        medicineRepository.save(medicine);
        return saleRepository.save(new Sale(pharmacy, medicine, quantity, medicine.getPrice(), medicine.getCostPrice()));
    }

    @Transactional(readOnly = true)
    public List<Sale> recentSales(Pharmacy pharmacy) {
        return saleRepository.findTop8ByPharmacyOrderBySoldAtDesc(pharmacy);
    }

    @Transactional(readOnly = true)
    public List<Sale> allSales(Pharmacy pharmacy) {
        return saleRepository.findByPharmacyOrderBySoldAtDesc(pharmacy);
    }
}

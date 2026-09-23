package com.pharma.pharmaapp.service;

import com.pharma.pharmaapp.entity.Medicine;
import com.pharma.pharmaapp.entity.Pharmacy;
import com.pharma.pharmaapp.repository.MedicineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class MedicineService {

    private final MedicineRepository medicineRepository;

    public MedicineService(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    @Transactional(readOnly = true)
    public List<Medicine> findByPharmacy(Pharmacy pharmacy) {
        return medicineRepository.findByPharmacy(pharmacy);
    }

    @Transactional
    public Medicine save(Medicine medicine) {
        return medicineRepository.save(medicine);
    }

    @Transactional
    public boolean deleteMedicineForPharmacy(Long id, Pharmacy pharmacy) {
        return medicineRepository.findByIdAndPharmacy(id, pharmacy)
                .map(medicine -> {
                    medicineRepository.delete(medicine);
                    return true;
                })
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public List<Medicine> searchByName(String name) {
        if (name == null || name.isBlank()) {
            return List.of();
        }
        return medicineRepository.findByNameContainingIgnoreCaseAndQuantityGreaterThanAndExpiryDateGreaterThanEqual(
                name.trim(), 0, LocalDate.now());
    }

    /**
     * Adjust inventory inside one write transaction. The repository query uses
     * PESSIMISTIC_WRITE, so this method must NOT be read-only.
     */
    @Transactional
    public boolean adjustQuantityForPharmacy(Long id, Pharmacy pharmacy, String action) {
        Medicine medicine = medicineRepository.findByIdAndPharmacy(id, pharmacy).orElse(null);
        if (medicine == null) {
            return false;
        }

        if ("increase".equals(action)) {
            medicine.setQuantity(medicine.getQuantity() + 1);
        } else if ("decrease".equals(action)) {
            if (medicine.getQuantity() == 0) {
                return false;
            }
            medicine.setQuantity(medicine.getQuantity() - 1);
        } else {
            return false;
        }

        // The entity is managed by the active transaction, so an explicit save
        // is not required, but keeping it makes the persistence intent clear.
        medicineRepository.save(medicine);
        return true;
    }

    /**
     * Kept for compatibility with any existing callers.
     * This method is now a normal write transaction because the repository
     * method uses a PESSIMISTIC_WRITE lock.
     */
    @Transactional
    public Medicine findByIdForPharmacy(Long id, Pharmacy pharmacy) {
        return medicineRepository.findByIdAndPharmacy(id, pharmacy).orElse(null);
    }
}

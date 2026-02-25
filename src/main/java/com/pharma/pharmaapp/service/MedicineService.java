package com.pharma.pharmaapp.service;

import com.pharma.pharmaapp.entity.Medicine;
import com.pharma.pharmaapp.entity.Pharmacy;
import com.pharma.pharmaapp.repository.MedicineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicineService {

    @Autowired
    private MedicineRepository medicineRepository;

    public List<Medicine> findAll() {
        return medicineRepository.findAll();
    }

    public Medicine save(Medicine medicine) {
        return medicineRepository.save(medicine);
    }

    public void deleteMedicine(Long id) {
        medicineRepository.deleteById(id);
    }

    public List<Medicine> findByPharmacy(Pharmacy pharmacy) {
        return medicineRepository.findByPharmacy(pharmacy);
    }

    public List<Medicine> searchByName(String name) {
        List<Medicine> results = medicineRepository.findByNameContainingIgnoreCase(name);
        for (Medicine med : results) {
            medicineRepository.save(med);
        }
        return results;
    }

    public Medicine findById(Long id) {
        return medicineRepository.findById(id).orElse(null);
    }
}
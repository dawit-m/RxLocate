package com.pharma.pharmaapp.repository;

import com.pharma.pharmaapp.entity.Medicine;
import com.pharma.pharmaapp.entity.Pharmacy;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {
    List<Medicine> findByNameContainingIgnoreCase(String name);
    List<Medicine> findByNameContainingIgnoreCaseAndQuantityGreaterThanAndExpiryDateGreaterThanEqual(
            String name, int quantity, LocalDate expiryDate);
    List<Medicine> findByPharmacy(Pharmacy pharmacy);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Medicine> findByIdAndPharmacy(Long id, Pharmacy pharmacy);
}

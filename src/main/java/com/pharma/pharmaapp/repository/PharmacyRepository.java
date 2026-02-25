package com.pharma.pharmaapp.repository;

import com.pharma.pharmaapp.entity.Pharmacy;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PharmacyRepository extends JpaRepository<Pharmacy, Long> {
    Optional<Pharmacy> findByUsername(String username);
}
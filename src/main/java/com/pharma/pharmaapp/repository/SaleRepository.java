package com.pharma.pharmaapp.repository;

import com.pharma.pharmaapp.entity.Pharmacy;
import com.pharma.pharmaapp.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findTop8ByPharmacyOrderBySoldAtDesc(Pharmacy pharmacy);
    List<Sale> findByPharmacyOrderBySoldAtDesc(Pharmacy pharmacy);
}

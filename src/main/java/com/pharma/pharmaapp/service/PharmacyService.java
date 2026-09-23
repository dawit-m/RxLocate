package com.pharma.pharmaapp.service;

import com.pharma.pharmaapp.entity.Pharmacy;
import com.pharma.pharmaapp.repository.PharmacyRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PharmacyService {

    private final PharmacyRepository pharmacyRepository;
    private final PasswordEncoder passwordEncoder;

    public PharmacyService(PharmacyRepository pharmacyRepository, PasswordEncoder passwordEncoder) {
        this.pharmacyRepository = pharmacyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Pharmacy register(Pharmacy pharmacy) {
        pharmacy.setUsername(pharmacy.getUsername().trim());
        pharmacy.setPassword(passwordEncoder.encode(pharmacy.getPassword()));
        try {
            return pharmacyRepository.save(pharmacy);
        } catch (DataIntegrityViolationException exception) {
            throw new IllegalArgumentException("Username or TIN number is already registered", exception);
        }
    }

    @Transactional(readOnly = true)
    public Pharmacy login(String username, String rawPassword) {
        return pharmacyRepository.findByUsername(username.trim())
                .filter(pharmacy -> passwordEncoder.matches(rawPassword, pharmacy.getPassword()))
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public Optional<Pharmacy> findById(Long id) {
        return pharmacyRepository.findById(id);
    }
}

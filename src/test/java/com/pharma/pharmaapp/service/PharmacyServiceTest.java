package com.pharma.pharmaapp.service;

import com.pharma.pharmaapp.entity.Pharmacy;
import com.pharma.pharmaapp.repository.PharmacyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PharmacyServiceTest {

    @Mock
    private PharmacyRepository pharmacyRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PharmacyService pharmacyService;

    @Test
    void registerHashesPasswordBeforePersisting() {
        Pharmacy pharmacy = pharmacy("secret123");
        when(passwordEncoder.encode("secret123")).thenReturn("bcrypt-hash");
        when(pharmacyRepository.save(any(Pharmacy.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Pharmacy saved = pharmacyService.register(pharmacy);

        assertThat(saved.getPassword()).isEqualTo("bcrypt-hash");
        verify(passwordEncoder).encode("secret123");
        verify(pharmacyRepository).save(pharmacy);
    }

    @Test
    void loginReturnsPharmacyOnlyForMatchingPassword() {
        Pharmacy pharmacy = pharmacy("bcrypt-hash");
        when(pharmacyRepository.findByUsername("owner")).thenReturn(Optional.of(pharmacy));
        when(passwordEncoder.matches(eq("secret123"), eq("bcrypt-hash"))).thenReturn(true);
        when(passwordEncoder.matches(eq("wrong-password"), eq("bcrypt-hash"))).thenReturn(false);

        assertThat(pharmacyService.login("owner", "secret123")).isSameAs(pharmacy);
        assertThat(pharmacyService.login("owner", "wrong-password")).isNull();
    }

    private Pharmacy pharmacy(String password) {
        Pharmacy pharmacy = new Pharmacy();
        pharmacy.setUsername("owner");
        pharmacy.setPassword(password);
        return pharmacy;
    }
}

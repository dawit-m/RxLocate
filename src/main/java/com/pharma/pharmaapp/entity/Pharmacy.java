package com.pharma.pharmaapp.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pharmacies")
public class Pharmacy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String pharmacyName;

    @Column(nullable = false, unique = true)
    private String tinNumber;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false, unique = true)
    @JsonIgnore
    private String username;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Column(length = 500)
    private String googleMapLink;

    @OneToMany(mappedBy = "pharmacy", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Medicine> medicines = new ArrayList<>();

    public Pharmacy() {
    }

    public Pharmacy(String pharmacyName, String tinNumber, String location, String phoneNumber,
            String username, String password, String googleMapLink) {
        this.pharmacyName = pharmacyName;
        this.tinNumber = tinNumber;
        this.location = location;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.password = password;
        this.googleMapLink = googleMapLink;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPharmacyName() {
        return pharmacyName;
    }

    public void setPharmacyName(String pharmacyName) {
        this.pharmacyName = pharmacyName;
    }

    public String getTinNumber() {
        return tinNumber;
    }

    public void setTinNumber(String tinNumber) {
        this.tinNumber = tinNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGoogleMapLink() {
        return googleMapLink;
    }

    public void setGoogleMapLink(String googleMapLink) {
        this.googleMapLink = googleMapLink;
    }

    public List<Medicine> getMedicines() {
        return medicines;
    }

    public void setMedicines(List<Medicine> medicines) {
        this.medicines = medicines;
    }
}

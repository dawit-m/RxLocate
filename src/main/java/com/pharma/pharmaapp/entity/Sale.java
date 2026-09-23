package com.pharma.pharmaapp.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sales")
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "pharmacy_id", nullable = false)
    private Pharmacy pharmacy;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private double unitPrice;

    @Column(nullable = false)
    private double unitCost;

    @Column(nullable = false)
    private double revenue;

    @Column(nullable = false)
    private double grossProfit;

    @Column(nullable = false)
    private LocalDateTime soldAt;

    public Sale() {
    }

    public Sale(Pharmacy pharmacy, Medicine medicine, int quantity, double unitPrice, double unitCost) {
        this.pharmacy = pharmacy;
        this.medicine = medicine;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.unitCost = unitCost;
        this.revenue = quantity * unitPrice;
        this.grossProfit = quantity * (unitPrice - unitCost);
        this.soldAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Pharmacy getPharmacy() { return pharmacy; }
    public Medicine getMedicine() { return medicine; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public double getUnitCost() { return unitCost; }
    public double getRevenue() { return revenue; }
    public double getGrossProfit() { return grossProfit; }
    public LocalDateTime getSoldAt() { return soldAt; }
    public void setSoldAt(LocalDateTime soldAt) { this.soldAt = soldAt; }
}

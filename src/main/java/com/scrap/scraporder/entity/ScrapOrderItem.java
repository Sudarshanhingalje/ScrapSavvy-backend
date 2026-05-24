package com.scrap.scraporder.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "scrap_order_items")
public class ScrapOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // MAIN ORDER ID
    @Column(name = "order_id")
    private Long scrapOrderId;

    // MATERIAL DETAILS
    private String materialType;

    private Double quantity;

    @Column(name = "price_per_kg")
    private Double pricePerKg;

    private Double totalPrice;

    private LocalDateTime createdAt;

    // =========================
    // AUTO TIMESTAMP
    // =========================
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();

        if (this.quantity == null) {
            this.quantity = 0.0;
        }

        if (this.pricePerKg == null) {
            this.pricePerKg = 0.0;
        }

        if (this.totalPrice == null) {
            this.totalPrice = 0.0;
        }
    }

    // =========================
    // GETTERS & SETTERS
    // =========================

    public Long getId() {
        return id;
    }

    public Long getScrapOrderId() {
        return scrapOrderId;
    }

    public void setScrapOrderId(Long scrapOrderId) {
        this.scrapOrderId = scrapOrderId;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getPricePerKg() {
        return pricePerKg;
    }

    public void setPricePerKg(Double pricePerKg) {
        this.pricePerKg = pricePerKg;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
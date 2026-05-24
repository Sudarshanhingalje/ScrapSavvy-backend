package com.scrap.inventory.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "scrap_prices")
public class ScrapPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long ownerId;

    private String materialType;

    @Column(nullable = false)
    private Double customerPrice;

    @Column(nullable = false)
    private Double companyPrice;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Automatically set timestamp on create/update
    @PrePersist
    public void onCreate() {
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public ScrapPrice() {}

    // ───── Getters ─────
    public Long getId() {
        return id;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public String getMaterialType() {
        return materialType;
    }

    public Double getCustomerPrice() {
        return customerPrice;
    }

    public Double getCompanyPrice() {
        return companyPrice;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // ───── Setters ─────
    public void setId(Long id) {
        this.id = id;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public void setCustomerPrice(Double customerPrice) {
        this.customerPrice = customerPrice;
    }

    public void setCompanyPrice(Double companyPrice) {
        this.companyPrice = companyPrice;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
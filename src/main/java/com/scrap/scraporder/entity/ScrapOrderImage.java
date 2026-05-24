package com.scrap.scraporder.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "scrap_order_images")
public class ScrapOrderImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // MAIN ORDER ID
    @Column(name = "order_id")
    private Long scrapOrderId;

    // IMAGE PATH OR URL
    private String imageUrl;

    private LocalDateTime createdAt;

    // =========================
    // AUTO TIMESTAMP
    // =========================
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
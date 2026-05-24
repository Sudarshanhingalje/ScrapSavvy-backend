package com.scrap.product.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.scrap.auth.entity.UserProfile;
import com.scrap.order.entity.OrderEntity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── which order this review belongs to ──────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "items", "userProfile"})
    private OrderEntity order;

    // ── which product is being reviewed ─────────────────────
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "images", "tags", "userProfile", "category"})
    private Product product;

    // ── who wrote the review ─────────────────────────────────
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_profile_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "user"})
    private UserProfile reviewer;

    // ── review data ──────────────────────────────────────────
    @Column(nullable = false)
    private Integer rating;           // 1–5

    @Column(columnDefinition = "TEXT")
    private String reviewText;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Boolean verified = true;  // purchased = always verified

    // ── lifecycle ────────────────────────────────────────────
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // ── getters / setters ────────────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public OrderEntity getOrder() { return order; }
    public void setOrder(OrderEntity order) { this.order = order; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public UserProfile getReviewer() { return reviewer; }
    public void setReviewer(UserProfile reviewer) { this.reviewer = reviewer; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getReviewText() { return reviewText; }
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Boolean getVerified() { return verified; }
    public void setVerified(Boolean verified) { this.verified = verified; }
}
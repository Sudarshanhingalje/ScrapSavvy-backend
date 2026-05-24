package com.scrap.product.dto;

import java.time.LocalDateTime;

public class ReviewResponse {

    private Long id;
    private Long orderId;
    private Long productId;
    private String productName;
    private String productImage;
    private Long reviewerProfileId;
    private String reviewerName;
    private Integer rating;
    private String reviewText;
    private Boolean verified;
    private LocalDateTime createdAt;

    // ── static factory ───────────────────────────────────────
    public static ReviewResponse from(com.scrap.product.entity.Review r) {
        ReviewResponse dto = new ReviewResponse();
        dto.id              = r.getId();
        dto.orderId         = r.getOrder().getOrderId();
        dto.productId       = r.getProduct().getProductId();
        dto.productName     = r.getProduct().getProductName();
        dto.productImage    = r.getProduct().getImages() != null && !r.getProduct().getImages().isEmpty()
                             ? r.getProduct().getImages().get(0).getImageUrl() : null;
        dto.reviewerProfileId = r.getReviewer().getUserProfileId();
        dto.reviewerName    = r.getReviewer().getName();
        dto.rating          = r.getRating();
        dto.reviewText      = r.getReviewText();
        dto.verified        = r.getVerified();
        dto.createdAt       = r.getCreatedAt();
        return dto;
    }

    // ── getters / setters ────────────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }

    public Long getReviewerProfileId() { return reviewerProfileId; }
    public void setReviewerProfileId(Long reviewerProfileId) { this.reviewerProfileId = reviewerProfileId; }

    public String getReviewerName() { return reviewerName; }
    public void setReviewerName(String reviewerName) { this.reviewerName = reviewerName; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getReviewText() { return reviewText; }
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }

    public Boolean getVerified() { return verified; }
    public void setVerified(Boolean verified) { this.verified = verified; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
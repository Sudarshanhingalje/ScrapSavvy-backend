package com.scrap.product.dto;

import java.util.List;

public class ReviewRequest {

    private Long orderId;
    private Integer rating;          // 1–5
    private String reviewText;
    private List<Long> productIds;   // one review per product in the order

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getReviewText() { return reviewText; }
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }

    public List<Long> getProductIds() { return productIds; }
    public void setProductIds(List<Long> productIds) { this.productIds = productIds; }
}
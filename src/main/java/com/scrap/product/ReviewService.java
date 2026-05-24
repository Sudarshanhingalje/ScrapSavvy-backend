package com.scrap.product;

import java.util.List;

import com.scrap.product.dto.ReviewRequest;
import com.scrap.product.dto.ReviewResponse;

public interface ReviewService {

    // Customer submits one review per product in the order
    List<ReviewResponse> submitReview(Long userProfileId, ReviewRequest request);

    // Public: get all reviews for a product
    List<ReviewResponse> getReviewsByProduct(Long productId);

    // Scrapyard owner: get all reviews for their products
    List<ReviewResponse> getReviewsForOwner(Long ownerProfileId);

    // Customer: get reviews they wrote
    List<ReviewResponse> getMyReviews(Long userProfileId);

    // Check if order already reviewed (so button becomes disabled)
    boolean hasReviewed(Long orderId, Long productId);
}
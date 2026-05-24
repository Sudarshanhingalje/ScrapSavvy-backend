package com.scrap.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.scrap.product.dto.ReviewRequest;
import com.scrap.product.dto.ReviewResponse;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // ── POST /api/reviews?userProfileId=26
    //    Body: { orderId, rating, reviewText, productIds }
    //    Customer submits a review after delivery
    @PostMapping
    public ResponseEntity<List<ReviewResponse>> submitReview(
            @RequestParam Long userProfileId,
            @RequestBody ReviewRequest request) {
        try {
            List<ReviewResponse> result = reviewService.submitReview(userProfileId, request);
            return ResponseEntity.ok(result);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ── GET /api/reviews/product/{productId}
    //    Public: all reviews for a product (shown on product detail page)
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewResponse>> getByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getReviewsByProduct(productId));
    }

    // ── GET /api/reviews/owner/{ownerProfileId}
    //    Scrapyard owner: see all reviews on their products
    @GetMapping("/owner/{ownerProfileId}")
    public ResponseEntity<List<ReviewResponse>> getByOwner(@PathVariable Long ownerProfileId) {
        return ResponseEntity.ok(reviewService.getReviewsForOwner(ownerProfileId));
    }

    // ── GET /api/reviews/my/{userProfileId}
    //    Customer: all reviews they have written
    @GetMapping("/my/{userProfileId}")
    public ResponseEntity<List<ReviewResponse>> getMyReviews(@PathVariable Long userProfileId) {
        return ResponseEntity.ok(reviewService.getMyReviews(userProfileId));
    }

    // ── GET /api/reviews/check?orderId=22&productId=97
    //    Returns { reviewed: true/false } — used to disable button if already reviewed
    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkReviewed(
            @RequestParam Long orderId,
            @RequestParam Long productId) {
        boolean reviewed = reviewService.hasReviewed(orderId, productId);
        return ResponseEntity.ok(Map.of("reviewed", reviewed));
    }
}
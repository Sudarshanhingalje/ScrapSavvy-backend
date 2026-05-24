package com.scrap.product;

import com.scrap.auth.entity.UserProfile;
import com.scrap.auth.repository.UserProfileRepository;
import com.scrap.common.enums.OrderStatus;
import com.scrap.order.entity.OrderEntity;
import com.scrap.order.repository.OrderRepository;
import com.scrap.product.dto.ReviewRequest;
import com.scrap.product.dto.ReviewResponse;
import com.scrap.product.entity.Product;
import com.scrap.product.entity.Review;
import com.scrap.product.repository.ProductRepository;
import com.scrap.product.repository.ReviewRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    // ── Submit review ─────────────────────────────────────────────────────────
    @Override
    public List<ReviewResponse> submitReview(Long userProfileId, ReviewRequest request) {

        // 1. validate rating
        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        // 2. load order and verify it belongs to this user and is DELIVERED
        OrderEntity order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found: " + request.getOrderId()));

        if (!order.getUserProfile().getUserProfileId().equals(userProfileId)) {
            throw new SecurityException("This order does not belong to you");
        }

        if (order.getOrderStatus() != OrderStatus.DELIVERED) {
            throw new IllegalStateException("You can only review delivered orders");
        }

        // 3. load reviewer
        UserProfile reviewer = userProfileRepository.findById(userProfileId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 4. save one Review per productId
        List<Long> productIds = request.getProductIds();
        List<ReviewResponse> saved = new ArrayList<>();

        for (Long productId : productIds) {
            // skip if already reviewed
            Optional<Review> existing = reviewRepository
                    .findByOrder_OrderIdAndProduct_ProductId(order.getOrderId(), productId);
            if (existing.isPresent()) {
                saved.add(ReviewResponse.from(existing.get()));
                continue;
            }

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

            Review review = new Review();
            review.setOrder(order);
            review.setProduct(product);
            review.setReviewer(reviewer);
            review.setRating(request.getRating());
            review.setReviewText(request.getReviewText());
            review.setVerified(true);

            saved.add(ReviewResponse.from(reviewRepository.save(review)));
        }

        return saved;
    }

    // ── Get reviews for a product (public) ───────────────────────────────────
    @Override
    public List<ReviewResponse> getReviewsByProduct(Long productId) {
        return reviewRepository.findByProduct_ProductIdOrderByCreatedAtDesc(productId)
                .stream()
                .map(ReviewResponse::from)
                .collect(Collectors.toList());
    }

    // ── Get reviews for scrapyard owner's products ────────────────────────────
    @Override
    public List<ReviewResponse> getReviewsForOwner(Long ownerProfileId) {
        return reviewRepository.findByProductOwner(ownerProfileId)
                .stream()
                .map(ReviewResponse::from)
                .collect(Collectors.toList());
    }

    // ── Get reviews written by the customer ──────────────────────────────────
    @Override
    public List<ReviewResponse> getMyReviews(Long userProfileId) {
        return reviewRepository.findByReviewer_UserProfileIdOrderByCreatedAtDesc(userProfileId)
                .stream()
                .map(ReviewResponse::from)
                .collect(Collectors.toList());
    }

    // ── Check if order+product already reviewed ──────────────────────────────
    @Override
    public boolean hasReviewed(Long orderId, Long productId) {
        return reviewRepository.findByOrder_OrderIdAndProduct_ProductId(orderId, productId).isPresent();
    }
}
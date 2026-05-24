package com.scrap.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scrap.product.entity.Review;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // all reviews for a single product (public product page)
    List<Review> findByProduct_ProductIdOrderByCreatedAtDesc(Long productId);

    // all reviews written by a customer
    List<Review> findByReviewer_UserProfileIdOrderByCreatedAtDesc(Long reviewerProfileId);

    // all reviews for a scrapyard owner (by their userProfileId)
    @Query("SELECT r FROM Review r WHERE r.product.userProfile.userProfileId = :ownerId ORDER BY r.createdAt DESC")
    List<Review> findByProductOwner(@Param("ownerId") Long ownerId);

    // check if customer already reviewed a specific product from a specific order
    Optional<Review> findByOrder_OrderIdAndProduct_ProductId(Long orderId, Long productId);

    // average rating for a product
    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.product.productId = :productId")
    Double avgRatingByProduct(@Param("productId") Long productId);

    // count reviews for a product
    long countByProduct_ProductId(Long productId);
}
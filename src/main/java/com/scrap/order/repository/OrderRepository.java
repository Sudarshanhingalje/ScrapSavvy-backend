package com.scrap.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scrap.order.entity.OrderEntity;

@Repository
public interface OrderRepository
        extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity>
    findByUserProfile_UserProfileIdOrderByCreatedAtDesc(
            Long userProfileId
    );
    @Query("SELECT o FROM OrderEntity o JOIN FETCH o.items WHERE o.userProfile.userProfileId = :userId ORDER BY o.createdAt DESC")
    List<OrderEntity> findOrdersWithItems(@Param("userId") Long userId);
    

    List<OrderEntity> findAllByOrderByCreatedAtDesc();
}
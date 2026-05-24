package com.scrap.order.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.scrap.order.entity.OrderItemEntity;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {

    @Modifying
    void deleteByProduct_ProductId(Long productId);

    boolean existsByProduct_ProductId(Long productId);
    
    
}
package com.scrap.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scrap.order.entity.OrderEntity;
import com.scrap.product.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByUserProfile_UserProfileIdOrderByCreatedAtDesc(Long userProfileId);

    List<Product> findByActiveTrueOrderByCreatedAtDesc();
    
   
}
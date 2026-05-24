package com.scrap.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scrap.product.entity.ProductTag;

@Repository
public interface ProductTagRepository extends JpaRepository<ProductTag, Long> {

    void deleteByProductProductId(Long productId);
}
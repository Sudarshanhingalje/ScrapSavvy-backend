package com.scrap.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scrap.product.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
}

package com.scrap.scraporder.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scrap.scraporder.entity.ScrapOrderImage;

public interface ScrapOrderImageRepository
        extends JpaRepository<ScrapOrderImage, Long> {

    List<ScrapOrderImage> findByScrapOrderId(Long scrapOrderId);
    
    
}
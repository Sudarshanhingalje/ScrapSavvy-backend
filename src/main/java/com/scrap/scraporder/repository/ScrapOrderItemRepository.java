package com.scrap.scraporder.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scrap.scraporder.entity.ScrapOrderItem;

public interface ScrapOrderItemRepository
        extends JpaRepository<ScrapOrderItem, Long> {

    List<ScrapOrderItem> findByScrapOrderId(Long scrapOrderId);
}
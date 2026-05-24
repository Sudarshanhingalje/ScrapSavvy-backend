package com.scrap.scraporder.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.scrap.scraporder.entity.ScrapOrder;

import java.util.List;

public interface ScrapOrderRepository extends JpaRepository<ScrapOrder, Long> {
    List<ScrapOrder> findByStatus(String status);

List<ScrapOrder> findByUserEmail(String userEmail);

List<ScrapOrder> findByOwnerId(Long ownerId);

List<ScrapOrder> findByCustomerIdOrderByIdDesc(
        Long customerId
);

List<ScrapOrder> findByOrderType(String orderType);

List<ScrapOrder> findByOrderTypeOrderByIdDesc(String orderType);

List<ScrapOrder> findByOwnerIdAndOrderType(Long ownerId, String orderType);
}
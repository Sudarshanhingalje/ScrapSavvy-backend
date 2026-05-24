package com.scrap.inventory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.scrap.inventory.entity.InventoryTransaction;

public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {

    @Query("SELECT SUM(t.totalAmount) FROM InventoryTransaction t WHERE t.ownerId = :ownerId AND t.type = 'REMOVE'")
    Double getTotalSales(Long ownerId);

    @Query("SELECT SUM(t.totalAmount) FROM InventoryTransaction t WHERE t.ownerId = :ownerId AND t.type = 'ADD'")
    Double getTotalPurchase(Long ownerId);
    
    List<InventoryTransaction> findByOwnerId(Long ownerId);
    
    List<InventoryTransaction> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);
}
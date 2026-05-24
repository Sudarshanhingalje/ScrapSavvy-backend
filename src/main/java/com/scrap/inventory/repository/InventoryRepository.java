package com.scrap.inventory.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scrap.inventory.entity.Inventory;
import com.scrap.inventory.entity.InventoryTransaction;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByOwnerIdAndMaterialType(Long ownerId, String materialType);

    List<Inventory> findByOwnerId(Long ownerId);
    
   

}
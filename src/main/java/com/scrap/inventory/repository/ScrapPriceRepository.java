package com.scrap.inventory.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scrap.inventory.entity.ScrapPrice;

public interface ScrapPriceRepository extends JpaRepository<ScrapPrice, Long> {

    Optional<ScrapPrice> findByOwnerIdAndMaterialType(Long ownerId, String materialType);

    List<ScrapPrice> findByOwnerId(Long ownerId);
}
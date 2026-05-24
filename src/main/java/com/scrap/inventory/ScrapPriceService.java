package com.scrap.inventory;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scrap.inventory.entity.ScrapPrice;
import com.scrap.inventory.repository.ScrapPriceRepository;

@Service
public class ScrapPriceService {

    @Autowired
    private ScrapPriceRepository repo;

    public ScrapPrice updatePrice(
            Long ownerId,
            String material,
            Double customerPrice,
            Double companyPrice
    ) {

        ScrapPrice price = repo
                .findByOwnerIdAndMaterialType(ownerId, material)
                .orElse(new ScrapPrice());

        price.setOwnerId(ownerId);

        price.setMaterialType(material);

        price.setCustomerPrice(
                customerPrice != null
                        ? customerPrice
                        : 0.0
        );

        price.setCompanyPrice(
                companyPrice != null
                        ? companyPrice
                        : 0.0
        );

        price.setUpdatedAt(LocalDateTime.now());

        return repo.save(price);
    }

    public Double getPrice(
            Long ownerId,
            String material,
            String orderType
    ) {

        ScrapPrice price = repo
                .findByOwnerIdAndMaterialType(ownerId, material)
                .orElseThrow(() ->
                        new RuntimeException("Price not set")
                );

        if ("CUSTOMER".equalsIgnoreCase(orderType)) {

            return price.getCustomerPrice();

        } else {

            return price.getCompanyPrice();
        }
    }

    public List<ScrapPrice> getOwnerPrices(Long ownerId) {

        return repo.findByOwnerId(ownerId);
    }

    public List<ScrapPrice> getAllPrices() {

        return repo.findAll();
    }
}
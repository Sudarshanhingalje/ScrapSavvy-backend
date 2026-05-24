package com.scrap.inventory;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.scrap.common.security.JwtTokenUtil;
import com.scrap.inventory.entity.Inventory;
import com.scrap.inventory.repository.InventoryRepository;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin
public class InventoryController {

    @Autowired
    private InventoryRepository inventoryRepo;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    // 🔒 ONLY AUTHENTICATED USERS CAN ACCESS
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    @GetMapping
    public List<Inventory> getInventory(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return List.of();
            }

            String jwt = token.substring(7);
            Long ownerId = jwtTokenUtil.extractUserId(jwt);

            if (ownerId == null) {
                return List.of();
            }

            return inventoryRepo.findByOwnerId(ownerId);

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
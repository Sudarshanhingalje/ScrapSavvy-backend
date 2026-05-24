package com.scrap.inventory;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scrap.common.security.JwtTokenUtil;
import com.scrap.inventory.entity.InventoryTransaction;
import com.scrap.inventory.repository.InventoryTransactionRepository;



@RestController
@RequestMapping("/api/transactions")
@CrossOrigin
public class InventoryTransactionController {

    @Autowired
    private InventoryTransactionRepository txRepo;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @GetMapping
    public List<InventoryTransaction> getTransactions(
            @RequestHeader(value = "Authorization", required = false) String token) {

        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return List.of();
            }

            token = token.substring(7);
            Long ownerId = jwtTokenUtil.extractUserId(token);

            if (ownerId == null) {
                return List.of();
            }

            return txRepo.findByOwnerId(ownerId); 
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
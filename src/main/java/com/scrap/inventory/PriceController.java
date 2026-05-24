package com.scrap.inventory;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scrap.inventory.entity.ScrapPrice;

@RestController
@RequestMapping("/api/prices")
@CrossOrigin
public class PriceController {

    @Autowired
    private ScrapPriceService service;

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // 🔥 ADD THIS

    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody Map<String, Object> req) {

        Long ownerId = Long.valueOf(req.get("ownerId").toString());
        String material = req.get("materialType").toString();

        Double customerPrice = Double.valueOf(req.get("customerPrice").toString());
        Double companyPrice = Double.valueOf(req.get("companyPrice").toString());

        ScrapPrice updated = service.updatePrice(ownerId, material, customerPrice, companyPrice);

        // 🔥 SEND LIVE UPDATE TO FRONTEND
        messagingTemplate.convertAndSend("/topic/prices", updated);

        return ResponseEntity.ok(updated);
    }
    
    // 👇 OWNER DASHBOARD (both rates)
    @GetMapping("/owner")
    public List<ScrapPrice> owner(@RequestParam Long ownerId) {
        return service.getOwnerPrices(ownerId);
    }

    // 👇 CUSTOMER DASHBOARD
    @GetMapping("/customer")
    public Double customerPrice(@RequestParam Long ownerId,
                               @RequestParam String material) {

        return service.getPrice(ownerId, material, "CUSTOMER");
    }

    // 👇 COMPANY DASHBOARD
    @GetMapping("/company")
    public Double companyPrice(@RequestParam Long ownerId,
                              @RequestParam String material) {

        return service.getPrice(ownerId, material, "COMPANY");
    }


    @GetMapping("/all")
    public ResponseEntity<List<ScrapPrice>> getAllPrices(@RequestParam Long ownerId) {
    	//ownerId = 2L; // fixed scrapyard owner

        List<ScrapPrice> prices = service.getOwnerPrices(ownerId);

        if (prices == null) {
            prices = List.of();
        }

        return ResponseEntity.ok(prices);
    }
}
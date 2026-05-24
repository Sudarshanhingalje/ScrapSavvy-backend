package com.scrap.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scrap.order.dto.DeliveryStatusUpdateRequest;
//import com.scrap.dto.AssignDriverRequest;
//import com.scrap.dto.DeliveryStatusUpdateRequest;
import com.scrap.order.entity.OrderEntity;
import com.scrap.scraporder.dto.AssignDriverRequest;

@RestController
@RequestMapping("/api/delivery")
@CrossOrigin(origins = "*")
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;

    // =========================
    // GET ALL DELIVERIES
    // =========================

    @GetMapping
    public List<OrderEntity> getAllDeliveries() {

        return deliveryService.getAllDeliveries();
    }

    // =========================
    // GET DELIVERY BY ID
    // =========================

    @GetMapping("/{id}")
    public OrderEntity getDeliveryById(
            @PathVariable Long id
    ) {

        return deliveryService.getDeliveryById(id);
    }

    // =========================
    // UPDATE DELIVERY STATUS
    // =========================

    @PutMapping("/status/{id}")
    public OrderEntity updateDeliveryStatus(
            @PathVariable Long id,
            @RequestBody DeliveryStatusUpdateRequest request
    ) {

        return deliveryService.updateDeliveryStatus(
                id,
                request.getDeliveryStatus()
        );
    }

    // =========================
    // ASSIGN DRIVER
    // =========================

    @PutMapping("/assign-driver/{id}")
    public OrderEntity assignDriver(
            @PathVariable Long id,
            @RequestBody AssignDriverRequest request
    ) {

        return deliveryService.assignDriver(
                id,
                request
        );
    }
}
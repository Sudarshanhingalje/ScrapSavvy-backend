package com.scrap.order;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scrap.common.enums.DeliveryStatus;
import com.scrap.common.enums.OrderStatus;
//import com.scrap.dto.AssignDriverRequest;
import com.scrap.order.entity.OrderEntity;
import com.scrap.order.repository.OrderRepository;
import com.scrap.scraporder.dto.AssignDriverRequest;

@Service
public class DeliveryService {

    @Autowired
    private OrderRepository orderRepository;

    // =========================
    // GET ALL DELIVERIES
    // =========================

    public List<OrderEntity> getAllDeliveries() {

        List<OrderEntity> orders = orderRepository.findAll();

        // FIX OLD NULL DATA
        for (OrderEntity order : orders) {

            if (order.getDeliveryStatus() == null) {

                if (order.getOrderStatus() == OrderStatus.DELIVERED) {
                    order.setDeliveryStatus(DeliveryStatus.DELIVERED);
                } else {
                    order.setDeliveryStatus(DeliveryStatus.PICKUP_PENDING);
                }

                orderRepository.save(order);
            }
        }

        return orderRepository.findAll();
    }

    // =========================
    // GET DELIVERY BY ID
    // =========================

    public OrderEntity getDeliveryById(Long orderId) {

        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    // =========================
    // UPDATE DELIVERY STATUS
    // =========================

    public OrderEntity updateDeliveryStatus(
            Long orderId,
            String deliveryStatus
    ) {

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        DeliveryStatus status =
                DeliveryStatus.valueOf(deliveryStatus);

        order.setDeliveryStatus(status);

        // AUTO UPDATE ORDER STATUS

        if (status == DeliveryStatus.DELIVERED) {

            order.setOrderStatus(OrderStatus.DELIVERED);
        }

        if (status == DeliveryStatus.FAILED) {

            order.setOrderStatus(OrderStatus.CANCELLED);
        }

        return orderRepository.save(order);
    }

    // =========================
    // ASSIGN DRIVER
    // =========================

    public OrderEntity assignDriver(
            Long orderId,
            AssignDriverRequest request
    ) {

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setDriverName(request.getDriverName());

        order.setDriverPhone(request.getDriverPhone());

        order.setVehicleNumber(request.getVehicleNumber());

        order.setDeliveryPartner(request.getDeliveryPartner());

        order.setAssignedAt(LocalDateTime.now());

        order.setEstimatedDelivery(
                LocalDateTime.now().plusDays(3)
        );

        // TRACKING ID

        if (
                order.getTrackingId() == null
                        || order.getTrackingId().isEmpty()
        ) {

            order.setTrackingId(
                    "TRK-2026-" + order.getOrderId()
            );
        }

        // DEFAULT STATUS

        order.setDeliveryStatus(
                DeliveryStatus.PICKUP_PENDING
        );

        return orderRepository.save(order);
    }
}
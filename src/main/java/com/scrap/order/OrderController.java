package com.scrap.order;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.scrap.order.dto.PlaceOrderRequest;
import com.scrap.order.repository.OrderRepository;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    public OrderController(OrderService orderService,
                           OrderRepository orderRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
    }

    @PostMapping("/place")
    public ResponseEntity<?> placeOrder(@RequestBody PlaceOrderRequest request) {
        return ResponseEntity.ok(orderService.placeOrder(request));
    }

    @GetMapping("/myorders/{userId}")
    public ResponseEntity<?> getMyOrders(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getMyOrders(userId));
    }

    @PutMapping("/status/{orderId}")
    public ResponseEntity<?> updateStatus(@PathVariable Long orderId,
                                          @RequestParam String status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
    }

    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId));
    }

    @PostMapping("/buyagain/{orderId}")
    public ResponseEntity<?> buyAgain(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.buyAgain(orderId));
    }

    @GetMapping("/invoice/{orderId}")
    public ResponseEntity<?> getInvoice(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderInvoice(orderId));
    }
}
package com.scrap.scraporder;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scrap.auth.repository.UserRepository;
import com.scrap.common.security.JwtTokenUtil;
import com.scrap.payment.dto.PaymentDTO;
//import com.scrap.dto.PaymentDTO;
import com.scrap.scraporder.entity.ScrapOrder;
import com.scrap.scraporder.repository.ScrapOrderImageRepository;
import com.scrap.scraporder.repository.ScrapOrderRepository;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/scraporders")
@CrossOrigin("*")
public class ScrapOrderController {

    @Autowired
    private ScrapOrderService service;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private ScrapOrderRepository scrapOrderRepository;
    
    @Autowired
    private ScrapOrderImageRepository imageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // =========================
    // GET ALL ORDERS
    // =========================
//    @GetMapping("/all")
//    public ResponseEntity<?> getAllOrders() {
//        return ResponseEntity.ok(service.getAllOrders());
//    }
    
    @GetMapping("/company/all")
    public ResponseEntity<?> getCompanyOrders() {
        return ResponseEntity.ok(
                scrapOrderRepository.findByOrderTypeOrderByIdDesc("COMPANY")
        );
    }
    
    @GetMapping("/customer/all")
    public ResponseEntity<?> getCustomerSellOrders() {

        List<ScrapOrder> orders =
                scrapOrderRepository.findByOrderTypeOrderByIdDesc("CUSTOMER");

        for (ScrapOrder order : orders) {

            order.setImages(
                    imageRepository.findByScrapOrderId(order.getId())
            );
        }

        return ResponseEntity.ok(orders);
    }

    // =========================
    // OWNER ORDERS
    // =========================
    @GetMapping("/owner")
    public ResponseEntity<List<ScrapOrder>> getOrdersByOwner(
            @RequestHeader(value = "Authorization", required = false) String token) {

        try {

            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.ok(List.of());
            }

            token = token.substring(7);

            Long ownerId = jwtTokenUtil.extractUserId(token);

            if (ownerId == null) {
                return ResponseEntity.ok(List.of());
            }

            List<ScrapOrder> orders =
                    scrapOrderRepository.findByOwnerId(ownerId);

            return ResponseEntity.ok(orders);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.ok(List.of());
        }
    }

    // =========================
    // CREATE ORDER
    // =========================
    @PostMapping("/create")
    public ResponseEntity<?> createOrder(
            @RequestBody ScrapOrder order,
            HttpServletRequest request) {

        try {

            String header = request.getHeader("Authorization");

            if (header == null || !header.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Unauthorized");
            }

            String token = header.substring(7);

            String email = jwtTokenUtil.getUsernameFromToken(token);

            Long loggedInUserId =
                    jwtTokenUtil.extractUserId(token);

            order.setCustomerId(loggedInUserId);

            order.setUserEmail(email);

            // ONLY ONE SCRAPYARD OWNER
            order.setOwnerId(2L);
            
            order.setOrderType("COMPANY");

            order.setStatus("PENDING");

            ScrapOrder savedOrder =
                    service.createOrder(order);

            // LIVE UPDATE
            messagingTemplate.convertAndSend(
                    "/topic/orders/" + order.getOwnerId(),
                    scrapOrderRepository.findByOwnerId(
                            order.getOwnerId()
                    )
            );

            return ResponseEntity.ok(savedOrder);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(500)
                    .body(e.getMessage());
        }
    }

    // =========================
    // UPDATE STATUS
    // =========================
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        try {

            ScrapOrder order =
                    service.getOrderById(id);

            String current =
                    order.getStatus();

            // VALID FLOW

            if ("ACCEPTED".equals(status)
                    && !"PENDING".equals(current)) {

                return ResponseEntity
                        .badRequest()
                        .body("Order must be PENDING to accept");
            }

            if ("OUT_FOR_PICKUP".equals(status)
                    && !"SCHEDULED".equals(current)) {

                return ResponseEntity
                        .badRequest()
                        .body("Order must be SCHEDULED first");
            }

            if ("COMPLETED".equals(status)
                    && !"OUT_FOR_PICKUP".equals(current)) {

                return ResponseEntity
                        .badRequest()
                        .body("Order must be OUT_FOR_PICKUP first");
            }

            ScrapOrder updated =
                    service.updateStatus(id, status);

            // LIVE UPDATE
            messagingTemplate.convertAndSend(
                    "/topic/orders/" + order.getOwnerId(),
                    scrapOrderRepository.findByOwnerId(
                            order.getOwnerId()
                    )
            );

            return ResponseEntity.ok(updated);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // =========================
    // SCHEDULE PICKUP
    // =========================
    @PutMapping("/{id}/schedule")
    public ResponseEntity<?> schedulePickup(
            @PathVariable Long id,
            @RequestBody Map<String, String> data) {

        try {

            ScrapOrder order =
                    service.getOrderById(id);

            if (!"ACCEPTED".equals(order.getStatus())) {

                return ResponseEntity
                        .badRequest()
                        .body("Order must be ACCEPTED first");
            }

            order.setPickupDate(
                    LocalDate.parse(data.get("pickupDate"))
            );

            order.setPickupTime(
                    data.get("pickupTime")
            );

            order.setPaymentMethod(
                    data.get("paymentMethod")
            );

            order.setAssignedDriver(
                    data.get("assignedDriver")
            );

            order.setDriverContactNo(
                    data.get("driverContactNo")
            );

            order.setStatus("SCHEDULED");

            ScrapOrder updated =
                    service.save(order);

            // LIVE UPDATE
            messagingTemplate.convertAndSend(
                    "/topic/orders/" + order.getOwnerId(),
                    scrapOrderRepository.findByOwnerId(
                            order.getOwnerId()
                    )
            );

            return ResponseEntity.ok(updated);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(500)
                    .body(e.getMessage());
        }
    }

    // =========================
    // DELETE
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(
            @PathVariable Long id) {

        service.deleteOrder(id);

        return ResponseEntity.ok("Deleted");
    }

    // =========================
    // MY ORDERS
    // =========================
    @GetMapping("/myorders")
    public ResponseEntity<?> getMyOrders(
            HttpServletRequest request) {

        String token =
                request.getHeader("Authorization")
                        .substring(7);

        String email =
                jwtTokenUtil.getUsernameFromToken(token);

        return ResponseEntity.ok(
                service.getOrdersByUserEmail(email)
        );
    }

    // =========================
    // PAYMENT
    // =========================
    @PutMapping("/{id}/paymentsuccess")
    public ResponseEntity<?> updatePayment(
            @PathVariable Long id,
            @RequestBody PaymentDTO req) {

        return ResponseEntity.ok(
                service.updatePayment(id, req)
        );
    }
}
package com.scrap.order.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.scrap.auth.entity.UserProfile;
import com.scrap.common.enums.DeliveryStatus;
import com.scrap.common.enums.OrderStatus;
import com.scrap.common.enums.PaymentStatus;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    // =========================
    // USER
    // =========================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_user_profile_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private UserProfile userProfile;

    // =========================
    // ORDER AMOUNTS
    // =========================

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(precision = 10, scale = 2)
    private BigDecimal cgst;

    @Column(precision = 10, scale = 2)
    private BigDecimal sgst;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    // =========================
    // ORDER STATUS
    // =========================

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // =========================
    // RAZORPAY
    // =========================

    @Column(name = "razorpay_payment_id")
    private String razorpayPaymentId;

    @Column(name = "razorpay_order_id")
    private String razorpayOrderId;

    // =========================
    // DELIVERY MANAGEMENT
    // =========================

    @Column(name = "tracking_id")
    private String trackingId;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status")
    private DeliveryStatus deliveryStatus;

    @Column(name = "estimated_delivery")
    private LocalDateTime estimatedDelivery;

    @Column(name = "delivery_partner")
    private String deliveryPartner;

    @Column(name = "driver_name")
    private String driverName;

    @Column(name = "driver_phone")
    private String driverPhone;

    @Column(name = "vehicle_number")
    private String vehicleNumber;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    // =========================
    // ORDER ITEMS
    // =========================

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private List<OrderItemEntity> items = new ArrayList<>();

    // =========================
    // AUTO TIMESTAMP
    // =========================

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();

        if (this.deliveryStatus == null) {
            this.deliveryStatus = DeliveryStatus.PICKUP_PENDING;
        }

        if (this.trackingId == null || this.trackingId.isEmpty()) {
            this.trackingId = "TRK-" + System.currentTimeMillis();
        }
    }

    // =========================
    // GETTERS & SETTERS
    // =========================

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public UserProfile getUserProfile() { return userProfile; }
    public void setUserProfile(UserProfile userProfile) { this.userProfile = userProfile; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getCgst() { return cgst; }
    public void setCgst(BigDecimal cgst) { this.cgst = cgst; }

    public BigDecimal getSgst() { return sgst; }
    public void setSgst(BigDecimal sgst) { this.sgst = sgst; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public OrderStatus getOrderStatus() { return orderStatus; }
    public void setOrderStatus(OrderStatus orderStatus) { this.orderStatus = orderStatus; }

    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getRazorpayPaymentId() { return razorpayPaymentId; }
    public void setRazorpayPaymentId(String razorpayPaymentId) { this.razorpayPaymentId = razorpayPaymentId; }

    public String getRazorpayOrderId() { return razorpayOrderId; }
    public void setRazorpayOrderId(String razorpayOrderId) { this.razorpayOrderId = razorpayOrderId; }

    public String getTrackingId() { return trackingId; }
    public void setTrackingId(String trackingId) { this.trackingId = trackingId; }

    public DeliveryStatus getDeliveryStatus() { return deliveryStatus; }
    public void setDeliveryStatus(DeliveryStatus deliveryStatus) { this.deliveryStatus = deliveryStatus; }

    public LocalDateTime getEstimatedDelivery() { return estimatedDelivery; }
    public void setEstimatedDelivery(LocalDateTime estimatedDelivery) { this.estimatedDelivery = estimatedDelivery; }

    public String getDeliveryPartner() { return deliveryPartner; }
    public void setDeliveryPartner(String deliveryPartner) { this.deliveryPartner = deliveryPartner; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public String getDriverPhone() { return driverPhone; }
    public void setDriverPhone(String driverPhone) { this.driverPhone = driverPhone; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public LocalDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; }

    public List<OrderItemEntity> getItems() { return items; }
    public void setItems(List<OrderItemEntity> items) { this.items = items; }
}
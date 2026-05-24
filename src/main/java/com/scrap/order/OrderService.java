package com.scrap.order;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scrap.auth.entity.UserProfile;
import com.scrap.auth.repository.UserProfileRepository;
import com.scrap.common.enums.OrderStatus;
import com.scrap.common.enums.PaymentStatus;
import com.scrap.customer.dto.CustomerDTO;
import com.scrap.order.dto.OrderInvoiceDTO;
import com.scrap.order.dto.OrderItemDTO;
import com.scrap.order.dto.OrderItemRequest;
import com.scrap.order.dto.OrderItemResponse;
import com.scrap.order.dto.OrderResponse;
import com.scrap.order.dto.PlaceOrderRequest;
//import com.scrap.dto.CustomerDTO;
//import com.scrap.dto.OrderInvoiceDTO;
//import com.scrap.dto.OrderItemDTO;
//import com.scrap.dto.OrderItemRequest;
//import com.scrap.dto.OrderItemResponse;
//import com.scrap.dto.OrderResponse;
//import com.scrap.dto.PlaceOrderRequest;
import com.scrap.order.entity.OrderEntity;
import com.scrap.order.entity.OrderItemEntity;
import com.scrap.order.repository.OrderItemRepository;
import com.scrap.order.repository.OrderRepository;
import com.scrap.product.entity.Product;
import com.scrap.product.repository.ProductImageRepository;
import com.scrap.product.repository.ProductRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserProfileRepository userProfileRepository;
    

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        UserProfileRepository userProfileRepository,
                        ProductImageRepository productImageRepository, OrderItemRepository orderItemRepository) {

        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userProfileRepository = userProfileRepository;
       
    }

    // ================= PLACE ORDER =================
    @Transactional
    public OrderResponse placeOrder(PlaceOrderRequest request) {

        UserProfile userProfile = userProfileRepository
                .findById(request.getUserProfileId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        OrderEntity order = new OrderEntity();
        order.setUserProfile(userProfile);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PAID);
        order.setItems(new ArrayList<>());

        List<OrderItemResponse> responseItems = new ArrayList<>();

        for (OrderItemRequest item : request.getItems()) {

            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if (product.getQuantity() < item.getQty()) {
                throw new RuntimeException(product.getProductName() + " out of stock");
            }

            product.setQuantity(product.getQuantity() - item.getQty());
            productRepository.save(product);

            OrderItemEntity orderItem = new OrderItemEntity();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(item.getQty());
            orderItem.setPriceAtPurchase(product.getPrice());
            order.getItems().add(orderItem);

            OrderItemResponse res = new OrderItemResponse();
            res.setProductId(product.getProductId());
            res.setProductName(product.getProductName());
            res.setQuantity(item.getQty());
            res.setPriceAtPurchase(product.getPrice());
            responseItems.add(res);
        }

        // ✅ USE VALUES SENT FROM FRONTEND — don't recalculate
        order.setSubtotal(request.getSubtotal());
        order.setCgst(request.getCgst());
        order.setSgst(request.getSgst());
        order.setTotalAmount(request.getTotalAmount()); // ₹25.96 ✅

        // Razorpay IDs
        order.setRazorpayPaymentId(request.getRazorpayPaymentId());
        order.setRazorpayOrderId(request.getRazorpayOrderId());

        OrderEntity savedOrder = orderRepository.save(order);

        OrderResponse response = new OrderResponse();
        response.setOrderId(savedOrder.getOrderId());
        response.setUserId(userProfile.getUserProfileId());
        response.setSubtotal(savedOrder.getSubtotal());
        response.setCgst(savedOrder.getCgst());
        response.setSgst(savedOrder.getSgst());
        response.setTotalAmount(savedOrder.getTotalAmount());
        response.setOrderStatus(savedOrder.getOrderStatus().name());
        response.setPaymentStatus(savedOrder.getPaymentStatus().name());
        response.setRazorpayPaymentId(savedOrder.getRazorpayPaymentId());
        response.setCreatedAt(savedOrder.getCreatedAt());
        response.setItems(responseItems);

        return response;
    }
    // ================= GET MY ORDERS =================
    public List<OrderResponse> getMyOrders(Long userId) {

        List<OrderEntity> orders =
                orderRepository.findByUserProfile_UserProfileIdOrderByCreatedAtDesc(userId);

        return orders.stream().map(order -> {

            OrderResponse response = new OrderResponse();

            response.setOrderId(order.getOrderId());
            response.setUserId(order.getUserProfile().getUserProfileId());
            response.setTotalAmount(order.getTotalAmount());
            response.setOrderStatus(order.getOrderStatus().name());
            response.setPaymentStatus(order.getPaymentStatus().name());
            response.setPaymentId("N/A");
            response.setCreatedAt(order.getCreatedAt());

            List<OrderItemResponse> items = order.getItems().stream().map(item -> {

                Product product = item.getProduct();

                OrderItemResponse r = new OrderItemResponse();
                r.setProductId(product.getProductId());
                r.setProductName(product.getProductName());
                r.setQuantity(item.getQuantity());
                r.setPriceAtPurchase(item.getPriceAtPurchase());

                if (product.getUserProfile() != null) {
                    r.setSellerUserId(product.getUserProfile().getUserProfileId());
                }

                if (product.getImages() != null && !product.getImages().isEmpty()) {
                    r.setProductImage(product.getImages().get(0).getImageUrl());
                }

                return r;

            }).collect(Collectors.toList());

            response.setItems(items);

            return response;

        }).collect(Collectors.toList());
    }

    // ================= UPDATE ORDER STATUS =================
    public String updateOrderStatus(Long orderId, String status) {

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());
        if(status.equalsIgnoreCase("REFUNDED")) {

            order.setPaymentStatus(PaymentStatus.REFUNDED);

        }
        order.setOrderStatus(newStatus);
        if(newStatus == OrderStatus.REJECTED) {

            order.setPaymentStatus(
                PaymentStatus.REFUND_PENDING
            );
        }
        orderRepository.save(order);

        return "Order status updated to " + status;
    }

    // ================= CANCEL ORDER =================
    @Transactional
    public String cancelOrder(Long orderId) {

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Order already cancelled");
        }

        for (OrderItemEntity item : order.getItems()) {

            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() + item.getQuantity());
            productRepository.save(product);
        }

        order.setOrderStatus(OrderStatus.CANCELLED);

        if(order.getPaymentStatus() == PaymentStatus.PAID) {

            order.setPaymentStatus(PaymentStatus.REFUND_PENDING);

        }
        orderRepository.save(order);

        return "Order cancelled successfully";
    }

    // ================= BUY AGAIN =================
    public List<OrderItemResponse> buyAgain(Long orderId) {

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return order.getItems().stream().map(item -> {

            Product product = item.getProduct();

            OrderItemResponse res = new OrderItemResponse();
            res.setProductId(product.getProductId());
            res.setProductName(product.getProductName());
            res.setQuantity(item.getQuantity());
            res.setPriceAtPurchase(product.getPrice());

            if (product.getUserProfile() != null) {
                res.setSellerUserId(product.getUserProfile().getUserProfileId());
            }

            if (product.getImages() != null && !product.getImages().isEmpty()) {
                res.setProductImage(product.getImages().get(0).getImageUrl());
            }

            return res;

        }).collect(Collectors.toList());
    }
    
    
    
    public OrderInvoiceDTO getOrderInvoice(Long orderId) {

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        UserProfile profile = order.getUserProfile();

        if (profile == null) {
            throw new RuntimeException("User profile not attached to order");
        }

        List<OrderItemDTO> items = order.getItems().stream().map(item -> {

            Product product = item.getProduct();

            OrderItemDTO dto = new OrderItemDTO();
            dto.setProductId(product.getProductId());
            dto.setProductName(product.getProductName());
            dto.setQuantity(item.getQuantity());

            double price = item.getPriceAtPurchase() != null
                    ? item.getPriceAtPurchase()
                    : product.getPrice();

            dto.setPrice(price);

            dto.setImage(
                    product.getImages() != null && !product.getImages().isEmpty()
                            ? product.getImages().get(0).getImageUrl()
                            : null
            );

            dto.setTotal(price * item.getQuantity());

            return dto;

        }).collect(Collectors.toList());

        double subtotal = items.stream().mapToDouble(OrderItemDTO::getTotal).sum();
        double gst = subtotal * 0.18;

        CustomerDTO customer = new CustomerDTO();

        customer.setUserId(profile.getUserProfileId());
        customer.setName(profile.getName() != null ? profile.getName() : "—");

        // ✅ FIXED EMAIL
        customer.setEmail(profile.getEmailId() != null ? profile.getEmailId() : "—");

        customer.setMobile(profile.getMobile() != null ? profile.getMobile() : "—");

        // ✅ FIXED ADDRESS
        customer.setAddress(
                profile.getCompanyAddress() != null
                        ? profile.getCompanyAddress()
                        : "—"
        );

        OrderInvoiceDTO dto = new OrderInvoiceDTO();

        dto.setOrderId(order.getOrderId());
        dto.setStatus(order.getOrderStatus().name());
        dto.setCreatedAt(order.getCreatedAt());

        dto.setCustomer(customer);
        dto.setItems(items);

        dto.setSubtotal(subtotal);
        dto.setGst(gst);
        dto.setTotal(subtotal + gst);

        dto.setPaymentStatus(
                order.getPaymentStatus() != null
                        ? order.getPaymentStatus().name()
                        : "PENDING"
        );

        return dto;
    }
}
package com.scrap.scraporder;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//import com.scrap.dto.PaymentDTO;
import com.scrap.inventory.InventoryServiceImpl;
import com.scrap.inventory.ScrapPriceService;
import com.scrap.payment.dto.PaymentDTO;
import com.scrap.scraporder.entity.ScrapOrder;
import com.scrap.scraporder.entity.ScrapOrderItem;
import com.scrap.scraporder.repository.ScrapOrderItemRepository;
import com.scrap.scraporder.repository.ScrapOrderRepository;

@Service
public class ScrapOrderServiceImpl implements ScrapOrderService {

    @Autowired
    private ScrapOrderRepository repository;

    @Autowired
    private ScrapOrderItemRepository itemRepository;

    @Autowired
    private InventoryServiceImpl inventoryService;

    @Autowired
    private ScrapPriceService priceService;

    // =========================
    // CREATE ORDER
    // =========================
    @Override
    public ScrapOrder createOrder(ScrapOrder order) {

        if (order.getOrderType() == null) {
            throw new RuntimeException("Order type required");
        }

        if (order.getOwnerId() == null) {
            throw new RuntimeException("Owner ID missing");
        }

        Double price = priceService.getPrice(
                order.getOwnerId(),
                order.getScrapType(),
                order.getOrderType()
        );

        order.setPricePerKg(price);

        double subtotal = price * order.getQuantity();

        double gst = subtotal * 0.18;

        double finalAmount = subtotal + gst;

        order.setTotalPrice(finalAmount);

        order.setStatus("PENDING");

        return repository.save(order);
    }

    // =========================
    // UPDATE STATUS
    // =========================
    @Transactional
    @Override
    public ScrapOrder updateStatus(Long id, String status) {

        ScrapOrder order = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // prevent duplicate complete/reject
        if ("COMPLETED".equalsIgnoreCase(order.getStatus()) ||
            "REJECTED".equalsIgnoreCase(order.getStatus())) {

            throw new RuntimeException("Order already finalized");
        }

        status = status.toUpperCase();

        // =========================
        // COMPANY STOCK VALIDATION
        // =========================
        if ("ACCEPTED".equals(status)
                && "COMPANY".equalsIgnoreCase(order.getOrderType())) {

            boolean ok = inventoryService.canFulfillOrder(
                    order.getOwnerId(),
                    order.getScrapType(),
                    order.getQuantity()
            );

            if (!ok) {

                order.setStatus("REJECTED");

                repository.save(order);

                throw new RuntimeException("Low stock");
            }
        }

        // =========================
        // COMPLETED
        // =========================
        if ("COMPLETED".equals(status)) {

            // payment required
            if (!"PAID".equalsIgnoreCase(order.getPaymentStatus())) {

                throw new RuntimeException("Payment not completed");
            }

            // =========================
            // COMPANY ORDER
            // REMOVE STOCK
            // =========================
            if ("COMPANY".equalsIgnoreCase(order.getOrderType())) {

                inventoryService.removeInventory(
                        order.getOwnerId(),
                        order.getScrapType(),
                        order.getQuantity(),
                        order.getPricePerKg(),
                        order.getId()
                );
            }

            // =========================
            // CUSTOMER SELL ORDER
            // ADD STOCK
            // =========================
            else {

                List<ScrapOrderItem> items =
                        itemRepository.findByScrapOrderId(order.getId());

                for (ScrapOrderItem item : items) {

                    inventoryService.addInventory(
                            order.getOwnerId(),
                            item.getMaterialType(),
                            item.getQuantity(),
                            item.getPricePerKg(),
                            order.getId()
                    );
                }
            }
        }

        order.setStatus(status);

        return repository.save(order);
    }

    // =========================
    // DELETE
    // =========================
    @Override
    public void deleteOrder(Long id) {

        repository.deleteById(id);
    }

    // =========================
    // GET USER ORDERS
    // =========================
    @Override
    public List<ScrapOrder> getOrdersByUserEmail(String email) {

        return repository.findByUserEmail(email);
    }

    // =========================
    // GET ORDER
    // =========================
    @Override
    public ScrapOrder getOrderById(Long id) {

        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    // =========================
    // SAVE
    // =========================
    @Override
    public ScrapOrder save(ScrapOrder order) {

        return repository.save(order);
    }

    // =========================
    // PAYMENT
    // =========================
    @Transactional
    @Override
    public ScrapOrder updatePayment(Long id, PaymentDTO dto) {

        ScrapOrder order = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setPaymentMethod(dto.getPaymentMethod());

        order.setPaymentStatus(dto.getPaymentStatus());

        order.setPaymentId(dto.getPaymentId());

        order.setPaidAmount(
                dto.getPaidAmount() != null
                        ? dto.getPaidAmount()
                        : order.getTotalPrice()
        );

        return repository.save(order);
    }

    // =========================
    // GET ALL
    // =========================
    @Override
    public List<ScrapOrder> getAllOrders() {

        return repository.findAll();
    }
}
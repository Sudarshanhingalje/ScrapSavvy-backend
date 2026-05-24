package com.scrap.scraporder;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

//import com.scrap.dto.CustomerSellItemDTO;
//import com.scrap.dto.CustomerSellOrderRequest;
import com.scrap.inventory.ScrapPriceService;
import com.scrap.scraporder.dto.CustomerSellItemDTO;
import com.scrap.scraporder.dto.CustomerSellOrderRequest;
import com.scrap.scraporder.entity.ScrapOrder;
import com.scrap.scraporder.entity.ScrapOrderImage;
import com.scrap.scraporder.entity.ScrapOrderItem;
import com.scrap.scraporder.repository.ScrapOrderImageRepository;
import com.scrap.scraporder.repository.ScrapOrderItemRepository;
import com.scrap.scraporder.repository.ScrapOrderRepository;
@Service
public class CustomerSellServiceImpl implements CustomerSellService {

    @Autowired
    private ScrapOrderRepository scrapOrderRepository;

    @Autowired
    private ScrapOrderItemRepository itemRepository;

    @Autowired
    private ScrapPriceService scrapPriceService;

    @Autowired
    private ScrapOrderImageRepository imageRepository;

    @Override
    public ScrapOrder createCustomerSellOrder(
            CustomerSellOrderRequest request,
            Long customerId,
            String email,
            MultipartFile[] images
    ) {

        ScrapOrder order = new ScrapOrder();

        order.setCustomerId(customerId);
        order.setUserEmail(email);

        if (request.getCustomerName() != null) {
            order.setCustomerName(request.getCustomerName());
        }

        order.setOwnerId(2L);

        order.setOrderType("CUSTOMER");

        order.setPricePerKg(null);

        order.setPickupAddress(request.getPickupAddress());

        order.setContactNo(request.getContactNo());

        order.setPickupDate(request.getPickupDate());

        order.setPickupTime(request.getPickupTime());

        order.setStatus("PENDING");

        order.setPaymentMethod("COD");

        order.setPaymentStatus("PENDING");

        order.setPaidAmount(0.0);

        // IMPORTANT
//        order.setInventoryUpdated("NO");

        List<CustomerSellItemDTO> items = request.getItems();

        double grandTotal = 0.0;

        // ================= MATERIAL SUMMARY =================

        String materials = items.stream()
                .map(CustomerSellItemDTO::getMaterialType)
                .distinct()
                .reduce((a, b) -> a + ", " + b)
                .orElse("");

        order.setScrapType(materials);

        // ================= TOTAL QUANTITY =================

        double totalQty = items.stream()
                .mapToDouble(CustomerSellItemDTO::getQuantity)
                .sum();

        order.setQuantity(totalQty);

        // ================= SAVE ORDER =================

        ScrapOrder savedOrder = scrapOrderRepository.save(order);

        // ================= SAVE ITEMS =================

        for (CustomerSellItemDTO dto : items) {

            Double price = scrapPriceService.getPrice(
                    2L,
                    dto.getMaterialType(),
                    "CUSTOMER"
            );

            grandTotal += price * dto.getQuantity();

            ScrapOrderItem item = new ScrapOrderItem();

            item.setScrapOrderId(savedOrder.getId());

            item.setMaterialType(dto.getMaterialType());

            item.setQuantity(dto.getQuantity());

            item.setPricePerKg(price);

            item.setTotalPrice(price * dto.getQuantity());

            itemRepository.save(item);
        }

        // ================= UPDATE TOTAL =================

        savedOrder.setTotalPrice(grandTotal);

        scrapOrderRepository.save(savedOrder);

     // ================= SAVE IMAGES =================

        if (images != null) {

            for (MultipartFile file : images) {

                String fileName = System.currentTimeMillis()
                        + "_" + file.getOriginalFilename();

                String uploadDir = "uploads/";

                java.io.File dir = new java.io.File(uploadDir);

                if (!dir.exists()) {
                    dir.mkdirs();
                }

                java.nio.file.Path path =
                        java.nio.file.Paths.get(uploadDir + fileName);

                try {
                    file.transferTo(path);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ScrapOrderImage image = new ScrapOrderImage();

                image.setScrapOrderId(savedOrder.getId());

                image.setImageUrl("/uploads/" + fileName);

                imageRepository.save(image);
            }
        }

        return savedOrder;
    }

    @Override
    public List<ScrapOrder> getOrdersByCustomer(Long customerId) {

        return scrapOrderRepository
                .findByCustomerIdOrderByIdDesc(customerId);
    }
}

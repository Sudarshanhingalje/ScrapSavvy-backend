package com.scrap.scraporder;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.scrap.scraporder.dto.CustomerSellOrderRequest;
//import com.scrap.dto.CustomerSellOrderRequest;
import com.scrap.scraporder.entity.ScrapOrder;

public interface CustomerSellService {

    ScrapOrder createCustomerSellOrder(
            CustomerSellOrderRequest request,
            Long customerId,
            String email,
            MultipartFile[] images
    );
    
    List<ScrapOrder> getOrdersByCustomer(Long customerId);
    
}
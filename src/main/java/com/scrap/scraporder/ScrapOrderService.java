package com.scrap.scraporder;

import java.util.List;

import com.scrap.payment.dto.PaymentDTO;
//import com.scrap.dto.PaymentDTO;
import com.scrap.scraporder.entity.ScrapOrder;

public interface ScrapOrderService {

    List<ScrapOrder> getAllOrders();

    ScrapOrder createOrder(ScrapOrder order);

    ScrapOrder updateStatus(Long id, String status);

    void deleteOrder(Long id);
    
    List<ScrapOrder> getOrdersByUserEmail(String email);
    
    ScrapOrder getOrderById(Long id);
    
    ScrapOrder save(ScrapOrder order) ;
    
    ScrapOrder updatePayment(Long id, PaymentDTO paymentDTO);
   
}
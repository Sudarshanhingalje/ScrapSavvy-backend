package com.scrap.scraporder;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.scrap.common.security.JwtTokenUtil;
import com.scrap.scraporder.dto.CustomerSellOrderRequest;
//import com.scrap.dto.CustomerSellOrderRequest;
import com.scrap.scraporder.entity.ScrapOrder;
import com.scrap.scraporder.repository.ScrapOrderImageRepository;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/customersell")
@CrossOrigin("*")
public class CustomerSellController {

    @Autowired
    private CustomerSellService customerSellService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Autowired
    private ScrapOrderImageRepository imageRepository;

    @PostMapping(
            value = "/create",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> createSellOrder(

            @RequestPart("order")
            CustomerSellOrderRequest request,

            @RequestPart(value = "images", required = false)
            MultipartFile[] images,

            HttpServletRequest http
    ) {

        try {

            String authHeader = http.getHeader("Authorization");

            String email = null;
            Long customerId = null;

            // OPTIONAL JWT
            if (authHeader != null && authHeader.startsWith("Bearer ")) {

                String token = authHeader.substring(7);

                customerId =
                        jwtTokenUtil.extractUserId(token);

                email =
                        jwtTokenUtil.getUsernameFromToken(token);
                
            }

            return ResponseEntity.ok(

                    customerSellService.createCustomerSellOrder(
                            request,
                            customerId,
                            email,
                            images
                    )
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }
    
    @GetMapping("/customer/{customerId}")
    public List<ScrapOrder> getCustomerOrders(
            @PathVariable Long customerId
    ) {

        List<ScrapOrder> orders =
                customerSellService.getOrdersByCustomer(customerId);

        for (ScrapOrder order : orders) {

            order.setImages(
                    imageRepository.findByScrapOrderId(order.getId())
            );
        }

        return orders;
    }
}

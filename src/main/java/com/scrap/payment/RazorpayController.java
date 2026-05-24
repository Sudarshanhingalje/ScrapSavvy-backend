package com.scrap.payment;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin
public class RazorpayController {

    @Value("${razorpay.key.id}")
    private String razorpayKey;

    @Value("${razorpay.key.secret}")
    private String razorpaySecret;

    @PostMapping("/createorder")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> data)
            throws Exception {

        // ✅ FIX: data.get("amount") comes as Double from JSON — convert safely
        double amountInRupees = ((Number) data.get("amount")).doubleValue();

        // ✅ Convert to paise (Razorpay requires integer paise)
        int amountInPaise = (int) Math.round(amountInRupees * 100);

        RazorpayClient razorpay = new RazorpayClient(razorpayKey, razorpaySecret);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaise);   // already in paise
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "txn_" + System.currentTimeMillis());

        Order order = razorpay.orders.create(orderRequest);

        Map<String, Object> response = new HashMap<>();
        response.put("orderId", order.get("id"));
        response.put("amount", order.get("amount"));  // Razorpay returns paise

        return ResponseEntity.ok(response);
    }
}
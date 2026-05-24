package com.scrap.payment;
//package com.scrap.services;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.scrap.dto.PaymentDTO;
//import com.scrap.entities.Payment;
//import com.scrap.exception.ResourceNotFoundException;
//import com.scrap.repositories.PaymentRepository;
//
//@Service
//public class PaymentService {
//    @Autowired
//    private PaymentRepository paymentRepository;
//
//    public Payment savePayment(PaymentDTO paymentDTO) {
//        Payment payment = new Payment();
//        payment.setTransactionId(paymentDTO.getPaymentId());
//        payment.setTransactionStatus(true);
//        payment.setModeOfPayment("Online");
//        payment.setCreatedOn(LocalDateTime.now());
//        payment.setUpdatedOn(LocalDateTime.now());
//        return paymentRepository.save(payment);
//    }
//
//    public List<Payment> getAllPayments() {
//        return paymentRepository.findAll();
//    }
//
//    public Payment updatePayment(Long id, Payment payment) {
//        Optional<Payment> existingPayment = paymentRepository.findById(id);
//        if (existingPayment.isPresent()) {
//            Payment updatedPayment = existingPayment.get();
//            updatedPayment.setTransactionStatus(payment.getTransactionStatus());
//            updatedPayment.setModeOfPayment(payment.getModeOfPayment());
//            updatedPayment.setUpdatedOn(LocalDateTime.now());
//            return paymentRepository.save(updatedPayment);
//        } else {
//            throw new ResourceNotFoundException("Payment not found");
//        }
//    }
//
//    public void deletePayment(Long id) {
//        paymentRepository.deleteById(id);
//    }
//}
//

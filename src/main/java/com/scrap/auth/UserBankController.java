//package com.scrap.auth;
//package com.scrap.controller;
//import java.time.LocalDateTime;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.scrap.dto.UserBankDTO;
//import com.scrap.entities.User;
//import com.scrap.entities.UserBankDetail;
//import com.scrap.repositories.UserBankRepository;
//
//@RestController
//@RequestMapping("/api/user/bank")
//public class UserBankController {
//
//    @Autowired
//    private UserBankRepository userBankRepository;
//
//    @PutMapping("/update/{userId}")
//    public ResponseEntity<?> updateBank(
//            @PathVariable Long userId,
//            @RequestBody UserBankDTO dto) {
//
//        UserBankDetail bank = new UserBankDetail();
//
//        User user = new User();
//        user.setUserId(userId);
//        bank.setUser(user);
//
//        bank.setBankName(dto.getBankName());
//        bank.setBankAccNo(dto.getBankAccNo());
//        bank.setBankAccIfsc(dto.getBankAccIfsc());
//        bank.setBankAccHolderName(dto.getBankAccHolderName());
//
//        bank.setActive(true);
//        bank.setCreatedOn(LocalDateTime.now());
//        bank.setUpdatedOn(LocalDateTime.now());
//
//        userBankRepository.save(bank);
//
//        return ResponseEntity.ok("Bank saved successfully");
//    }
//
//    @PostMapping("/save/{userId}")
//    public ResponseEntity<?> saveBankDetails(
//            @PathVariable Long userId,
//            @RequestBody UserBankDetail dto) {
//
//        User user = new User();
//        user.setUserId(userId);
//
//        UserBankDetail bank = new UserBankDetail();
//        bank.setUser(user);
//
//        bank.setBankName(dto.getBankName());
//        bank.setBankAccNo(dto.getBankAccNo());
//        bank.setBankAccIfsc(dto.getBankAccIfsc());
//        bank.setBankAccHolderName(dto.getBankAccHolderName());
//
//        bank.setActive(true);
//        bank.setCreatedOn(LocalDateTime.now());
//        bank.setUpdatedOn(LocalDateTime.now());
//
//        userBankRepository.save(bank);
//
//        return ResponseEntity.ok("Bank saved");
//    }
//}
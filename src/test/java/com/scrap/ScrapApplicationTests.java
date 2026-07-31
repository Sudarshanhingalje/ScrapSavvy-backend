package com.scrap;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("prod")
@TestPropertySource(properties = {
    "DB_URL=jdbc:mysql://localhost:3306/scrapsavvy?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true",
    "DB_USER=root",
    "DB_PASSWORD=Sudu@1308",
    "JWT_SECRET=ScrapProjectSuperSecureJwtSecretKey_2026_MustBeLongEnoughForHS512_ChangeThis12345",
    "RAZORPAY_KEY=rzp_test_SteHZcq9L88UIY",
    "RAZORPAY_SECRET=aYMxhx56GwQM7Lal3tLJr7cz",
    "GEMINI_API_KEY=AIzaSyCKTwaDuMJNv_q_9Zm2jdOc9EqV2Sl-3vA",
    "GEMINI_API_URL=https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-pro:generateContent"
})
class ScrapApplicationTests {

    @Test
    void contextLoads() {
    }
}
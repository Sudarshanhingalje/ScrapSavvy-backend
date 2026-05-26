package com.scrap.aiassitent.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scrap.aiassitent.dto.ChatRequest;
import com.scrap.aiassitent.dto.ChatResponse;
import com.scrap.aiassitent.service.AIService;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin("*")
public class AIController {

    @Autowired
    private AIService aiService;

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest req) {
        if (req == null || req.getMessage() == null || req.getMessage().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ChatResponse("", "en", false, "Message cannot be empty"));
        }

        String lang = req.getLanguage() != null ? req.getLanguage() : "en";
        ChatResponse response = aiService.generateResponse(req.getMessage(), lang);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public String health() {
        return "AI Service Running";
    }

    @GetMapping("/languages")
    public String[] languages() {
        return new String[]{"en", "hi", "mr"};
    }
}
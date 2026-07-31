package com.scrap.aiassitent.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.scrap.aiassitent.dto.ChatResponse;

@Service
public class AIService {

    // =========================
    // GEMINI CONFIG
    // =========================

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    // =========================
    // HUGGING FACE CONFIG
    // =========================

    @Value("${huggingface.api.key}")
    private String huggingFaceKey;

    @Value("${huggingface.api.url}")
    private String huggingFaceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    // =========================
    // MAIN RESPONSE METHOD
    // =========================

    public ChatResponse generateResponse(String message, String language) {

        try {

            String prompt = buildPrompt(message, language);

            // Try Gemini first
            String result = askGemini(prompt);

            // If Gemini fails -> fallback to HuggingFace
            if (
                    result == null ||
                    result.contains("429") ||
                    result.contains("RESOURCE_EXHAUSTED") ||
                    result.contains("quota") ||
                    result.contains("API Error")
            ) {

                System.out.println("Gemini failed. Switching to HuggingFace...");

                result = askHuggingFace(prompt);
            }

            if (result == null || result.trim().isEmpty()) {

                return new ChatResponse(
                        "Sorry, I could not get a response.",
                        language,
                        false
                );
            }

            return new ChatResponse(result, language, true);

        } catch (Exception e) {

            e.printStackTrace();

            return new ChatResponse(
                    "AI Error: " + e.getMessage(),
                    language,
                    false,
                    e.getMessage()
            );
        }
    }

    // =========================
    // PROMPT BUILDER
    // =========================

    private String buildPrompt(String message, String language) {

        String langInstruction;

        switch (language) {

            case "hi":
                langInstruction =
                        "You MUST reply in Hindi language only. Hindi mein jawab do.";
                break;

            case "mr":
                langInstruction =
                        "You MUST reply in Marathi language only. Marathi madhe uttar dya.";
                break;

            default:
                langInstruction =
                        "You MUST reply in English language only.";
        }

        return langInstruction
                + "\n\nYou are ScrapSavvy AI Assistant."
                + "\nYou help users with scrap prices, recycling, pickup booking and waste management in India."

                + "\n\nApproximate Scrap Prices:"
                + "\n- Iron: ₹25-35/kg"
                + "\n- Copper: ₹450-500/kg"
                + "\n- Aluminium: ₹100-130/kg"
                + "\n- Brass: ₹300-350/kg"
                + "\n- Paper: ₹8-12/kg"
                + "\n- Plastic: ₹5-20/kg"

                + "\n\nGuidelines:"
                + "\n- Keep answers short"
                + "\n- Be friendly"
                + "\n- Help users clearly"
                + "\n- If unsure about exact price, mention approximate range"

                + "\n\nUser Message: "
                + message;
    }

    // =========================
    // GEMINI API
    // =========================

    public String askGemini(String prompt) {

        try {

            String url = apiUrl + "?key=" + apiKey;

            System.out.println("\n=== GEMINI API CALL ===");

            Map<String, Object> textPart = new HashMap<>();
            textPart.put("text", prompt);

            Map<String, Object> content = new HashMap<>();
            content.put("parts", List.of(textPart));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", List.of(content));

            // Gemini Config
            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("temperature", 0.7);
            generationConfig.put("topK", 40);
            generationConfig.put("topP", 0.95);
            generationConfig.put("maxOutputTokens", 512);

            requestBody.put("generationConfig", generationConfig);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            Map body = response.getBody();

            if (body == null) {
                return "No response from Gemini";
            }

            List candidates = (List) body.get("candidates");

            if (candidates == null || candidates.isEmpty()) {
                return "No candidates returned";
            }

            Map firstCandidate = (Map) candidates.get(0);

            Map contentMap = (Map) firstCandidate.get("content");

            if (contentMap == null) {
                return "Empty Gemini response";
            }

            List parts = (List) contentMap.get("parts");

            if (parts == null || parts.isEmpty()) {
                return "Empty Gemini response";
            }

            Map firstPart = (Map) parts.get(0);

            Object textObj = firstPart.get("text");

            if (textObj == null) {
                return "No text returned";
            }

            return textObj.toString();

        } catch (HttpClientErrorException e) {

            System.out.println("Gemini HTTP Error: "
                    + e.getStatusCode());

            return "API Error: "
                    + e.getStatusCode();

        } catch (Exception e) {

            e.printStackTrace();

            return "Gemini Error: "
                    + e.getMessage();
        }
    }

    // =========================
    // HUGGING FACE API
    // =========================

    public String askHuggingFace(String prompt) {

        try {

            System.out.println("\n=== HUGGING FACE FALLBACK ===");
            System.out.println("HF URL = " + huggingFaceUrl);

            HttpHeaders headers = new HttpHeaders();

            headers.setBearerAuth(huggingFaceKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("inputs", prompt);

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(requestBody, headers);

            ResponseEntity<List> response = restTemplate.exchange(
                    huggingFaceUrl,
                    HttpMethod.POST,
                    entity,
                    List.class
            );

            List responseBody = response.getBody();

            if (responseBody == null || responseBody.isEmpty()) {
                return "No response from HuggingFace";
            }

            Map firstResponse = (Map) responseBody.get(0);

            Object generatedText =
                    firstResponse.get("generated_text");

            if (generatedText == null) {
                return "Empty HuggingFace response";
            }

            return generatedText.toString();

        } catch (Exception e) {

            e.printStackTrace();

            return "Fallback AI Error: "
                    + e.getMessage();
        }
    }
}
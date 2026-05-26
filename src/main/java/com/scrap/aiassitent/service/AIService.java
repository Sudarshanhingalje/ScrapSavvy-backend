package com.scrap.aiassitent.service;

import com.scrap.aiassitent.dto.ChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AIService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public ChatResponse generateResponse(String message, String language) {
        try {
            String prompt = buildPrompt(message, language);
            String result = askGemini(prompt);

            if (result == null || result.trim().isEmpty()) {
                return new ChatResponse("Sorry, I could not get a response. Please try again.", language, false);
            }

            return new ChatResponse(result, language, true);

        } catch (Exception e) {
            e.printStackTrace();
            return new ChatResponse("Error: " + e.getMessage(), language, false, e.getMessage());
        }
    }

    private String buildPrompt(String message, String language) {

        String langInstruction;
        switch (language) {
            case "hi":
                langInstruction = "You MUST reply in Hindi language only. Hindi mein jawab do. ";
                break;
            case "mr":
                langInstruction = "You MUST reply in Marathi language only. Marathi madhe uttar dya. ";
                break;
            default:
                langInstruction = "You MUST reply in English language only. ";
        }

        String systemPrompt = langInstruction
            + "\n\nYou are ScrapSavvy AI Assistant, a smart and friendly assistant for ScrapSavvy — "
            + "an online scrap pickup and recycling platform in India."
            + "\n\nYou help users with:"
            + "\n- Current scrap material prices in India (iron, copper, aluminium, brass, steel, paper, plastic, e-waste)"
            + "\n- Scheduling and booking scrap pickup"
            + "\n- Recycling tips and best practices"
            + "\n- Tracking scrap pickup orders"
            + "\n- Information about different types of scrap materials"
            + "\n- Environmental benefits of recycling"
            + "\n- How ScrapSavvy works"

            + "\n\nScrap Price Reference (approximate Indian market rates per kg):"
            + "\n- Iron/Steel: ₹25-35"
            + "\n- Copper: ₹450-500"
            + "\n- Aluminium: ₹100-130"
            + "\n- Brass: ₹300-350"
            + "\n- Paper/Cardboard: ₹8-12"
            + "\n- Plastic (hard): ₹10-20"
            + "\n- Plastic (soft): ₹5-8"
            + "\n- E-waste (mobile): ₹50-200"
            + "\n- E-waste (laptop): ₹200-500"
            + "\n- Glass: ₹1-3"
            + "\n- Rubber: ₹8-15"

            + "\n\nHow ScrapSavvy works:"
            + "\n- User registers and requests a pickup"
            + "\n- Our agent visits home/office to collect scrap"
            + "\n- Scrap is weighed and payment is made instantly"
            + "\n- We recycle responsibly"

            + "\n\nGuidelines:"
            + "\n- Be friendly, helpful and professional"
            + "\n- Keep answers short and clear (2-4 lines max)"
            + "\n- If user asks for pickup booking, ask: location, scrap type, preferred date and time"
            + "\n- If you do not know exact current price, give approximate range and say prices may vary"
            + "\n- Never make up information about orders or user data"
            + "\n- If asked something unrelated to scrap or recycling, politely redirect to scrap topics"

            + "\n\nUser message: " + message;

        return systemPrompt;
    }

    public String askGemini(String prompt) {
        try {
            String url = apiUrl + "?key=" + apiKey;

            System.out.println("=== Gemini API Call ===");
            System.out.println("API Key starts with: " + (apiKey != null ? apiKey.substring(0, 10) + "..." : "NULL"));

            Map<String, Object> textPart = new HashMap<>();
            textPart.put("text", prompt);

            Map<String, Object> content = new HashMap<>();
            content.put("parts", List.of(textPart));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", List.of(content));

            // Gemini generation config for better responses
            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("temperature", 0.7);
            generationConfig.put("topK", 40);
            generationConfig.put("topP", 0.95);
            generationConfig.put("maxOutputTokens", 512);
            requestBody.put("generationConfig", generationConfig);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            System.out.println("Gemini Response Status: " + response.getStatusCode());

            Map body = response.getBody();
            if (body == null) {
                System.out.println("ERROR: Response body is null");
                return "No response from Gemini";
            }

            if (body.containsKey("error")) {
                Map error = (Map) body.get("error");
                System.out.println("Gemini API Error: " + error);
                return "API Error: " + error.get("message");
            }

            List candidates = (List) body.get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                System.out.println("ERROR: No candidates. Full body: " + body);
                return "No response generated. Please try again.";
            }

            Map firstCandidate = (Map) candidates.get(0);
            Map contentMap = (Map) firstCandidate.get("content");

            if (contentMap == null) {
                return "Empty response from AI.";
            }

            List parts = (List) contentMap.get("parts");
            if (parts == null || parts.isEmpty()) {
                return "Empty response from AI.";
            }

            Map firstPart = (Map) parts.get(0);
            String text = firstPart.get("text").toString();

            System.out.println("Gemini Reply: " + text.substring(0, Math.min(100, text.length())));

            return text;

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            System.out.println("HTTP Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return "API Error " + e.getStatusCode() + ": Please check API key or quota.";

        } catch (Exception e) {
            System.out.println("Exception: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
            return "AI Error: " + e.getMessage();
        }
    }
}
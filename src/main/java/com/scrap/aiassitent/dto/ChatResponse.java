package com.scrap.aiassitent.dto;

public class ChatResponse {

    private String message;
    private String language;
    private boolean success;
    private String error;

    public ChatResponse() {}

    public ChatResponse(String message, String language, boolean success) {
        this.message = message;
        this.language = language;
        this.success = success;
        this.error = null;
    }

    public ChatResponse(String message, String language, boolean success, String error) {
        this.message = message;
        this.language = language;
        this.success = success;
        this.error = error;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}
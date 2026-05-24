package com.scrap.auth.dto;

import com.scrap.auth.entity.UserProfile;

public class LoginResponse {

    private String token;
    private UserProfile userProfile;

    public LoginResponse(String token, UserProfile userProfile) {
        this.token = token;
        this.userProfile = userProfile;
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }
}


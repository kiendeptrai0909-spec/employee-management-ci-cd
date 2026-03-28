package com.example.employee_management.dto;

import com.example.employee_management.entity.Role;

public class LoginResponse {
    private String tokenType;
    private String accessToken;
    private String username;
    private Role role;

    public LoginResponse() {
    }

    public LoginResponse(String tokenType, String accessToken, String username, Role role) {
        this.tokenType = tokenType;
        this.accessToken = accessToken;
        this.username = username;
        this.role = role;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}

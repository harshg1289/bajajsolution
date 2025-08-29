package com.bajaj.challenge.model;

public class WebhookRequest {
    private String name;
    private String regNo;
    private String email;
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getRegNo() { return regNo; }
    public void setRegNo(String regNo) { this.regNo = regNo; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
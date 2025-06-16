package com.example.finzu.models;

import java.io.Serializable;

public class User implements Serializable {
    private int id;
    private String fullName;
    private String email;
    private String password;
    private String profilePicUrl;

    // Settings
    private String currency;        // cop, usd, eur
    private String reminderHour;    // "off" or "08:00", etc.

    public User(int id, String fullName, String email, String password, String profilePicUrl,
                String currency, String reminderHour) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.profilePicUrl = profilePicUrl;
        this.currency = currency;
        this.reminderHour = reminderHour;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getProfilePicUrl() { return profilePicUrl; }
    public String getCurrency() { return currency; }
    public String getReminderHour() { return reminderHour; }

    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setProfilePicUrl(String profilePicUrl) { this.profilePicUrl = profilePicUrl; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setReminderHour(String reminderHour) { this.reminderHour = reminderHour; }
}

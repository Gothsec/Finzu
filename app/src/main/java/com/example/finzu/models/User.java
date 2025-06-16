package com.example.finzu.models;

public class User {
    private int id;
    private String fullName;
    private String email;
    private String password;
    private String profilePicUrl;
    private String currency;
    private String reminderHour;
    private boolean reminder; // true / false
    private double balance;

    public User(int id, String fullName, String email, String password, String profilePicUrl,
                String currency, String reminderHour, boolean reminder, double balance) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.profilePicUrl = profilePicUrl;
        this.currency = currency;
        this.reminderHour = reminderHour;
        this.reminder = reminder;
        this.balance = balance;
    }

    // Getters y setters
    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getProfilePicUrl() { return profilePicUrl; }
    public String getCurrency() { return currency; }
    public String getReminderHour() { return reminderHour; }
    public boolean isReminder() { return reminder; }
    public double getBalance() { return balance; }

    public void setId(int id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setProfilePicUrl(String profilePicUrl) { this.profilePicUrl = profilePicUrl; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setReminderHour(String reminderHour) { this.reminderHour = reminderHour; }
    public void setReminder(boolean reminder) { this.reminder = reminder; }
    public void setBalance(double balance) { this.balance = balance; }
}

package com.example.finzu.models;

public class Account {
    private int id;
    private String userEmail;
    private String name;
    private double amount;
    private boolean active;

    public Account(int id, String userEmail, String name, double amount) {
        this.id = id;
        this.userEmail = userEmail;
        this.name = name;
        this.amount = amount;
    }

    // Getters y setters
    public int getId() { return id; }
    public String getUserEmail() { return userEmail; }
    public String getName() { return name; }
    public double getAmount() { return amount; }
    public boolean isActive() { return active; }

    public void setId(int id) { this.id = id; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public void setName(String name) { this.name = name; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setActive(boolean active) { this.active = active; }
}

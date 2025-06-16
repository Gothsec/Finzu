package com.example.finzu.models;

public class Transaction {
    private int id;
    private double amount;
    private String type; // "income" o "outcome"
    private int accountId;
    private String details;
    private String date;

    public Transaction(int id, double amount, String type, int accountId, String details, String date) {
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.accountId = accountId;
        this.details = details;
        this.date = date;
    }

    // Getters y setters
    public int getId() { return id; }
    public double getAmount() { return amount; }
    public String getType() { return type; }
    public int getAccountId() { return accountId; }
    public String getDetails() { return details; }
    public String getDate() { return date; }

    public void setId(int id) { this.id = id; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setType(String type) { this.type = type; }
    public void setAccountId(int accountId) { this.accountId = accountId; }
    public void setDetails(String details) { this.details = details; }
    public void setDate(String date) { this.date = date; }
}

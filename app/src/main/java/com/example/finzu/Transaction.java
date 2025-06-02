package com.example.finzu;

import java.io.Serializable;

public class Transaction implements Serializable {
    private int id;
    private String type; // income or outcome
    private double amount;
    private int accountId; // linked account
    private String details;
    private String date;

    public Transaction(int id, String type, double amount, int accountId, String details, String date) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.accountId = accountId;
        this.details = details;
        this.date = date;
    }

    // Getters and Setters
    public int getId() { return id; }

    public String getType() { return type; }

    public double getAmount() { return amount; }

    public int getAccountId() { return accountId; }

    public String getDetails() { return details; }

    public String getDate() { return date; }

    public void setId(int id) { this.id = id; }

    public void setType(String type) { this.type = type; }

    public void setAmount(double amount) { this.amount = amount; }

    public void setAccountId(int accountId) { this.accountId = accountId; }

    public void setDetails(String details) { this.details = details; }

    public void setDate(String date) { this.date = date; }
}
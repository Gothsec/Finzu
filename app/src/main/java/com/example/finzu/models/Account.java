package com.example.finzu.models;

import java.io.Serializable;

public class Account implements Serializable {
    private int id;
    private String name;
    private double amount;
    private String type;

    public Account(int id, String name, double amount, String type) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.type = type;
    }

    // Getters and Setters
    public int getId() { return id; }

    public String getName() { return name; }

    public double getAmount() { return amount; }

    public String getType() { return type; }

    public void setId(int id) { this.id = id; }

    public void setName(String name) { this.name = name; }

    public void setAmount(double amount) { this.amount = amount; }

    public void setType(String type) { this.type = type; }
}
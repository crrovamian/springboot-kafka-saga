package com.example.risk.entity;

import java.math.BigDecimal;

public class Customer {
    private String id;
    private String name;
    private String riskScore;
    private BigDecimal creditLimit;

    public Customer() {}

    public Customer(String id, String name, String riskScore, BigDecimal creditLimit) {
        this.id = id;
        this.name = name;
        this.riskScore = riskScore;
        this.creditLimit = creditLimit;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRiskScore() { return riskScore; }
    public void setRiskScore(String riskScore) { this.riskScore = riskScore; }
    public BigDecimal getCreditLimit() { return creditLimit; }
    public void setCreditLimit(BigDecimal creditLimit) { this.creditLimit = creditLimit; }
}

package com.example.events;

import java.math.BigDecimal;

public class FundsReserved {
    private String loanId;
    private String customerId;
    private BigDecimal amount;
    private String paymentId;
    private String timestamp;

    public FundsReserved() {}

    public FundsReserved(String loanId, String customerId, BigDecimal amount, String paymentId, String timestamp) {
        this.loanId = loanId;
        this.customerId = customerId;
        this.amount = amount;
        this.paymentId = paymentId;
        this.timestamp = timestamp;
    }

    public String getLoanId() { return loanId; }
    public void setLoanId(String loanId) { this.loanId = loanId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}

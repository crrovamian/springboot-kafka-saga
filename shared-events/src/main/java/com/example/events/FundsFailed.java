package com.example.events;

import java.math.BigDecimal;

public class FundsFailed {
    private String loanId;
    private String customerId;
    private BigDecimal amount;
    private String reason;
    private String timestamp;

    public FundsFailed() {}

    public FundsFailed(String loanId, String customerId, BigDecimal amount, String reason, String timestamp) {
        this.loanId = loanId;
        this.customerId = customerId;
        this.amount = amount;
        this.reason = reason;
        this.timestamp = timestamp;
    }

    public String getLoanId() { return loanId; }
    public void setLoanId(String loanId) { this.loanId = loanId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}

package com.example.risk.service;

import com.example.risk.entity.Customer;
import com.example.events.LoanRequested;
import com.example.events.RiskApproved;
import com.example.events.RiskRejected;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RiskService {
    private final Map<String, Customer> customers = new ConcurrentHashMap<>();

    public RiskService() {
        customers.put("cust-001", new Customer("cust-001", "John Doe", "LOW", new BigDecimal("50000")));
        customers.put("cust-002", new Customer("cust-002", "Jane Smith", "MEDIUM", new BigDecimal("30000")));
        customers.put("cust-003", new Customer("cust-003", "Bob Johnson", "HIGH", new BigDecimal("10000")));
    }

    @Retry(name = "riskRetry")
    public boolean evaluateRisk(LoanRequested loanRequest) {
        Customer customer = customers.get(loanRequest.getCustomerId());
        
        if (customer == null) {
            return false;
        }

        if ("HIGH".equals(customer.getRiskScore())) {
            return false;
        }

        if (loanRequest.getAmount().compareTo(customer.getCreditLimit()) > 0) {
            return false;
        }

        return true;
    }

    public RiskApproved approve(LoanRequested loanRequest) {
        return new RiskApproved(
            loanRequest.getLoanId(),
            loanRequest.getCustomerId(),
            loanRequest.getAmount(),
            LocalDateTime.now().toString()
        );
    }

    public RiskRejected reject(LoanRequested loanRequest, String reason) {
        return new RiskRejected(
            loanRequest.getLoanId(),
            loanRequest.getCustomerId(),
            loanRequest.getAmount(),
            reason,
            LocalDateTime.now().toString()
        );
    }
}

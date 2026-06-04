package com.example.loan.kafka;

import com.example.loan.service.LoanService;
import com.example.events.FundsReserved;
import com.example.events.FundsFailed;
import com.example.events.RiskRejected;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class LoanConsumer {
    private final LoanService loanService;

    public LoanConsumer(LoanService loanService) {
        this.loanService = loanService;
    }

    @CircuitBreaker(name = "loanConsumerCircuitBreaker")
    @KafkaListener(topics = "risk-rejected", groupId = "loan-group")
    public void handleRiskRejected(RiskRejected event) {
        loanService.updateLoanStatus(event.getLoanId(), "REJECTED");
    }

    @CircuitBreaker(name = "loanConsumerCircuitBreaker")
    @KafkaListener(topics = "funds-reserved", groupId = "loan-group")
    public void handleFundsReserved(FundsReserved event) {
        loanService.updateLoanStatus(event.getLoanId(), "APPROVED");
    }

    @CircuitBreaker(name = "loanConsumerCircuitBreaker")
    @KafkaListener(topics = "funds-failed", groupId = "loan-group")
    public void handleFundsFailed(FundsFailed event) {
        loanService.updateLoanStatus(event.getLoanId(), "FAILED");
    }
}

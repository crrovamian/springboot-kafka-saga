package com.example.loan.kafka;

import com.example.loan.service.LoanService;
import com.example.events.FundsReserved;
import com.example.events.FundsFailed;
import com.example.events.RiskRejected;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class LoanConsumer {
    private final LoanService loanService;

    public LoanConsumer(LoanService loanService) {
        this.loanService = loanService;
    }

    @KafkaListener(topics = "risk-rejected", groupId = "loan-group")
    public void handleRiskRejected(RiskRejected event) {
        loanService.updateLoanStatus(event.getLoanId(), "REJECTED");
    }

    @KafkaListener(topics = "funds-reserved", groupId = "loan-group")
    public void handleFundsReserved(FundsReserved event) {
        loanService.updateLoanStatus(event.getLoanId(), "APPROVED");
    }

    @KafkaListener(topics = "funds-failed", groupId = "loan-group")
    public void handleFundsFailed(FundsFailed event) {
        loanService.updateLoanStatus(event.getLoanId(), "FAILED");
    }
}

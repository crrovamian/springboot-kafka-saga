package com.example.risk.kafka;

import com.example.risk.service.RiskService;
import com.example.events.LoanRequested;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class RiskConsumer {
    private final RiskService riskService;
    private final RiskProducer riskProducer;

    public RiskConsumer(RiskService riskService, RiskProducer riskProducer) {
        this.riskService = riskService;
        this.riskProducer = riskProducer;
    }

    @KafkaListener(topics = "loan-requested", groupId = "risk-group")
    public void handleLoanRequested(LoanRequested event) {
        boolean approved = riskService.evaluateRisk(event);
        
        if (approved) {
            riskProducer.sendRiskApproved(riskService.approve(event));
        } else {
            riskProducer.sendRiskRejected(riskService.reject(event, "Risk evaluation failed"));
        }
    }
}

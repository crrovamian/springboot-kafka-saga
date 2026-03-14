package com.example.risk.kafka;

import com.example.events.RiskApproved;
import com.example.events.RiskRejected;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class RiskProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public RiskProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendRiskApproved(RiskApproved event) {
        kafkaTemplate.send("risk-approved", event.getLoanId(), event);
    }

    public void sendRiskRejected(RiskRejected event) {
        kafkaTemplate.send("risk-rejected", event.getLoanId(), event);
    }
}

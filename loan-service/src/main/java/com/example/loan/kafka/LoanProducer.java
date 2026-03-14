package com.example.loan.kafka;

import com.example.events.LoanRequested;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class LoanProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public LoanProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendLoanRequested(LoanRequested event) {
        kafkaTemplate.send("loan-requested", event.getLoanId(), event);
    }
}

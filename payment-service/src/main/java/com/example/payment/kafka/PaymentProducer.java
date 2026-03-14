package com.example.payment.kafka;

import com.example.events.FundsReserved;
import com.example.events.FundsFailed;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PaymentProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendFundsReserved(FundsReserved event) {
        kafkaTemplate.send("funds-reserved", event.getLoanId(), event);
    }

    public void sendFundsFailed(FundsFailed event) {
        kafkaTemplate.send("funds-failed", event.getLoanId(), event);
    }
}

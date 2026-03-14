package com.example.payment.kafka;

import com.example.payment.service.PaymentService;
import com.example.events.RiskApproved;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentConsumer {
    private final PaymentService paymentService;

    public PaymentConsumer(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @KafkaListener(topics = "risk-approved", groupId = "payment-group")
    public void handleRiskApproved(RiskApproved event) {
        paymentService.processPayment(event);
    }
}

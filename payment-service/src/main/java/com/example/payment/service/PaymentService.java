package com.example.payment.service;

import com.example.payment.entity.Payment;
import com.example.events.RiskApproved;
import com.example.events.FundsReserved;
import com.example.events.FundsFailed;
import com.example.payment.kafka.PaymentProducer;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {
    private final PaymentProducer paymentProducer;

    public PaymentService(PaymentProducer paymentProducer) {
        this.paymentProducer = paymentProducer;
    }

    public void processPayment(RiskApproved event) {
        Payment payment = new Payment(event.getLoanId(), event.getCustomerId(), event.getAmount(), "PROCESSING");
        
        boolean success = reserveFunds(payment);
        
        if (success) {
            FundsReserved fundsReserved = new FundsReserved(
                event.getLoanId(),
                event.getCustomerId(),
                event.getAmount(),
                payment.getId(),
                LocalDateTime.now().toString()
            );
            paymentProducer.sendFundsReserved(fundsReserved);
        } else {
            FundsFailed fundsFailed = new FundsFailed(
                event.getLoanId(),
                event.getCustomerId(),
                event.getAmount(),
                "Payment processing failed",
                LocalDateTime.now().toString()
            );
            paymentProducer.sendFundsFailed(fundsFailed);
        }
    }

    private boolean reserveFunds(Payment payment) {
        return Math.random() > 0.33;
    }
}

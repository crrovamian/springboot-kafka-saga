package com.example.payment.service;

import com.example.payment.entity.Payment;
import com.example.events.RiskApproved;
import com.example.events.FundsReserved;
import com.example.events.FundsFailed;
import com.example.payment.kafka.PaymentProducer;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class PaymentService {
    private final PaymentProducer paymentProducer;
    private final ReserveFundsService reserveFundsService;

    public PaymentService(PaymentProducer paymentProducer, ReserveFundsService reserveFundsService) {
        this.paymentProducer = paymentProducer;
        this.reserveFundsService = reserveFundsService;
    }

    @CircuitBreaker(name = "paymentCircuitBreaker")
    public void processPayment(RiskApproved event) {
        Payment payment = new Payment(event.getLoanId(), event.getCustomerId(), event.getAmount(), "PROCESSING");

        boolean success = reserveFundsService.reserveFunds(payment);

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
}

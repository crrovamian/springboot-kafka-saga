package com.example.payment.service;

import com.example.payment.entity.Payment;
import com.example.events.RiskApproved;
import com.example.events.FundsReserved;
import com.example.events.FundsFailed;
import com.example.payment.kafka.PaymentProducer;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class PaymentService {
    private final PaymentProducer paymentProducer;
    private final ReserveFundsService reserveFundsService;
    private final Tracer tracer;

    public PaymentService(PaymentProducer paymentProducer, ReserveFundsService reserveFundsService, Tracer tracer) {
        this.paymentProducer = paymentProducer;
        this.reserveFundsService = reserveFundsService;
        this.tracer = tracer;
    }

    @CircuitBreaker(name = "paymentCircuitBreaker")
    public void processPayment(RiskApproved event) {
        Span span = tracer.spanBuilder("PaymentService.processPayment")
            .setAttribute("loan.id", event.getLoanId())
            .setAttribute("customer.id", event.getCustomerId())
            .setAttribute("loan.amount", event.getAmount().doubleValue())
            .startSpan();
        try (Scope ignored = span.makeCurrent()) {
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
                span.setAttribute("payment.outcome", "reserved");
            } else {
                FundsFailed fundsFailed = new FundsFailed(
                    event.getLoanId(),
                    event.getCustomerId(),
                    event.getAmount(),
                    "Payment processing failed",
                    LocalDateTime.now().toString()
                );
                paymentProducer.sendFundsFailed(fundsFailed);
                span.setAttribute("payment.outcome", "failed");
            }
        } catch (Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR);
            throw e;
        } finally {
            span.end();
        }
    }
}

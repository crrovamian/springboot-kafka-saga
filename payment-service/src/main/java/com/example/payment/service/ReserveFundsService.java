package com.example.payment.service;

import com.example.payment.entity.Payment;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ReserveFundsService {
    private static final Logger log = LoggerFactory.getLogger(ReserveFundsService.class);
    private final Tracer tracer;

    public ReserveFundsService(Tracer tracer) {
        this.tracer = tracer;
    }

    @Retry(name = "paymentRetry", fallbackMethod = "fallback")
    public boolean reserveFunds(Payment payment) {
        Span span = tracer.spanBuilder("ReserveFundsService.reserveFunds")
            .setAttribute("payment.id", payment.getId())
            .setAttribute("loan.id", payment.getLoanId())
            .startSpan();
        try (Scope ignored = span.makeCurrent()) {
            log.info("Attempting to reserve funds for payment {}", payment.getId());
            boolean success = Math.random() > 0.33;
            span.setAttribute("funds.reserved", success);
            return success;
        } catch (Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR);
            throw e;
        } finally {
            span.end();
        }
    }

    public boolean fallback(Payment payment, Exception e) {
        log.warn("Funds reservation failed after retries for payment {}", payment.getId());
        Span span = tracer.spanBuilder("ReserveFundsService.fallback")
            .setAttribute("payment.id", payment.getId())
            .setAttribute("loan.id", payment.getLoanId())
            .startSpan();
        span.end();
        return false;
    }
}

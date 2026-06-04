package com.example.payment.service;

import com.example.payment.entity.Payment;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ReserveFundsService {
    private static final Logger log = LoggerFactory.getLogger(ReserveFundsService.class);

    @Retry(name = "paymentRetry", fallbackMethod = "fallback")
    public boolean reserveFunds(Payment payment) {
        log.info("Attempting to reserve funds for payment {}", payment.getId());
        return Math.random() > 0.33;
    }

    public boolean fallback(Payment payment, Exception e) {
        log.warn("Funds reservation failed after retries for payment {}", payment.getId());
        return false;
    }
}

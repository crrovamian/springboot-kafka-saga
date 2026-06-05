package com.example.payment.service;

import com.example.payment.entity.Payment;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class ReserveFundsServiceTest {

    private final ReserveFundsService reserveFundsService = new ReserveFundsService();

    @Test
    void fallbackShouldReturnFalse() {
        Payment payment = new Payment("loan-1", "cust-001", new BigDecimal("10000"), "PROCESSING");
        boolean result = reserveFundsService.fallback(payment, new RuntimeException("test"));
        assertFalse(result);
    }
}

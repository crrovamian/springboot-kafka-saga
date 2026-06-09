package com.example.payment.service;

import com.example.payment.entity.Payment;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ReserveFundsServiceTest {

    private final ReserveFundsService reserveFundsService;

    {
        Tracer tracer = mock(Tracer.class);
        SpanBuilder spanBuilder = mock(SpanBuilder.class);
        Span span = mock(Span.class);
        when(tracer.spanBuilder(anyString())).thenReturn(spanBuilder);
        when(spanBuilder.setAttribute(anyString(), any())).thenReturn(spanBuilder);
        when(spanBuilder.startSpan()).thenReturn(span);
        when(span.makeCurrent()).thenReturn(mock(Scope.class));
        reserveFundsService = new ReserveFundsService(tracer);
    }

    @Test
    void fallbackShouldReturnFalse() {
        Payment payment = new Payment("loan-1", "cust-001", new BigDecimal("10000"), "PROCESSING");
        boolean result = reserveFundsService.fallback(payment, new RuntimeException("test"));
        assertFalse(result);
    }
}

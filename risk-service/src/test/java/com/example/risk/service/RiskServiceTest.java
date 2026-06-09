package com.example.risk.service;

import com.example.events.LoanRequested;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RiskServiceTest {

    private RiskService riskService;

    @BeforeEach
    void setUp() {
        Tracer tracer = mock(Tracer.class);
        SpanBuilder spanBuilder = mock(SpanBuilder.class);
        Span span = mock(Span.class);
        when(tracer.spanBuilder(anyString())).thenReturn(spanBuilder);
        when(spanBuilder.setAttribute(anyString(), any())).thenReturn(spanBuilder);
        when(spanBuilder.setAttribute(anyString(), anyDouble())).thenReturn(spanBuilder);
        when(spanBuilder.startSpan()).thenReturn(span);
        when(span.makeCurrent()).thenReturn(mock(Scope.class));
        riskService = new RiskService(tracer);
    }

    @Test
    void shouldRejectWhenCustomerNotFound() {
        LoanRequested request = new LoanRequested("loan-1", "unknown-customer", new BigDecimal("1000"), null);
        assertFalse(riskService.evaluateRisk(request));
    }

    @Test
    void shouldRejectWhenCustomerHasHighRiskScore() {
        LoanRequested request = new LoanRequested("loan-2", "cust-003", new BigDecimal("5000"), null);
        assertFalse(riskService.evaluateRisk(request));
    }

    @Test
    void shouldRejectWhenAmountExceedsCreditLimit() {
        LoanRequested request = new LoanRequested("loan-3", "cust-002", new BigDecimal("40000"), null);
        assertFalse(riskService.evaluateRisk(request));
    }

    @Test
    void shouldApproveWhenLowRiskAndWithinLimit() {
        LoanRequested request = new LoanRequested("loan-4", "cust-001", new BigDecimal("25000"), null);
        assertTrue(riskService.evaluateRisk(request));
    }

    @Test
    void shouldApproveWhenMediumRiskAndWithinLimit() {
        LoanRequested request = new LoanRequested("loan-5", "cust-002", new BigDecimal("20000"), null);
        assertTrue(riskService.evaluateRisk(request));
    }

    @Test
    void shouldApproveWithExactCreditLimitAmount() {
        LoanRequested request = new LoanRequested("loan-6", "cust-001", new BigDecimal("50000"), null);
        assertTrue(riskService.evaluateRisk(request));
    }

    @Test
    void shouldReturnApprovedEventWithCorrectData() {
        LoanRequested request = new LoanRequested("loan-7", "cust-001", new BigDecimal("10000"), "2024-01-01T00:00:00");
        var approved = riskService.approve(request);
        assertEquals("loan-7", approved.getLoanId());
        assertEquals("cust-001", approved.getCustomerId());
        assertEquals(new BigDecimal("10000"), approved.getAmount());
        assertNotNull(approved.getTimestamp());
    }

    @Test
    void shouldReturnRejectedEventWithCorrectData() {
        LoanRequested request = new LoanRequested("loan-8", "cust-001", new BigDecimal("10000"), "2024-01-01T00:00:00");
        var rejected = riskService.reject(request, "Insufficient credit");
        assertEquals("loan-8", rejected.getLoanId());
        assertEquals("cust-001", rejected.getCustomerId());
        assertEquals("Insufficient credit", rejected.getReason());
        assertNotNull(rejected.getTimestamp());
    }
}

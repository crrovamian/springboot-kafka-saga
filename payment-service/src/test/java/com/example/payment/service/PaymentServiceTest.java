package com.example.payment.service;

import com.example.events.FundsFailed;
import com.example.events.FundsReserved;
import com.example.events.RiskApproved;
import com.example.payment.kafka.PaymentProducer;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentProducer paymentProducer;

    @Mock
    private ReserveFundsService reserveFundsService;

    @Mock
    private Tracer tracer;

    @Mock
    private SpanBuilder spanBuilder;

    @Mock
    private Span span;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        lenient().when(tracer.spanBuilder(anyString())).thenReturn(spanBuilder);
        lenient().when(spanBuilder.setAttribute(anyString(), any())).thenReturn(spanBuilder);
        lenient().when(spanBuilder.setAttribute(anyString(), anyDouble())).thenReturn(spanBuilder);
        lenient().when(spanBuilder.startSpan()).thenReturn(span);
        lenient().when(span.makeCurrent()).thenReturn(mock(Scope.class));
    }

    @Test
    void processPaymentShouldSendFundsReservedWhenReservationSucceeds() {
        RiskApproved event = new RiskApproved("loan-1", "cust-001", new BigDecimal("10000"), "2024-01-01T00:00:00");
        when(reserveFundsService.reserveFunds(any())).thenReturn(true);

        paymentService.processPayment(event);

        verify(paymentProducer).sendFundsReserved(any(FundsReserved.class));
        verify(paymentProducer, never()).sendFundsFailed(any());
    }

    @Test
    void processPaymentShouldSendFundsFailedWhenReservationFails() {
        RiskApproved event = new RiskApproved("loan-2", "cust-002", new BigDecimal("5000"), "2024-01-01T00:00:00");
        when(reserveFundsService.reserveFunds(any())).thenReturn(false);

        paymentService.processPayment(event);

        verify(paymentProducer).sendFundsFailed(any(FundsFailed.class));
        verify(paymentProducer, never()).sendFundsReserved(any());
    }

    @Test
    void processPaymentShouldIncludeLoanIdInFundsReservedEvent() {
        RiskApproved event = new RiskApproved("loan-3", "cust-001", new BigDecimal("15000"), "2024-01-01T00:00:00");
        when(reserveFundsService.reserveFunds(any())).thenReturn(true);

        paymentService.processPayment(event);

        ArgumentCaptor<FundsReserved> captor = ArgumentCaptor.forClass(FundsReserved.class);
        verify(paymentProducer).sendFundsReserved(captor.capture());
        assertEquals("loan-3", captor.getValue().getLoanId());
        assertEquals("cust-001", captor.getValue().getCustomerId());
        assertEquals(new BigDecimal("15000"), captor.getValue().getAmount());
        assertNotNull(captor.getValue().getPaymentId());
    }

    @Test
    void processPaymentShouldIncludeLoanIdInFundsFailedEvent() {
        RiskApproved event = new RiskApproved("loan-4", "cust-002", new BigDecimal("8000"), "2024-01-01T00:00:00");
        when(reserveFundsService.reserveFunds(any())).thenReturn(false);

        paymentService.processPayment(event);

        ArgumentCaptor<FundsFailed> captor = ArgumentCaptor.forClass(FundsFailed.class);
        verify(paymentProducer).sendFundsFailed(captor.capture());
        assertEquals("loan-4", captor.getValue().getLoanId());
        assertEquals("cust-002", captor.getValue().getCustomerId());
        assertEquals("Payment processing failed", captor.getValue().getReason());
    }
}

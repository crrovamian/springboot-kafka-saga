package com.example.loan.service;

import com.example.loan.entity.Loan;
import com.example.loan.kafka.LoanProducer;
import com.example.loan.repository.LoanRepository;
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
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private LoanProducer loanProducer;

    @Mock
    private Tracer tracer;

    @Mock
    private SpanBuilder spanBuilder;

    @Mock
    private Span span;

    @InjectMocks
    private LoanService loanService;

    @BeforeEach
    void setUp() {
        lenient().when(tracer.spanBuilder(anyString())).thenReturn(spanBuilder);
        lenient().when(spanBuilder.setAttribute(anyString(), any())).thenReturn(spanBuilder);
        lenient().when(spanBuilder.setAttribute(anyString(), anyDouble())).thenReturn(spanBuilder);
        lenient().when(spanBuilder.startSpan()).thenReturn(span);
        lenient().when(span.makeCurrent()).thenReturn(mock(Scope.class));
    }

    @Test
    void createLoanShouldSaveWithPendingStatusAndSendEvent() {
        Loan savedLoan = new Loan("cust-001", new BigDecimal("10000"), "PENDING");
        savedLoan.setId("loan-123");

        when(loanRepository.save(any(Loan.class))).thenReturn(savedLoan);

        Loan result = loanService.createLoan("cust-001", new BigDecimal("10000"));

        assertEquals("PENDING", result.getStatus());
        assertEquals("cust-001", result.getCustomerId());
        assertEquals(new BigDecimal("10000"), result.getAmount());

        ArgumentCaptor<Loan> loanCaptor = ArgumentCaptor.forClass(Loan.class);
        verify(loanRepository).save(loanCaptor.capture());
        assertEquals("PENDING", loanCaptor.getValue().getStatus());
        assertEquals("cust-001", loanCaptor.getValue().getCustomerId());

        verify(loanProducer).sendLoanRequested(any());
    }

    @Test
    void getLoanShouldReturnLoanWhenFound() {
        Loan loan = new Loan("cust-001", new BigDecimal("5000"), "APPROVED");
        loan.setId("loan-456");
        when(loanRepository.findById("loan-456")).thenReturn(Optional.of(loan));

        Optional<Loan> result = loanService.getLoan("loan-456");

        assertTrue(result.isPresent());
        assertEquals("APPROVED", result.get().getStatus());
        assertEquals("loan-456", result.get().getId());
    }

    @Test
    void getLoanShouldReturnEmptyWhenNotFound() {
        when(loanRepository.findById("nonexistent")).thenReturn(Optional.empty());

        Optional<Loan> result = loanService.getLoan("nonexistent");

        assertFalse(result.isPresent());
    }

    @Test
    void getAllLoansShouldReturnAllLoans() {
        List<Loan> loans = List.of(
            new Loan("cust-001", new BigDecimal("10000"), "PENDING"),
            new Loan("cust-002", new BigDecimal("20000"), "APPROVED")
        );
        when(loanRepository.findAll()).thenReturn(loans);

        List<Loan> result = loanService.getAllLoans();

        assertEquals(2, result.size());
        verify(loanRepository).findAll();
    }

    @Test
    void updateLoanStatusShouldUpdateWhenLoanExists() {
        Loan loan = new Loan("cust-001", new BigDecimal("10000"), "PENDING");
        loan.setId("loan-789");
        when(loanRepository.findById("loan-789")).thenReturn(Optional.of(loan));

        loanService.updateLoanStatus("loan-789", "APPROVED");

        assertEquals("APPROVED", loan.getStatus());
        verify(loanRepository).save(loan);
    }

    @Test
    void updateLoanStatusShouldNotSaveWhenLoanNotFound() {
        when(loanRepository.findById("nonexistent")).thenReturn(Optional.empty());

        loanService.updateLoanStatus("nonexistent", "APPROVED");

        verify(loanRepository, never()).save(any());
    }
}

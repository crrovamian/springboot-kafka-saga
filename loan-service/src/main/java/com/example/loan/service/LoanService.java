package com.example.loan.service;

import com.example.loan.entity.Loan;
import com.example.loan.repository.LoanRepository;
import com.example.loan.kafka.LoanProducer;
import com.example.events.LoanRequested;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class LoanService {
    private final LoanRepository loanRepository;
    private final LoanProducer loanProducer;
    private final Tracer tracer;

    public LoanService(LoanRepository loanRepository, LoanProducer loanProducer, Tracer tracer) {
        this.loanRepository = loanRepository;
        this.loanProducer = loanProducer;
        this.tracer = tracer;
    }

    public Loan createLoan(String customerId, BigDecimal amount) {
        Span span = tracer.spanBuilder("LoanService.createLoan")
            .setAttribute("customer.id", customerId)
            .setAttribute("loan.amount", amount.doubleValue())
            .startSpan();
        try (Scope ignored = span.makeCurrent()) {
            Loan loan = new Loan(customerId, amount, "PENDING");
            loan = loanRepository.save(loan);

            LoanRequested event = new LoanRequested(
                loan.getId(),
                customerId,
                amount,
                LocalDateTime.now().toString()
            );
            loanProducer.sendLoanRequested(event);

            span.setAttribute("loan.id", loan.getId());
            return loan;
        } catch (Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR);
            throw e;
        } finally {
            span.end();
        }
    }

    public Optional<Loan> getLoan(String id) {
        return loanRepository.findById(id);
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    public void updateLoanStatus(String loanId, String status) {
        loanRepository.findById(loanId).ifPresent(loan -> {
            loan.setStatus(status);
            loanRepository.save(loan);
        });
    }
}

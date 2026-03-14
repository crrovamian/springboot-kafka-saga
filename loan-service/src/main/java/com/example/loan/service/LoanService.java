package com.example.loan.service;

import com.example.loan.entity.Loan;
import com.example.loan.repository.LoanRepository;
import com.example.loan.kafka.LoanProducer;
import com.example.events.LoanRequested;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class LoanService {
    private final LoanRepository loanRepository;
    private final LoanProducer loanProducer;

    public LoanService(LoanRepository loanRepository, LoanProducer loanProducer) {
        this.loanRepository = loanRepository;
        this.loanProducer = loanProducer;
    }

    public Loan createLoan(String customerId, BigDecimal amount) {
        Loan loan = new Loan(customerId, amount, "PENDING");
        loan = loanRepository.save(loan);

        LoanRequested event = new LoanRequested(
            loan.getId(),
            customerId,
            amount,
            LocalDateTime.now().toString()
        );
        loanProducer.sendLoanRequested(event);

        return loan;
    }

    public Optional<Loan> getLoan(String id) {
        return loanRepository.findById(id);
    }

    public void updateLoanStatus(String loanId, String status) {
        loanRepository.findById(loanId).ifPresent(loan -> {
            loan.setStatus(status);
            loanRepository.save(loan);
        });
    }
}

package com.example.loan.kafka;

import com.example.events.LoanRequested;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class LoanProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public LoanProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Retry(name = "loanRetry")
    public void sendLoanRequested(LoanRequested event) {
        try {
            kafkaTemplate.send("loan-requested", event.getLoanId(), event)
                .get(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Kafka send interrupted", e);
        } catch (ExecutionException | TimeoutException e) {
            throw new RuntimeException("Kafka send failed", e);
        }
    }
}

package com.example.loan.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Respuesta de préstamo")
public record LoanResponse(
    @Schema(description = "ID único del préstamo", example = "550e8400-e29b-41d4-a716-446655440000")
    String id,
    
    @Schema(description = "ID del cliente", example = "cust-001")
    String customerId,
    
    @Schema(description = "Monto del préstamo", example = "10000.00")
    BigDecimal amount,
    
    @Schema(description = "Estado del préstamo", example = "PENDING",
        allowableValues = {"PENDING", "APPROVED", "REJECTED", "FAILED"})
    String status,
    
    @Schema(description = "Fecha de creación", example = "2024-01-15T10:30:00")
    LocalDateTime createdAt
) {}

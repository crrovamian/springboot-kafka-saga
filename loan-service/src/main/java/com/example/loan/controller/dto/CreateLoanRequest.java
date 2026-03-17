package com.example.loan.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request para crear un préstamo")
public record CreateLoanRequest(
    @Schema(description = "ID del cliente", 
        example = "cust-001",
        allowableValues = {"cust-001", "cust-002", "cust-003"})
    String customerId,
    
    @Schema(description = "Monto del préstamo", 
        example = "10000")
    String amount
) {}

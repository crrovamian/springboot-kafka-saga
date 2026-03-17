package com.example.loan.controller;

import com.example.loan.controller.dto.CreateLoanRequest;
import com.example.loan.controller.dto.LoanResponse;
import com.example.loan.entity.Loan;
import com.example.loan.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@Tag(name = "Préstamos", description = "API para gestión de préstamos")
public class LoanController {
    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping
    @Operation(summary = "Listar todos los préstamos", description = "Obtiene todos los préstamos registrados")
    @ApiResponse(responseCode = "200", description = "Lista de préstamos",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = LoanResponse.class),
            examples = @ExampleObject(name = "Ejemplo respuesta",
                value = """
                    [
                      {
                        "id": "550e8400-e29b-41d4-a716-446655440000",
                        "customerId": "cust-001",
                        "amount": 10000.00,
                        "status": "APPROVED",
                        "createdAt": "2024-01-15T10:30:00"
                      },
                      {
                        "id": "660e8400-e29b-41d4-a716-446655440001",
                        "customerId": "cust-002",
                        "amount": 25000.00,
                        "status": "PENDING",
                        "createdAt": "2024-01-15T11:00:00"
                      }
                    ]
                    """)))
    public ResponseEntity<List<LoanResponse>> getAllLoans() {
        List<LoanResponse> loans = loanService.getAllLoans().stream()
            .map(this::toResponse)
            .toList();
        return ResponseEntity.ok(loans);
    }

    @PostMapping
    @Operation(summary = "Crear un préstamo", description = "Crea un nuevo préstamo para un cliente")
    public ResponseEntity<LoanResponse> createLoan(@RequestBody CreateLoanRequest request) {
        if (request.customerId() == null || request.amount() == null) {
            return ResponseEntity.badRequest().build();
        }
        Loan loan = loanService.createLoan(request.customerId(), new java.math.BigDecimal(request.amount()));
        return ResponseEntity.ok(toResponse(loan));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener préstamo", description = "Obtiene un préstamo por su ID")
    @ApiResponse(responseCode = "200", description = "Préstamo encontrado",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = LoanResponse.class),
            examples = @ExampleObject(name = "Ejemplo respuesta",
                value = """
                    {
                      "id": "550e8400-e29b-41d4-a716-446655440000",
                      "customerId": "cust-001",
                      "amount": 10000.00,
                      "status": "APPROVED",
                      "createdAt": "2024-01-15T10:30:00"
                    }
                    """)))
    @ApiResponse(responseCode = "404", description = "Préstamo no encontrado")
    public ResponseEntity<LoanResponse> getLoan(@PathVariable String id) {
        return loanService.getLoan(id)
            .map(loan -> ResponseEntity.ok(toResponse(loan)))
            .orElse(ResponseEntity.notFound().build());
    }

    private LoanResponse toResponse(Loan loan) {
        return new LoanResponse(
            loan.getId(),
            loan.getCustomerId(),
            loan.getAmount(),
            loan.getStatus(),
            loan.getCreatedAt()
        );
    }
}

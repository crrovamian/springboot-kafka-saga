# Spring Boot Kafka Saga

A distributed saga pattern implementation using Spring Boot and Apache Kafka.

## Architecture

The project consists of 3 microservices:

- **loan-service**: Handles loan requests and coordinates the saga
- **risk-service**: Evaluates customer risk for loan approval
- **payment-service**: Processes payments and reserves funds
- **shared-events**: Common event classes shared across services

## Saga Flow

```mermaid
%%{init: {'themeCSS': '.messageLine0:nth-of-type(4) { stroke: #22c55e; stroke-width: 3px; }; .messageLine0:nth-of-type(5) { stroke: #22c55e; stroke-width: 3px; }; .messageLine0:nth-of-type(6) { stroke: #22c55e; stroke-width: 3px; }; .messageLine0:nth-of-type(7) { stroke: #22c55e; stroke-width: 3px; }; .messageLine0:nth-of-type(8) { stroke: #22c55e; stroke-width: 3px; }; .messageLine0:nth-of-type(9) { stroke: #dc3545; stroke-width: 3px; }; .messageLine0:nth-of-type(10) { stroke: #dc3545; stroke-width: 3px; }; .messageLine0:nth-of-type(11) { stroke: #dc3545; stroke-width: 3px; }; .messageLine0:nth-of-type(12) { stroke: #dc3545; stroke-width: 3px; }; .messageLine0:nth-of-type(13) { stroke: #dc3545; stroke-width: 3px; }; .messageLine0:nth-of-type(14) { stroke: #dc3545; stroke-width: 3px; }'}}%%
sequenceDiagram
    participant Client
    participant loan-service
    participant Kafka
    participant risk-service
    participant payment-service

    Client->>loan-service: POST /api/loans
    loan-service->>loan-service: Create Loan (PENDING)
    loan-service->>Kafka: LoanRequested
    Kafka->>risk-service: Consume LoanRequested
    risk-service->>risk-service: Evaluate Risk
    
    alt Risk Approved
        risk-service->>Kafka: RiskApproved
        Kafka->>payment-service: Consume RiskApproved
        payment-service->>payment-service: Reserve Funds
        
        alt Funds Reserved
            payment-service->>Kafka: FundsReserved
            Kafka->>loan-service: Consume FundsReserved
            loan-service->>loan-service: Update to APPROVED
            loan-service-->>Client: Loan Approved
        else Funds Failed
            payment-service->>Kafka: FundsFailed
            Kafka->>loan-service: Consume FundsFailed
            loan-service->>loan-service: Update to FAILED
            loan-service-->>Client: Loan Failed
        end
    else Risk Rejected
        risk-service->>Kafka: RiskRejected
        Kafka->>loan-service: Consume RiskRejected
        loan-service->>loan-service: Update to REJECTED
        loan-service-->>Client: Loan Rejected
    end
```

**Compensation**: On failure at any step, the saga publishes a compensating event to rollback the previous steps.
2. `loan-service` publishes `LoanRequested` event to Kafka
3. `risk-service` consumes the event, evaluates risk, publishes `RiskApproved` or `RiskRejected`
4. `payment-service` consumes `RiskApproved`, processes payment, publishes `FundsReserved` or `FundsFailed`
5. `loan-service` consumes the final event and updates loan status

## Kafka Topics

- `loan-requested`
- `risk-approved`
- `risk-rejected`
- `funds-reserved`
- `funds-failed`

## Prerequisites

- Docker (para Kafka)
- Java 17+ y Maven

## Quick Start

```bash
# 1. Iniciar Kafka
docker network create kafka-net
docker compose -f .devcontainer/kafka-compose.yml up -d

# 2. Compilar e iniciar todos los servicios
./start.sh

# 3. Detener servicios
./stop.sh
```

## Build manual

```bash
mvn clean install -DskipTests
```

## Run individual

```bash
mvn -pl loan-service spring-boot:run   # puerto 9080
mvn -pl risk-service spring-boot:run   # puerto 8081
mvn -pl payment-service spring-boot:run # puerto 8082
```

## Usage

```bash
curl -X POST http://localhost:9080/api/loans \
  -H "Content-Type: application/json" \
  -d '{"customerId": "cust-001", "amount": 10000}'
```

## Test Customers

- `cust-001`: LOW risk, $50,000 limit
- `cust-002`: MEDIUM risk, $30,000 limit
- `cust-003`: HIGH risk (rejected automatically)

## OpenAPI / Swagger

- **loan-service**: http://localhost:9080/swagger-ui.html
- **loan-service** (JSON): http://localhost:9080/v3/api-docs

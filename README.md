# Spring Boot Kafka Saga Example

A distributed saga pattern implementation using Spring Boot and Apache Kafka.

## Architecture

The project consists of 3 microservices:

- **loan-service**: Handles loan requests and coordinates the saga
- **risk-service**: Evaluates customer risk for loan approval
- **payment-service**: Processes payments and reserves funds
- **shared-events**: Common event classes shared across services

## Saga Flow

1. Client creates a loan request via `POST /api/loans`
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

## Build

```bash
cd shared-events && mvn install
cd loan-service && mvn spring-boot:run
cd risk-service && mvn spring-boot:run
cd payment-service && mvn spring-boot:run
```

## Usage

```bash
curl -X POST http://localhost:8080/api/loans \
  -H "Content-Type: application/json" \
  -d '{"customerId": "cust-001", "amount": 10000}'
```

## Test Customers

- `cust-001`: LOW risk, $50,000 limit
- `cust-002`: MEDIUM risk, $30,000 limit
- `cust-003`: HIGH risk (rejected automatically)

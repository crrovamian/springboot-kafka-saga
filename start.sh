#!/bin/bash
set -e

echo "=== Compilando proyecto ==="
mvn clean install -DskipTests

echo ""
echo "=== Iniciando servicios Saga ==="

mvn -pl loan-service spring-boot:run &
PID_LOAN=$!

mvn -pl risk-service spring-boot:run &
PID_RISK=$!

mvn -pl payment-service spring-boot:run &
PID_PAYMENT=$!

echo "loan-service (9080) PID: $PID_LOAN"
echo "risk-service (8081) PID: $PID_RISK"
echo "payment-service (8082) PID: $PID_PAYMENT"
echo ""
echo "Usa './stop.sh' para detenerlos o 'kill PID' manualmente."

trap "kill $PID_LOAN $PID_RISK $PID_PAYMENT 2>/dev/null" EXIT SIGINT SIGTERM

wait

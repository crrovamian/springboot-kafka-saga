#!/bin/bash
echo "=== Deteniendo servicios Saga ==="

pkill -f "loan-service" 2>/dev/null && echo "loan-service detenido" || echo "loan-service no estaba corriendo"
pkill -f "risk-service" 2>/dev/null && echo "risk-service detenido" || echo "risk-service no estaba corriendo"
pkill -f "payment-service" 2>/dev/null && echo "payment-service detenido" || echo "payment-service no estaba corriendo"

echo "=== Listo ==="

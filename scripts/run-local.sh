#!/bin/bash
# Local application runner with docker-compose PostgreSQL

set -e  # Exit on error

echo "=========================================="
echo "Govinda ERP - Local App Runner"
echo "=========================================="
echo ""

echo "→ Starting PostgreSQL container..."
docker-compose -f infrastructure/docker/docker-compose.yml up -d postgres

echo "→ Waiting for PostgreSQL to be ready..."
sleep 3
until docker-compose -f infrastructure/docker/docker-compose.yml exec -T postgres pg_isready -U govinda > /dev/null 2>&1; do
    echo "   Waiting for PostgreSQL..."
    sleep 1
done
echo "✅ PostgreSQL is ready"
echo ""

echo "→ Starting Spring Boot app..."
exec mvn -pl backend/govinda-app -am spring-boot:run

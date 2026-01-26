#!/bin/bash
# Test runner simulating CI environment (uses service PostgreSQL instead of Testcontainers)

# Start PostgreSQL if not running
docker-compose -f infrastructure/docker/docker-compose.yml up -d postgres

# Wait for PostgreSQL to be ready
sleep 2

# Set CI environment variables
export SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/govinda"
export SPRING_DATASOURCE_USERNAME="govinda"
export SPRING_DATASOURCE_PASSWORD="govinda"

# Run tests
mvn test "$@"

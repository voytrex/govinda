#!/bin/bash
# Local test runner - configures Testcontainers to use Colima

export DOCKER_HOST="unix://${HOME}/.colima/default/docker.sock"

# Run tests
mvn test "$@"

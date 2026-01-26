#!/bin/bash
# Local test runner - configures Testcontainers to use Colima
# Runs code quality checks, unit tests, integration tests, and coverage

set -e  # Exit on error

export DOCKER_HOST="unix://${HOME}/.colima/default/docker.sock"

echo "=========================================="
echo "Govinda ERP - Local Test Runner"
echo "=========================================="
echo ""

# Parse arguments
SKIP_QUALITY=false
SKIP_UNIT=false
SKIP_INTEGRATION=false
SKIP_COVERAGE=false

while [[ $# -gt 0 ]]; do
    case $1 in
        --skip-quality)
            SKIP_QUALITY=true
            shift
            ;;
        --skip-unit)
            SKIP_UNIT=true
            shift
            ;;
        --skip-integration)
            SKIP_INTEGRATION=true
            shift
            ;;
        --skip-coverage)
            SKIP_COVERAGE=true
            shift
            ;;
        --unit-only)
            SKIP_QUALITY=true
            SKIP_INTEGRATION=true
            SKIP_COVERAGE=true
            shift
            ;;
        --integration-only)
            SKIP_QUALITY=true
            SKIP_UNIT=true
            SKIP_COVERAGE=true
            shift
            ;;
        *)
            echo "Unknown option: $1"
            echo "Usage: $0 [--skip-quality] [--skip-unit] [--skip-integration] [--skip-coverage] [--unit-only] [--integration-only]"
            exit 1
            ;;
    esac
done

# 1. Code Quality Checks
if [ "$SKIP_QUALITY" = false ]; then
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "1. Running Code Quality Checks"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo ""
    
    echo "→ Checkstyle..."
    (cd backend && mvn -B checkstyle:check) || {
        echo "❌ Checkstyle failed. Fix violations and try again."
        exit 1
    }
    echo "✅ Checkstyle passed"
    echo ""
    
    echo "→ SpotBugs..."
    (cd backend && mvn -B spotbugs:check) || {
        echo "⚠️  SpotBugs found issues (non-blocking)"
    }
    echo "✅ SpotBugs completed"
    echo ""
else
    echo "⏭️  Skipping code quality checks"
    echo ""
fi

# 2. Build
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "2. Building Project"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
(cd backend && mvn -B clean compile -DskipTests)
echo "✅ Build successful"
echo ""

# 3. Prepare JaCoCo Agent (if coverage will be generated)
if [ "$SKIP_COVERAGE" = false ]; then
    echo "→ Preparing JaCoCo agent..."
    (cd backend && mvn -B jacoco:prepare-agent -q)
fi

# 4. Unit Tests
if [ "$SKIP_UNIT" = false ]; then
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "3. Running Unit Tests"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo ""
    (cd backend && mvn -B surefire:test) || {
        echo "❌ Unit tests failed"
        exit 1
    }
    echo "✅ Unit tests passed"
    echo ""
else
    echo "⏭️  Skipping unit tests"
    echo ""
fi

# 5. Integration Tests
if [ "$SKIP_INTEGRATION" = false ]; then
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "4. Running Integration Tests"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo ""
    (cd backend && mvn -B failsafe:integration-test) || {
        echo "❌ Integration tests failed"
        exit 1
    }
    (cd backend && mvn -B failsafe:verify) || {
        echo "❌ Integration test verification failed"
        exit 1
    }
    echo "✅ Integration tests passed"
    echo ""
else
    echo "⏭️  Skipping integration tests"
    echo ""
fi

# 6. Coverage Report
if [ "$SKIP_COVERAGE" = false ]; then
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "5. Generating Coverage Report"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo ""
    # Run from backend directory where modules are
    (cd backend && mvn -B jacoco:report)
    echo "✅ Coverage report generated"
    echo ""
    echo "📊 Coverage reports available at:"
    find backend -name "index.html" -path "*/site/jacoco/*" 2>/dev/null | while read -r report; do
        echo "   → $report"
    done
    echo ""
    
    # Check coverage thresholds
    echo "→ Checking coverage thresholds..."
    (cd backend && mvn -B jacoco:check) || {
        echo "⚠️  Coverage below threshold (non-blocking)"
    }
    echo ""
else
    echo "⏭️  Skipping coverage report"
    echo ""
fi

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "✅ All checks completed successfully!"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

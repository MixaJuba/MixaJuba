#!/usr/bin/env bash
# ================================================================================
# 🧪 run-tests.sh - Test Runner | Запуск Тестів
# ================================================================================
# File Purpose: Запускає всі тести проекту (unit, integration)
# Role: Забезпечує коректність функціональності перед комітом
# 
# Usage: 
#   ./scripts/run-tests.sh              # Run all tests
#   ./scripts/run-tests.sh unit         # Only unit tests
#   ./scripts/run-tests.sh integration  # Only integration tests
# ================================================================================

set -e  # Exit on error
set -u  # Exit on undefined variable

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

# Script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

echo -e "${BLUE}🧪 QuantumForce_Code | Test Runner${NC}"
echo -e "${BLUE}=================================================================================${NC}"

cd "$PROJECT_ROOT"

# Parse arguments
TEST_TYPE="${1:-all}"

# ===== Unit Tests =====
if [ "$TEST_TYPE" = "all" ] || [ "$TEST_TYPE" = "unit" ]; then
    echo -e "\n${YELLOW}🧪 Running unit tests...${NC}"
    ./gradlew test --info
    
    echo -e "\n${GREEN}✅ Unit tests completed!${NC}"
    echo -e "${BLUE}Reports:${NC}"
    echo -e "  - HTML: build/reports/tests/test/index.html"
    echo -e "  - XML:  build/test-results/test/"
fi

# ===== Integration Tests =====
if [ "$TEST_TYPE" = "all" ] || [ "$TEST_TYPE" = "integration" ]; then
    echo -e "\n${YELLOW}🔗 Running integration tests...${NC}"
    
    # Check if emulator/device is connected
    if ! adb devices | grep -q "device$"; then
        echo -e "${RED}❌ No Android device/emulator detected${NC}"
        echo -e "${YELLOW}💡 Start emulator or connect device first${NC}"
        exit 1
    fi
    
    ./gradlew connectedAndroidTest --info
    
    echo -e "\n${GREEN}✅ Integration tests completed!${NC}"
    echo -e "${BLUE}Reports:${NC}"
    echo -e "  - HTML: app/build/reports/androidTests/connected/index.html"
fi

# ===== Code Coverage =====
echo -e "\n${YELLOW}📊 Generating code coverage report...${NC}"
./gradlew jacocoTestReport

echo -e "\n${GREEN}=================================================================================${NC}"
echo -e "${GREEN}✅ All tests passed! 🎉${NC}"
echo -e "${GREEN}=================================================================================${NC}"
echo -e "\n${BLUE}Coverage Report:${NC}"
echo -e "  - build/reports/jacoco/test/html/index.html"
echo ""

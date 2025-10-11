#!/usr/bin/env bash
# ================================================================================
# 🔥 verify-local.sh - Pre-PR Local Verification | Локальна Перевірка Перед PR
# ================================================================================
# File Purpose: Запускає всі перевірки локально перед створенням PR
# Role: Забезпечує якість коду та запобігає помилкам у CI
# 
# Usage: ./scripts/verify-local.sh
# ================================================================================

set -e  # Exit on error
set -u  # Exit on undefined variable

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

echo -e "${BLUE}🚀 QuantumForce_Code | Local Verification${NC}"
echo -e "${BLUE}=================================================================================${NC}"

cd "$PROJECT_ROOT"

# ===== Step 1: Code Formatting Check =====
echo -e "\n${YELLOW}📝 Step 1/5: Checking code formatting (ktlint)...${NC}"
if ./gradlew ktlintCheck --quiet 2>/dev/null; then
    echo -e "${GREEN}✅ Code formatting: PASSED${NC}"
else
    echo -e "${RED}❌ Code formatting: FAILED${NC}"
    echo -e "${YELLOW}💡 Run: ./scripts/format-code.sh to auto-fix${NC}"
    exit 1
fi

# ===== Step 2: Static Analysis =====
echo -e "\n${YELLOW}🔍 Step 2/5: Running static analysis (detekt)...${NC}"
if ./gradlew detekt --quiet 2>/dev/null; then
    echo -e "${GREEN}✅ Static analysis: PASSED${NC}"
else
    echo -e "${RED}❌ Static analysis: FAILED${NC}"
    echo -e "${YELLOW}💡 Check: build/reports/detekt/detekt.html${NC}"
    exit 1
fi

# ===== Step 3: Unit Tests =====
echo -e "\n${YELLOW}🧪 Step 3/5: Running unit tests...${NC}"
if ./gradlew test --quiet 2>/dev/null; then
    echo -e "${GREEN}✅ Unit tests: PASSED${NC}"
else
    echo -e "${RED}❌ Unit tests: FAILED${NC}"
    echo -e "${YELLOW}💡 Check: build/reports/tests/test/index.html${NC}"
    exit 1
fi

# ===== Step 4: Build Check =====
echo -e "\n${YELLOW}🔨 Step 4/5: Building project...${NC}"
if ./gradlew assembleDebug --quiet 2>/dev/null; then
    echo -e "${GREEN}✅ Build: PASSED${NC}"
else
    echo -e "${RED}❌ Build: FAILED${NC}"
    exit 1
fi

# ===== Step 5: Lint Check =====
echo -e "\n${YELLOW}🔎 Step 5/5: Running Android lint...${NC}"
if ./gradlew lint --quiet 2>/dev/null; then
    echo -e "${GREEN}✅ Lint: PASSED${NC}"
else
    echo -e "${RED}❌ Lint: FAILED${NC}"
    echo -e "${YELLOW}💡 Check: app/build/reports/lint-results.html${NC}"
    exit 1
fi

# ===== Success =====
echo -e "\n${GREEN}=================================================================================${NC}"
echo -e "${GREEN}✅ All checks passed! Ready to create PR 🚀${NC}"
echo -e "${GREEN}=================================================================================${NC}"
echo -e "\n${BLUE}Next steps:${NC}"
echo -e "  1. git add ."
echo -e "  2. git commit -m 'Your message'"
echo -e "  3. git push"
echo -e "  4. Create PR on GitHub"
echo ""

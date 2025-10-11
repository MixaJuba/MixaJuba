#!/usr/bin/env bash
# ================================================================================
# ✨ format-code.sh - Code Formatter | Форматування Коду
# ================================================================================
# File Purpose: Автоматично форматує Kotlin код за правилами ktlint
# Role: Забезпечує консистентний стиль коду у всьому проекті
# 
# Usage: ./scripts/format-code.sh
# ================================================================================

set -e  # Exit on error
set -u  # Exit on undefined variable

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

echo -e "${BLUE}✨ QuantumForce_Code | Code Formatter${NC}"
echo -e "${BLUE}=================================================================================${NC}"

cd "$PROJECT_ROOT"

# ===== Run ktlint Format =====
echo -e "\n${YELLOW}📝 Formatting Kotlin code with ktlint...${NC}"
./gradlew ktlintFormat

echo -e "\n${GREEN}=================================================================================${NC}"
echo -e "${GREEN}✅ Code formatted successfully!${NC}"
echo -e "${GREEN}=================================================================================${NC}"
echo -e "\n${BLUE}Files changed:${NC}"
git diff --name-only --diff-filter=M | grep -E '\.(kt|kts)$' || echo "  No changes detected"
echo ""

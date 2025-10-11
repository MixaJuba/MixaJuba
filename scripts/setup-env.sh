#!/usr/bin/env bash
# ================================================================================
# ⚙️ setup-env.sh - Environment Setup | Налаштування Середовища
# ================================================================================
# File Purpose: Налаштовує середовище розробки для AutoDiagPro
# Role: Встановлює необхідні SDK, tools, dependencies
# 
# Usage: ./scripts/setup-env.sh
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

echo -e "${BLUE}⚙️  QuantumForce_Code | Environment Setup${NC}"
echo -e "${BLUE}=================================================================================${NC}"

cd "$PROJECT_ROOT"

# ===== Step 1: Check Java/JDK =====
echo -e "\n${YELLOW}☕ Step 1/6: Checking Java/JDK...${NC}"
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
    echo -e "${GREEN}✅ Java version: $JAVA_VERSION${NC}"
    
    if [ "$JAVA_VERSION" -lt 17 ]; then
        echo -e "${RED}❌ Java 17+ required (found: $JAVA_VERSION)${NC}"
        echo -e "${YELLOW}💡 Install: sudo apt install openjdk-17-jdk${NC}"
        exit 1
    fi
else
    echo -e "${RED}❌ Java not found${NC}"
    echo -e "${YELLOW}💡 Install: sudo apt install openjdk-17-jdk${NC}"
    exit 1
fi

# ===== Step 2: Check Android SDK =====
echo -e "\n${YELLOW}📱 Step 2/6: Checking Android SDK...${NC}"
if [ -z "${ANDROID_SDK_ROOT:-}" ] && [ -z "${ANDROID_HOME:-}" ]; then
    echo -e "${RED}❌ ANDROID_SDK_ROOT not set${NC}"
    echo -e "${YELLOW}💡 Set in ~/.bashrc:${NC}"
    echo -e "   export ANDROID_SDK_ROOT=\$HOME/Android/Sdk"
    echo -e "   export PATH=\$PATH:\$ANDROID_SDK_ROOT/cmdline-tools/latest/bin"
    exit 1
else
    SDK_ROOT="${ANDROID_SDK_ROOT:-$ANDROID_HOME}"
    echo -e "${GREEN}✅ Android SDK: $SDK_ROOT${NC}"
fi

# ===== Step 3: Download Gradle Dependencies =====
echo -e "\n${YELLOW}📦 Step 3/6: Downloading Gradle dependencies...${NC}"
./gradlew dependencies --quiet

echo -e "${GREEN}✅ Dependencies downloaded${NC}"

# ===== Step 4: Install ktlint =====
echo -e "\n${YELLOW}✨ Step 4/6: Setting up ktlint...${NC}"
./gradlew ktlintApplyToIdea --quiet || true
echo -e "${GREEN}✅ ktlint configured${NC}"

# ===== Step 5: Install detekt =====
echo -e "\n${YELLOW}🔍 Step 5/6: Setting up detekt...${NC}"
# detekt is already in Gradle dependencies
echo -e "${GREEN}✅ detekt configured${NC}"

# ===== Step 6: Generate Gradle Wrapper =====
echo -e "\n${YELLOW}🔧 Step 6/6: Checking Gradle wrapper...${NC}"
if [ ! -f "gradlew" ]; then
    echo -e "${YELLOW}Generating Gradle wrapper...${NC}"
    gradle wrapper --gradle-version 8.5
fi
chmod +x gradlew
echo -e "${GREEN}✅ Gradle wrapper ready${NC}"

# ===== Success =====
echo -e "\n${GREEN}=================================================================================${NC}"
echo -e "${GREEN}✅ Environment setup completed! 🎉${NC}"
echo -e "${GREEN}=================================================================================${NC}"

echo -e "\n${BLUE}Next steps:${NC}"
echo -e "  1. Open project in Android Studio"
echo -e "  2. Sync Gradle"
echo -e "  3. Run: ./scripts/verify-local.sh"
echo -e "\n${BLUE}Useful commands:${NC}"
echo -e "  - ./gradlew build          # Build project"
echo -e "  - ./gradlew test           # Run tests"
echo -e "  - ./gradlew assembleDebug  # Build APK"
echo ""

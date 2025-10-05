# ⚙️ scripts/ - Automation Scripts | Скрипти Автоматизації

## 📋 File Purpose | Призначення
Набір Bash скриптів для автоматизації рутинних завдань розробки: форматування, тестування, збірка, перевірка середовища.

## 🎯 Role | Роль
Спрощує DevOps процеси, забезпечує консистентність середовища, запобігає помилкам перед PR.

---

## 📂 Доступні Скрипти / Available Scripts

### ⚙️ setup-env.sh
**Призначення:** Налаштування середовища розробки (initial setup).

**Що робить:**
- ✅ Перевіряє Java/JDK 17+
- ✅ Перевіряє Android SDK
- ✅ Завантажує Gradle dependencies
- ✅ Налаштовує ktlint + detekt
- ✅ Генерує Gradle wrapper

**Коли використовувати:**
- При першому клонуванні проекту
- Після зміни середовища (нова машина, Docker container)
- Якщо є проблеми з dependencies

**Використання:**
```bash
./scripts/setup-env.sh
```

**Вимоги:**
- Ubuntu/Debian (або macOS з Homebrew)
- Internet connection для завантаження dependencies
- ~2GB вільного місця для SDK

---

### ✨ format-code.sh
**Призначення:** Автоматичне форматування Kotlin коду.

**Що робить:**
- ✅ Запускає `ktlintFormat`
- ✅ Виправляє indentation, spacing, imports
- ✅ Показує змінені файли

**Коли використовувати:**
- Перед комітом (якщо CI падає на `ktlintCheck`)
- Після merge (щоб уніфікувати стиль)
- При рефакторингу великих файлів

**Використання:**
```bash
./scripts/format-code.sh
```

**Після виконання:**
- Перевірте зміни: `git diff`
- Додайте до коміту: `git add .`

**Конфігурація ktlint:**
- `.editorconfig` - rules для форматування
- `build.gradle.kts` - ktlint plugin setup

---

### 🧪 run-tests.sh
**Призначення:** Запуск всіх тестів проекту.

**Що робить:**
- ✅ Запускає unit tests (JUnit)
- ✅ Запускає integration tests (Espresso)
- ✅ Генерує code coverage (Jacoco)

**Коли використовувати:**
- Перед PR (щоб переконатися, що тести проходять)
- Після додавання нових тестів
- Для локального debugging тестів

**Використання:**
```bash
# Всі тести
./scripts/run-tests.sh

# Тільки unit tests
./scripts/run-tests.sh unit

# Тільки integration tests (потребує emulator/device)
./scripts/run-tests.sh integration
```

**Звіти:**
- **Unit tests:** `build/reports/tests/test/index.html`
- **Integration:** `app/build/reports/androidTests/connected/index.html`
- **Coverage:** `build/reports/jacoco/test/html/index.html`

**Troubleshooting:**
```bash
# Якщо integration tests падають:
adb devices  # Перевірити підключення
adb logcat -c && adb logcat  # Дивитися логи

# Якщо unit tests падають:
./gradlew test --info --stacktrace  # Детальні логи
```

---

### ✅ verify-local.sh
**Призначення:** Повна перевірка проекту перед PR (Pre-PR checks).

**Що робить:**
1. **Code formatting** - ktlintCheck
2. **Static analysis** - detekt
3. **Unit tests** - `./gradlew test`
4. **Build** - `./gradlew assembleDebug`
5. **Lint** - Android lint

**Коли використовувати:**
- **Завжди перед PR!** (щоб CI не падав)
- Після великих змін
- Перед merge у main

**Використання:**
```bash
./scripts/verify-local.sh
```

**Якщо падає:**
- **ktlintCheck:** `./scripts/format-code.sh`
- **detekt:** Виправити code smells вручну (див. `build/reports/detekt/detekt.html`)
- **tests:** Виправити тести або код
- **build:** Виправити compile errors
- **lint:** Виправити Android-specific issues (див. `app/build/reports/lint-results.html`)

**Приклад виводу:**
```
🚀 QuantumForce_Code | Local Verification
=================================================================================

📝 Step 1/5: Checking code formatting (ktlint)...
✅ Code formatting: PASSED

🔍 Step 2/5: Running static analysis (detekt)...
✅ Static analysis: PASSED

🧪 Step 3/5: Running unit tests...
✅ Unit tests: PASSED

🔨 Step 4/5: Building project...
✅ Build: PASSED

🔎 Step 5/5: Running Android lint...
✅ Lint: PASSED

=================================================================================
✅ All checks passed! Ready to create PR 🚀
=================================================================================
```

---

## 🔄 Workflow: Типовий Процес Розробки

```bash
# 1. Initial setup (один раз)
./scripts/setup-env.sh

# 2. Розробка фічі
# ... edit files ...

# 3. Форматування коду
./scripts/format-code.sh

# 4. Локальні тести
./scripts/run-tests.sh

# 5. Перевірка перед PR
./scripts/verify-local.sh

# 6. Коміт і push
git add .
git commit -m "feat: Add new feature"
git push

# 7. Створити PR на GitHub
# CI автоматично запустить ті ж перевірки
```

---

## 🛠️ Налаштування / Configuration

### Додати новий скрипт:
1. Створити файл: `scripts/my-script.sh`
2. Додати shebang: `#!/usr/bin/env bash`
3. Додати `set -e` (exit on error)
4. Зробити executable: `chmod +x scripts/my-script.sh`
5. Додати документацію у цей README

### Загальні правила для скриптів:
```bash
#!/usr/bin/env bash
# Header з описом
set -e  # Exit on error
set -u  # Exit on undefined variable

# Colors для output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

cd "$PROJECT_ROOT"

# ... logic ...

echo -e "${GREEN}✅ Success${NC}"
```

---

## 🔧 CI/CD Integration

### GitHub Actions
Скрипти використовуються у `.github/workflows/android-ci.yml`:

```yaml
- name: Setup Environment
  run: ./scripts/setup-env.sh

- name: Run Tests
  run: ./scripts/run-tests.sh

- name: Verify Code Quality
  run: ./scripts/verify-local.sh
```

### Local Pre-commit Hook (опціонально)
```bash
# .git/hooks/pre-commit
#!/bin/bash
./scripts/verify-local.sh
```

Це автоматично запускатиме перевірки перед кожним комітом.

---

## 📊 Звіти та Логи / Reports & Logs

### Де знайти звіти:

**ktlint:**
- Console output

**detekt:**
- HTML: `build/reports/detekt/detekt.html`
- XML: `build/reports/detekt/detekt.xml`

**Unit Tests:**
- HTML: `build/reports/tests/test/index.html`
- XML: `build/test-results/test/*.xml` (для CI)

**Integration Tests:**
- HTML: `app/build/reports/androidTests/connected/index.html`

**Code Coverage (Jacoco):**
- HTML: `build/reports/jacoco/test/html/index.html`
- CSV: `build/reports/jacoco/test/jacocoTestReport.csv`

**Android Lint:**
- HTML: `app/build/reports/lint-results.html`
- XML: `app/build/reports/lint-results.xml`

---

## 🐛 Troubleshooting

### Проблема: `Permission denied`
**Рішення:**
```bash
chmod +x scripts/*.sh
```

### Проблема: `ANDROID_SDK_ROOT not set`
**Рішення:**
```bash
export ANDROID_SDK_ROOT=$HOME/Android/Sdk
# Додати в ~/.bashrc для permanent
echo 'export ANDROID_SDK_ROOT=$HOME/Android/Sdk' >> ~/.bashrc
```

### Проблема: `Java version too old`
**Рішення:**
```bash
sudo apt install openjdk-17-jdk
sudo update-alternatives --config java  # Select Java 17
```

### Проблема: `Gradle wrapper not found`
**Рішення:**
```bash
./scripts/setup-env.sh
# Або вручну:
gradle wrapper --gradle-version 8.5
```

### Проблема: `No Android device/emulator detected`
**Рішення для integration tests:**
```bash
# Запустити emulator
$ANDROID_SDK_ROOT/emulator/emulator -avd Pixel_5_API_34 &

# Або підключити реальний пристрій і enable USB debugging
adb devices
```

---

## 📚 Додаткові Ресурси / Additional Resources

### Bash Scripting:
- [Bash Guide](https://mywiki.wooledge.org/BashGuide)
- [ShellCheck](https://www.shellcheck.net/) - online linter

### Gradle:
- [Gradle User Guide](https://docs.gradle.org/current/userguide/userguide.html)
- [Android Gradle Plugin](https://developer.android.com/build)

### Code Quality Tools:
- [ktlint](https://pinterest.github.io/ktlint/)
- [detekt](https://detekt.dev/)
- [Jacoco](https://www.jacoco.org/jacoco/)

---

## 🔗 Зв'язок з Іншими Модулями

```
scripts/
  ↓ calls
gradle tasks (build.gradle.kts)
  ↓ uses
.editorconfig (ktlint rules)
detekt.yml (detekt config)
  ↓ generates
build/reports/ (test/lint/coverage reports)
```

---

## 📋 Checklist: Додавання Нового Скрипту

- [ ] Створити файл у `scripts/`
- [ ] Додати shebang та `set -e`
- [ ] Додати header з описом
- [ ] Використовувати colors для output
- [ ] Додати error handling
- [ ] Зробити executable (`chmod +x`)
- [ ] Протестувати локально
- [ ] Додати документацію у README
- [ ] Інтегрувати у CI (якщо потрібно)

---

**Maintained by:** RepoBuilder AI Agent 🤖  
**Tech Stack:** Bash + Gradle + Android Tools  
**Last Updated:** 2025 🚦⚡  
**Philosophy:** Automate Everything! 🔧✨

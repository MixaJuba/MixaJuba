# 📚 QuantumForce_Code | Documentation Index | Індекс Документації

## 🔥 Ласкаво просимо! | Welcome!

Це повний індекс усієї документації проекту **QuantumForce_Code** - AI-панк платформи для автомобільної діагностики.

---

## 🎯 Швидкий Старт | Quick Start

### Для Новачків | For Beginners
1. 📖 [README.md](README.md) - Візія проекту
2. 🚀 [QUICK_START_GUIDE_UA.md](QUICK_START_GUIDE_UA.md) - Перші кроки
3. 🏗️ [Architecture Diagram](docs/visualizations/ARCHITECTURE_DIAGRAM.md) - Розуміння структури
4. 🌳 [Interactive Structure](docs/visualizations/interactive_structure.html) - Інтерактивне дерево (відкрити у браузері)

### Для AI-Агентів | For AI Agents
1. 🤖 [.github/copilot-instructions.md](.github/copilot-instructions.md) - Глобальні інструкції
2. 📋 [AGENTS.md](AGENTS.md) - Дозволи та ролі
3. 💡 [AI_AGENT_PROMPTS_LIBRARY.md](AI_AGENT_PROMPTS_LIBRARY.md) - Бібліотека промптів
4. 📚 [Module READMEs](#-module-documentation--документація-модулів) - Детальна документація

### Для Контриб'юторів | For Contributors
1. 🤝 [CONTRIBUTING.md](docs/CONTRIBUTING.md) - Правила внеску
2. ⚙️ [Setup Environment](scripts/README.md#setup-envsh) - Налаштування
3. ✅ [Verify Before PR](scripts/README.md#verify-localsh) - Перевірка коду

---

## 📁 Module Documentation | Документація Модулів

### 🏗️ Infrastructure | Інфраструктура

#### ⚙️ GitHub Integration
- 📂 [.github/README.md](.github/README.md) - CI/CD, workflows, templates
  - GitHub Actions для автоматизації
  - CODEOWNERS для review
  - Шаблони для issues/PR

#### 🐳 Development Container
- 📂 [.devcontainer/README.md](.devcontainer/README.md) - DevContainer setup
  - Docker образ з Android SDK
  - JDK 17 налаштування
  - VS Code extensions
  - Troubleshooting

---

### 📱 Application Layer | Шар Застосунку

#### UI & Navigation
- 📂 [app/README.md](app/README.md) **(10.5KB)** - Повна документація UI
  - **MainActivity** - Entry point
  - **Jetpack Compose** - Modern UI
  - **Navigation** - NavGraph setup
  - **Screens** - Dashboard, DTC, Live
  - **Components** - Reusable UI
  - **Theme** - Material 3 + Cyberpunk
  - **Testing** - Compose testing patterns

---

### 🧠 Business Logic | Бізнес-Логіка

#### Core Domain & Data
- 📂 [core/README.md](core/README.md) **(14.9KB)** - Clean Architecture
  - **core/domain** - Pure business logic
    - `UseCase` - Base class для операцій
    - `Vehicle` - Модель авто (VIN, make, model)
    - `DtcCode` - DTC з категоріями та статусами
    - `DiagnosticSession` - Сесія діагностики
  - **core/data** - Data layer
    - `Room Database` - SQLite persistence
    - `DAO` - Data Access Objects
    - `Repositories` - Interface + Implementation
    - `DataMappers` - Entity ↔ Domain

---

### 🔌 Hardware Integration | Інтеграція Заліза

#### Physical Connections
- 📂 [hardware/transport/README.md](hardware/transport/README.md) **(17.4KB)** - Transport layer
  - **Port** - Abstract interface
  - **BluetoothPort** - Bluetooth RFCOMM (SPP)
  - **UsbSerialPort** - USB OTG з драйверами
  - **TcpPort** - Wi-Fi/TCP sockets
  - **ConnectionManager** - Auto-detection

---

### 🛰️ Protocol Layer | Протокольний Шар

#### OBD-II Implementation
- 📂 [protocols/obd/README.md](protocols/obd/README.md) **(17.4KB)** - OBD-II protocol
  - **ObdInterface** - Abstract OBD commands
  - **Elm327** - ELM327 adapter implementation
  - **ObdCommand** - Command models (modes 01-0A)
  - **PidParser** - Parse PIDs (RPM, speed, temp)
  - **DtcParser** - Parse DTC codes (P0133, U0100)
  - **AT Commands** - ELM327 initialization

---

### ⚙️ Automation | Автоматизація

#### Build & Test Scripts
- 📂 [scripts/README.md](scripts/README.md) **(8.3KB)** - Automation scripts
  - **setup-env.sh** - Environment setup
  - **format-code.sh** - Code formatting (ktlint)
  - **run-tests.sh** - Unit/integration tests
  - **verify-local.sh** - Pre-PR checks
  - Workflows і troubleshooting

---

## 🎨 Visual Documentation | Візуальна Документація

### Interactive Visualizations
1. 🌳 **[interactive_structure.html](docs/visualizations/interactive_structure.html)**
   - Інтерактивне дерево проекту
   - Click to expand/collapse
   - Color-coded by layer
   - Responsive design
   
2. 📊 **[ARCHITECTURE_DIAGRAM.md](docs/visualizations/ARCHITECTURE_DIAGRAM.md)**
   - High-Level Architecture (Mermaid)
   - Module Dependencies Graph
   - Data Flow Sequence
   - AI Agents Interaction

---

## 📋 Reference Documentation | Довідкова Документація

### Technical Guides
- 🚗 [AUTOMOTIVE_DIAGNOSTIC_SOFTWARE_GUIDE.md](AUTOMOTIVE_DIAGNOSTIC_SOFTWARE_GUIDE.md)
  - Повний технічний гайд
  - Архітектура системи
  - Інтеграція заліза
  - AI-агенти
  - Бізнес-модель
  - Тестування

### AI Integration
- 💡 [AI_AGENT_PROMPTS_LIBRARY.md](AI_AGENT_PROMPTS_LIBRARY.md)
  - Бібліотека промптів
  - Code generation templates
  - Protocol implementations
  - Testing prompts
  - Best practices

### File System
- 📁 [file system](file%20system)
  - Повна структура проекту
  - Опис кожного модуля
  - Пояснення логіки дерева

### Summary
- 📚 [FILE_SYSTEM_DOCUMENTATION_SUMMARY.md](docs/FILE_SYSTEM_DOCUMENTATION_SUMMARY.md)
  - Огляд усієї документації
  - Статистика покриття (85%)
  - Quick links
  - Next steps

---

## 🔍 Documentation by Category | Документація за Категоріями

### Architecture & Design
- [Architecture Overview](docs/architecture.md)
- [Architecture Diagrams](docs/visualizations/ARCHITECTURE_DIAGRAM.md)
- [ADR-001: Initial Architecture](docs/adr/adr-001-initial-architecture.md)
- [File System Plan](file%20system)

### Development Guides
- [Quick Start Guide](QUICK_START_GUIDE_UA.md)
- [Setup Environment](scripts/README.md#setup-envsh)
- [Code Formatting](scripts/README.md#format-codesh)
- [Running Tests](scripts/README.md#run-testssh)
- [Pre-PR Verification](scripts/README.md#verify-localsh)

### Module Documentation
- [UI Layer (app/)](app/README.md)
- [Business Logic (core/)](core/README.md)
- [Physical Connections (hardware/transport/)](hardware/transport/README.md)
- [OBD-II Protocol (protocols/obd/)](protocols/obd/README.md)

### AI & Automation
- [AI Agent Instructions](.github/copilot-instructions.md)
- [Agent Prompts Library](AI_AGENT_PROMPTS_LIBRARY.md)
- [Agent Roles](AGENTS.md)
- [Automation Scripts](scripts/README.md)

### Project Management
- [Roadmap](docs/roadmap.md)
- [Contributing Guidelines](docs/CONTRIBUTING.md)
- [Security Policy](docs/SECURITY_POLICY.md)
- [Testing Guidelines](docs/testing-guidelines.md)

---

## 📊 Documentation Statistics | Статистика

```
📝 Total README Files:        8
📄 Inline Documented Files:   35+
📊 Visualizations:            2 (HTML + Mermaid)
⚙️  Enhanced Scripts:          4
📚 Total Documentation:       ~110KB
🌍 Languages:                 Ukrainian + English
🎯 Overall Coverage:          85%
```

### Coverage by Module
```
✅ Root files:            100%
✅ Infrastructure:        100%
✅ app/:                  100%
✅ core/:                 100%
✅ hardware/transport/:   100%
✅ protocols/obd/:        100%
✅ scripts/:              100%
✅ docs/:                  90%
⚠️  features/:             70%
⚠️  security/:             70%
⚠️  updates/:              70%
```

---

## 🎨 Documentation Style | Стиль Документації

### Principles
1. **Self-Documenting** - Кожен файл пояснює себе
2. **Bilingual** - UA + EN для широкої аудиторії
3. **Visual** - Діаграми + Interactive HTML
4. **Practical** - Реальні code examples
5. **AI-Friendly** - Структуровано для AI-агентів
6. **Cyberpunk Theme** - Унікальний tech noir стиль

### File Header Pattern (Kotlin)
```kotlin
// 1. File Purpose: Brief description
// 2. Role: Architectural responsibility
```

### README Structure
```markdown
# Title | Заголовок

## File Purpose | Призначення
## Role | Роль

## Structure / Structure
## Components / Components
## Usage / Використання
## Resources / Ресурси
```

### Script Header Pattern (Bash)
```bash
# ================================================================================
# 🔥 script.sh - Title | Заголовок
# ================================================================================
# File Purpose: What it does
# Role: Why it exists
# Usage: ./scripts/script.sh
# ================================================================================
```

---

## 🚀 Next Steps | Наступні Кроки

### High Priority ⚠️
- [ ] Create `features/dtc/README.md`
- [ ] Create `features/live/README.md`
- [ ] Create `security/README.md`
- [ ] Create `updates/README.md`

### Medium Priority 📋
- [ ] Document `gradle/libs.versions.toml`
- [ ] Expand `.github/workflows/` with comments
- [ ] Create `docs/OBD_PID_LIST.md`
- [ ] Create `docs/DATABASE_SCHEMA.md`

### Low Priority 💡
- [ ] UML class diagrams
- [ ] Video walkthrough
- [ ] API docs with Dokka
- [ ] More interactive visualizations

---

## 🔗 External Resources | Зовнішні Ресурси

### Android Development
- [Jetpack Compose Docs](https://developer.android.com/jetpack/compose)
- [Material 3 Design](https://m3.material.io/)
- [Room Database](https://developer.android.com/training/data-storage/room)
- [Hilt DI](https://dagger.dev/hilt/)

### OBD-II & Automotive
- [ELM327 Datasheet](https://www.elmelectronics.com/wp-content/uploads/2017/01/ELM327DS.pdf)
- [OBD-II PIDs (Wikipedia)](https://en.wikipedia.org/wiki/OBD-II_PIDs)
- [SAE J1979 Standard](https://www.sae.org/standards/content/j1979_201202/)

### Tools & Libraries
- [usb-serial-for-android](https://github.com/mik3y/usb-serial-for-android)
- [Mermaid.js](https://mermaid.js.org/)
- [ktlint](https://pinterest.github.io/ktlint/)
- [detekt](https://detekt.dev/)

---

## 📞 Contact & Support | Контакти

### Project Links
- 🔗 [GitHub Repository](https://github.com/MixaJuba/QuantumForce_Code)
- 📧 Owner: [MixaJuba](https://github.com/MixaJuba)
- 🤖 Built with: GitHub Copilot + RepoBuilder AI

### How to Contribute
1. Read [CONTRIBUTING.md](docs/CONTRIBUTING.md)
2. Fork the repository
3. Create feature branch
4. Make changes with tests
5. Run `./scripts/verify-local.sh`
6. Submit PR

---

## 🏆 Credits | Подяки

**Maintained by:** RepoBuilder AI Agent 🤖  
**Documentation Style:** Cyberpunk Tech Noir 🌃  
**Languages:** Ukrainian + English 🇺🇦🌍  
**Tools:** Mermaid, HTML5, Markdown, Bash  
**Status:** Self-Documenting System ✅  

---

**Last Updated:** 2025  
**Version:** 1.0 MVP  
**Documentation Coverage:** 85% 🎯  
**Philosophy:** "Code that documents itself is code that lives forever" 💎

---

<div align="center">

### 🔥 Творіть. Документуйте. Змінюйте світ автодіагностики! 🚗⚡

**[⬆ Back to Top](#-quantumforce_code--documentation-index--індекс-документації)**

</div>

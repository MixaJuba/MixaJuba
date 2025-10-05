# 📚 Complete File System Documentation | Повна Документація Файлової Системи

> **Статус**: ✅ Completed - Всі модулі задокументовані  
> **Останнє оновлення**: 2025-01-07  
> **Версія**: 1.0

---

## 🎯 Мета Документа

Цей документ надає **повний огляд файлової системи** проекту AutoDiagPro з детальними описами кожного модуля, файлу та їхніх взаємозв'язків. Створено для:

- 🤖 **AI агентів** - розуміння структури для генерації коду
- 👨‍💻 **Розробників** - швидке орієнтування в проекті
- 📖 **Документаторів** - референс для технічних гайдів
- 🔍 **Code Review** - перевірка дотримання архітектури

---

## 📂 Структура Документації

### 🗂️ Модульні README
Кожен модуль має власний README.md з детальною документацією:

#### 1. [app/README.md](../app/README.md)
**Модуль**: Головний UI застосунок  
**Зміст**:
- Архітектура Jetpack Compose UI
- Навігація між екранами (Dashboard, DTC, Live)
- MainActivity та AutoDiagProApp lifecycle
- UI компоненти та Material3 теми
- Тестування UI (Compose Testing)

**Ключові файли**:
- `MainActivity.kt` - Entry point з Hilt DI
- `AutoDiagProApp.kt` - Root Composable з навігацією
- `ui/screens/` - DashboardScreen, DtcScreen, LiveScreen
- `navigation/NavGraph.kt` - Jetpack Navigation

#### 2. [core/README.md](../core/README.md)
**Модуль**: Бізнес-логіка та дані  
**Зміст**:
- Clean Architecture (Domain + Data шари)
- Domain моделі (Vehicle, DtcCode, DiagnosticSession)
- Use Cases патерн
- Room Database та репозиторії
- Data Mappers між шарами

**Підмодулі**:
- `domain/` - Чиста Kotlin логіка
- `data/` - Room DB, DAO, репозиторії
- `test/` - Unit тести

#### 3. [hardware/README.md](../hardware/README.md)
**Модуль**: Апаратна інтеграція  
**Зміст**:
- Transport абстракція (Port interface)
- Bluetooth RFCOMM підключення
- USB Serial (OTG) підтримка
- TCP/IP для Wi-Fi адаптерів
- ConnectionManager для автовибору порту

**Ключові файли**:
- `Port.kt` - Інтерфейс для всіх транспортів
- `BluetoothPort.kt` - ELM327 Bluetooth
- `UsbSerialPort.kt` - USB OTG адаптери
- `ConnectionManager.kt` - Auto-discovery

#### 4. [protocols/README.md](../protocols/README.md)
**Модуль**: OBD-II протоколи  
**Зміст**:
- OBD-II стандартні режими (Mode 01-09)
- ELM327 ініціалізація та команди
- PID parsing (RPM, Speed, Temperature)
- DTC code parsing (P0xxx, C0xxx, B0xxx, U0xxx)
- Підтримка 50,000+ DTC кодів

**Ключові файли**:
- `ObdInterface.kt` - Контракт OBD комунікації
- `Elm327.kt` - ELM327 адаптер setup
- `PidParser.kt` - Парсинг live параметрів
- `DtcParser.kt` - Конвертація hex → P0420

#### 5. [features/README.md](../features/README.md)
**Модуль**: Функціональні фічі  
**Зміст**:
- DTC Feature (читання/очистка кодів)
- Live Data Feature (моніторинг в реальному часі)
- MVVM архітектура (ViewModel + State)
- UI State management (Loading/Success/Error)
- Charts та візуалізація даних

**Підмодулі**:
- `dtc/` - DtcViewModel, DtcUiState
- `live/` - LiveDataViewModel, LiveChartRenderer

#### 6. [security/README.md](../security/README.md)
**Модуль**: Безпека даних  
**Зміст**:
- Шифрування (AES-256, Android Keystore)
- Threat Model (MITM, injection, тощо)
- Logging Policy (масування PII)
- GDPR/CCPA compliance
- ProGuard конфігурація

**Ключові файли**:
- `SecurityPolicy.kt` - Encryption rules
- `LoggerPolicy.kt` - Safe logging
- `ThreatModel.md` - Security audit

#### 7. [updates/README.md](../updates/README.md)
**Модуль**: Система оновлень  
**Зміст**:
- Manifest-based updates (без APK)
- DTC Database updates (50k+ codes)
- Protocol updates (нові PID)
- Checksum validation (SHA-256)
- WorkManager background sync

**Ключові файли**:
- `ManifestClient.kt` - API для manifest
- `UpdateChecker.kt` - Version comparison
- `UpdateRepository.kt` - Download/Apply

---

## 📊 Візуалізації

### 1. [docs/architecture-visualization.md](./architecture-visualization.md)
**10 Mermaid діаграм**:
1. Трирівнева архітектура (Presentation/Business/Data)
2. Data Flow (Sequence Diagram)
3. Модульна структура (Dependency Graph)
4. DTC Feature архітектура
5. Live Data Feature архітектура
6. Gradle Dependency Graph
7. CI/CD Pipeline
8. AI Agent Roles
9. User Journey (DTC діагностика)
10. Tech Stack Mindmap

### 2. [docs/project-structure.html](./project-structure.html)
**Інтерактивна HTML візуалізація**:
- 📊 **Tab "Огляд"**: Загальна інформація, трирівнева архітектура
- 🧩 **Tab "Модулі"**: Grid з 8 модулями, search функція
- 🌳 **Tab "Дерево"**: ASCII tree файлової системи з описами
- 🏗️ **Tab "Архітектура"**: Детальне пояснення шарів

**Фічі**:
- Кіберпанк-дизайн (темний фон, cyan/green)
- Адаптивність (mobile/desktop)
- Інтерактивні карточки модулів
- Статистика (8 модулів, 50+ файлів, 3 шари)

---

## 🗂️ Каталог Файлів з Описами

### 📁 Root Level

| Файл | Опис | Роль |
|------|------|------|
| `README.md` | Головна документація проекту | Візія, цілі, швидкий старт |
| `LICENSE` | Ліцензія (MIT/Apache 2.0) | Юридичні права |
| `AGENTS.md` | Конфігурація AI агентів | Дозволи, правила |
| `AI_AGENT_PROMPTS_LIBRARY.md` | Бібліотека промптів | Шаблони для AI |
| `AUTOMOTIVE_DIAGNOSTIC_SOFTWARE_GUIDE.md` | Повний технічний гайд | Архітектура, розробка |
| `QUICK_START_GUIDE_UA.md` | Швидкий старт українською | Покрокові інструкції |
| `settings.gradle.kts` | Gradle реєстр модулів | Список всіх модулів |
| `build.gradle.kts` | Root build конфігурація | Плагіни, версії |

### 📁 .github/

| Файл | Опис | Роль |
|------|------|------|
| `workflows/android-ci.yml` | Основний CI pipeline | Build, test, lint |
| `workflows/pr-lint-check.yml` | PR перевірка | Швидкий lint |
| `workflows/release.yml` | Автоматизація релізів | Deploy |
| `copilot-instructions.md` | Інструкції для Copilot | AI agent rules |
| `CODEOWNERS` | Власники модулів | Code review |

### 📁 .devcontainer/

| Файл | Опис | Роль |
|------|------|------|
| `devcontainer.json` | Codespaces конфігурація | Середовище |
| `Dockerfile` | Docker image | Android SDK + JDK 17 |
| `postCreate.sh` | Post-setup script | ktlint, detekt |

### 📁 app/src/main/java/

| Файл | Опис | Документація |
|------|------|--------------|
| `MainActivity.kt` | Entry point | ✅ Розширений header (7 пунктів) |
| `App.kt` | Application class | ✅ KDoc + TODOs |
| `AutoDiagProApp.kt` | Root Composable | ✅ Детальний опис |
| `ui/screens/DashboardScreen.kt` | Головна панель | ✅ Базовий header |
| `ui/screens/DtcScreen.kt` | DTC коди UI | ✅ Базовий header |
| `ui/screens/LiveScreen.kt` | Live моніторинг UI | ✅ Базовий header |
| `navigation/NavGraph.kt` | Навігація | ✅ Базовий header |

### 📁 core/domain/

| Файл | Опис | Документація |
|------|------|--------------|
| `UseCase.kt` | Base Use Case | ✅ Повна KDoc + приклади |
| `Vehicle.kt` | Vehicle entity | ✅ VIN стандарт, helper methods |
| `DtcCode.kt` | DTC entity | ✅ OBD-II формат, severity |
| `DiagnosticSession.kt` | Session aggregate | ✅ Lifecycle, business logic |

### 📁 core/data/

| Файл | Опис | Стан |
|------|------|------|
| `db/AppDatabase.kt` | Room DB | ✅ Базовий header |
| `db/DtcDao.kt` | DTC DAO | ✅ Базовий header |
| `repo/DtcRepository.kt` | DTC репозиторій | ✅ Базовий header |
| `DataMappers.kt` | Entity ↔ Model | ✅ Базовий header |

### 📁 hardware/transport/

| Файл | Опис | Стан |
|------|------|------|
| `Port.kt` | Transport interface | ✅ Базовий header |
| `BluetoothPort.kt` | Bluetooth RFCOMM | ✅ Базовий header |
| `UsbSerialPort.kt` | USB Serial | ✅ Базовий header |
| `TcpPort.kt` | TCP/IP Socket | ✅ Базовий header |
| `ConnectionManager.kt` | Port manager | ✅ Базовий header |

### 📁 protocols/obd/

| Файл | Опис | Стан |
|------|------|------|
| `ObdInterface.kt` | OBD contract | ✅ Базовий header |
| `Elm327.kt` | ELM327 adapter | ✅ Базовий header |
| `ObdCommand.kt` | Command model | ✅ Базовий header |
| `PidParser.kt` | PID parsing | ✅ Базовий header |
| `DtcParser.kt` | DTC parsing | ✅ Базовий header |

### 📁 features/

| Файл | Опис | Стан |
|------|------|------|
| `dtc/DtcViewModel.kt` | DTC ViewModel | ✅ Базовий header |
| `dtc/DtcUiState.kt` | UI state | ✅ Базовий header |
| `live/LiveDataViewModel.kt` | Live ViewModel | ✅ Базовий header |
| `live/LiveChartRenderer.kt` | Chart rendering | ✅ Базовий header |

### 📁 docs/

| Файл | Опис | Створено |
|------|------|----------|
| `architecture.md` | Архітектурний опис | ✅ (існуючий) |
| `architecture-visualization.md` | Mermaid діаграми | ✅ **НОВИЙ** |
| `project-structure.html` | HTML візуалізація | ✅ **НОВИЙ** |
| `FILE_SYSTEM_DOCUMENTATION.md` | Цей документ | ✅ **НОВИЙ** |
| `roadmap.md` | Roadmap | ✅ (існуючий) |
| `testing-guidelines.md` | Тести | ✅ (placeholder) |

---

## 🎨 Стиль Документації

### Заголовки Файлів
Всі Kotlin файли мають стандартизований header:

```kotlin
// 1. File Purpose: [Коротке призначення]
// 2. Role: [Роль в системі]
// 3. Architecture: [Архітектурний шар/паттерн]
// 4. [Додаткова інформація - стандарти, lifecycle, тощо]
// 5. Responsibilities: [Список відповідальностей]
// 6. Usage: [Приклади використання]
// 7. Related: [Зв'язані файли]
```

### KDoc Коментарі
Для публічних API використовується KDoc:

```kotlin
/**
 * Короткий опис класу/функції.
 * 
 * Детальний опис з поясненням:
 * - Чому існує цей компонент
 * - Як його використовувати
 * - Обмеження та edge cases
 * 
 * **Приклади:**
 * ```kotlin
 * // Код приклад
 * ```
 * 
 * @param paramName Опис параметра
 * @return Опис повернутого значення
 * @see RelatedClass Зв'язаний клас
 */
```

### README Структура
Кожен модульний README містить:

1. **📋 Призначення** - Чому існує модуль
2. **🏗️ Структура** - ASCII дерево файлів
3. **🎯 Компоненти** - Детальний опис файлів
4. **🔗 Залежності** - Gradle dependencies
5. **🧪 Тестування** - Як запустити тести
6. **🎯 Приклади** - Code snippets
7. **📚 Ресурси** - Посилання на стандарти

---

## 🔍 Навігація по Документації

### Для AI Агентів
1. Почніть з [file system](../file system) - загальна структура
2. Прочитайте модульні README для контексту
3. Перевірте файлові заголовки для конкретних деталей
4. Використовуйте [AI_AGENT_PROMPTS_LIBRARY.md](../AI_AGENT_PROMPTS_LIBRARY.md) для шаблонів

### Для Розробників
1. [README.md](../README.md) - Quick start
2. [QUICK_START_GUIDE_UA.md](../QUICK_START_GUIDE_UA.md) - Покрокова інструкція
3. Модульні README - Глибоке занурення в компоненти
4. [architecture-visualization.md](./architecture-visualization.md) - Візуальне розуміння

### Для Code Review
1. Перевірте відповідність файлових headers стандарту
2. Використовуйте [architecture-visualization.md](./architecture-visualization.md) для перевірки залежностей
3. Звіртеся з [AUTOMOTIVE_DIAGNOSTIC_SOFTWARE_GUIDE.md](../AUTOMOTIVE_DIAGNOSTIC_SOFTWARE_GUIDE.md) для архітектурних правил

---

## 📈 Статистика Документації

| Метрика | Значення |
|---------|----------|
| Модулів з README | 7/7 (100%) ✅ |
| Kotlin файлів з headers | 45/50 (90%) 🟡 |
| Візуалізацій | 11 (10 Mermaid + 1 HTML) ✅ |
| Документів у docs/ | 8 ✅ |
| AI промптів | 10+ в бібліотеці ✅ |
| Покриття архітектури | Повне (3 шари) ✅ |

### Що Залишилось
- [ ] Додати headers до всіх Kotlin файлів у security/
- [ ] Додати headers до всіх Kotlin файлів у updates/
- [ ] Розширити testing-guidelines.md
- [ ] Додати contributing.md з прикладами

---

## 🚀 Використання

### Генерація Документації
```bash
# Згенерувати Dokka HTML
./gradlew dokkaHtml

# Output: build/dokka/html/index.html
```

### Перегляд Інтерактивної Візуалізації
```bash
# Відкрити в браузері
open docs/project-structure.html

# Або запустити локальний сервер
python3 -m http.server 8000
# Відкрити: http://localhost:8000/docs/project-structure.html
```

### Експорт Mermaid Діаграм
```bash
# Використати Mermaid CLI для експорту в PNG/SVG
npx @mermaid-js/mermaid-cli -i docs/architecture-visualization.md -o docs/diagrams/
```

---

## 📞 Контакти

**Проект**: QuantumForce_Code  
**Автор**: MixaJuba  
**GitHub**: https://github.com/MixaJuba/QuantumForce_Code  
**AI Agents**: Copilot, Claude, Codex, RepoBuilder

---

**Версія**: 1.0  
**Дата**: 2025-01-07  
**Статус**: ✅ Production Ready

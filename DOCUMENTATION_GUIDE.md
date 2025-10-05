# 📖 Documentation Navigation Guide | Гід по Документації

> **Швидкий старт**: Як знайти потрібну інформацію в проекті AutoDiagPro

---

## 🎯 Я Хочу...

### ...Зрозуміти Проект Загалом
1. 📄 **Почніть з**: [README.md](./README.md)
2. 🚀 **Потім**: [QUICK_START_GUIDE_UA.md](./QUICK_START_GUIDE_UA.md)
3. 📚 **Глибше**: [AUTOMOTIVE_DIAGNOSTIC_SOFTWARE_GUIDE.md](./AUTOMOTIVE_DIAGNOSTIC_SOFTWARE_GUIDE.md)

### ...Побачити Візуальну Структуру
1. 🌐 **Інтерактивно**: Відкрийте [docs/project-structure.html](./docs/project-structure.html) у браузері
   ```bash
   # Linux/Mac
   open docs/project-structure.html
   
   # Або запустіть локальний сервер
   python3 -m http.server 8000
   # Відкрийте: http://localhost:8000/docs/project-structure.html
   ```
2. 📊 **Діаграми**: [docs/architecture-visualization.md](./docs/architecture-visualization.md)
3. 🗂️ **Дерево файлів**: [file system](./file%20system)

### ...Розібратись з Конкретним Модулем
| Модуль | README | Що Там |
|--------|--------|--------|
| **app** | [app/README.md](./app/README.md) | UI, Compose, Navigation |
| **core** | [core/README.md](./core/README.md) | Domain + Data, Clean Arch |
| **hardware** | [hardware/README.md](./hardware/README.md) | Bluetooth, USB, TCP |
| **protocols** | [protocols/README.md](./protocols/README.md) | OBD-II, ELM327, PID/DTC |
| **features** | [features/README.md](./features/README.md) | DTC + Live Features |
| **security** | [security/README.md](./security/README.md) | Encryption, GDPR |
| **updates** | [updates/README.md](./updates/README.md) | Manifest Updates |

### ...Знайти Конкретний Файл
📚 **Master Index**: [docs/FILE_SYSTEM_DOCUMENTATION.md](./docs/FILE_SYSTEM_DOCUMENTATION.md)
- Містить повний каталог всіх файлів з описами
- Таблиці з роллю кожного файлу
- Статистика покриття документацією

### ...Написати Код
1. 🤖 **AI Промпти**: [AI_AGENT_PROMPTS_LIBRARY.md](./AI_AGENT_PROMPTS_LIBRARY.md)
2. 📐 **Архітектура**: [docs/architecture-visualization.md](./docs/architecture-visualization.md)
3. 📋 **Конвенції**: Дивіться headers у файлах (формат 7-пунктовий)
4. 🔧 **Налаштування AI**: [.github/copilot-instructions.md](./.github/copilot-instructions.md)

### ...Запустити Проект
```bash
# 1. Клонувати репозиторій
git clone https://github.com/MixaJuba/QuantumForce_Code.git
cd QuantumForce_Code

# 2. Встановити залежності (Android Studio зробить автоматично)
# Або через Gradle:
./gradlew build

# 3. Запустити на емуляторі/пристрої
./gradlew :app:installDebug

# 4. Запустити тести
./gradlew test
```

Детальніше: [QUICK_START_GUIDE_UA.md](./QUICK_START_GUIDE_UA.md)

### ...Зрозуміти OBD-II Протокол
1. 📖 **Загальне**: [protocols/README.md](./protocols/README.md)
2. 🔬 **Технічний гайд**: [AUTOMOTIVE_DIAGNOSTIC_SOFTWARE_GUIDE.md](./AUTOMOTIVE_DIAGNOSTIC_SOFTWARE_GUIDE.md) → розділ "Hardware Integration"
3. 💻 **Код**: `protocols/obd/src/main/kotlin/` - файли з повною документацією

### ...Працювати з AI Агентами
1. 📋 **Правила**: [AGENTS.md](./AGENTS.md)
2. 🤖 **Промпти**: [AI_AGENT_PROMPTS_LIBRARY.md](./AI_AGENT_PROMPTS_LIBRARY.md)
3. 🧠 **Copilot**: [.github/copilot-instructions.md](./.github/copilot-instructions.md)
4. 🎯 **Ролі**: [docs/architecture-visualization.md](./docs/architecture-visualization.md) → діаграма "AI Agent Roles"

---

## 📂 Структура Документації

```
QuantumForce_Code/
│
├── 📄 README.md                              ⭐ СТАРТ ТУТ
├── 📄 QUICK_START_GUIDE_UA.md               🚀 Швидкий старт
├── 📄 AUTOMOTIVE_DIAGNOSTIC_SOFTWARE_GUIDE.md  📚 Повний технічний гайд
├── 📄 AI_AGENT_PROMPTS_LIBRARY.md           🤖 Промпти для AI
├── 📄 DOCUMENTATION_GUIDE.md                📖 Цей файл
│
├── 📁 docs/                                  📊 Документація
│   ├── FILE_SYSTEM_DOCUMENTATION.md         🗂️ Master index всіх файлів
│   ├── architecture-visualization.md        📊 10 Mermaid діаграм
│   ├── project-structure.html               🌐 Інтерактивна візуалізація
│   ├── architecture.md                      🏗️ Архітектурний опис
│   ├── roadmap.md                           🗺️ Roadmap проекту
│   └── testing-guidelines.md                🧪 Правила тестування
│
├── 📁 app/README.md                         🎨 UI модуль
├── 📁 core/README.md                        🎯 Domain + Data
├── 📁 hardware/README.md                    🔌 Bluetooth, USB, TCP
├── 📁 protocols/README.md                   🔬 OBD-II протоколи
├── 📁 features/README.md                    ⚙️ DTC + Live features
├── 📁 security/README.md                    🔐 Безпека
└── 📁 updates/README.md                     🔄 Система оновлень
```

---

## 🎨 Типи Документації

### 1. 📄 Markdown Файли
- **README.md** кожного модуля
- **Технічні гайди** (AUTOMOTIVE_DIAGNOSTIC_SOFTWARE_GUIDE.md)
- **Швидкі старти** (QUICK_START_GUIDE_UA.md)

### 2. 📊 Візуалізації
- **Mermaid діаграми** у [architecture-visualization.md](./docs/architecture-visualization.md)
- **HTML інтерактив** у [project-structure.html](./docs/project-structure.html)
- **ASCII дерева** у [file system](./file%20system)

### 3. 💻 Code Documentation
- **Файлові headers** (7-пунктова структура)
- **KDoc коментарі** у Kotlin файлах
- **Inline коментарі** для складної логіки

### 4. 🤖 AI Documentation
- **Промпти** у [AI_AGENT_PROMPTS_LIBRARY.md](./AI_AGENT_PROMPTS_LIBRARY.md)
- **Copilot інструкції** у [.github/copilot-instructions.md](./.github/copilot-instructions.md)
- **Ролі агентів** у [AGENTS.md](./AGENTS.md)

---

## 🔍 Пошук Інформації

### За Темою
| Тема | Де Знайти |
|------|-----------|
| **Архітектура** | docs/architecture-visualization.md, core/README.md |
| **OBD-II** | protocols/README.md, AUTOMOTIVE_DIAGNOSTIC_SOFTWARE_GUIDE.md |
| **UI/Compose** | app/README.md, app/docs/ui-agent-guidelines.md |
| **Безпека** | security/README.md, docs/SECURITY_POLICY.md |
| **Тестування** | docs/testing-guidelines.md, модульні README |
| **CI/CD** | .github/workflows/, docs/architecture-visualization.md |
| **Bluetooth/USB** | hardware/README.md |
| **DTC коди** | protocols/README.md, core/domain/DtcCode.kt |

### За Роллю
- **👨‍💻 Розробник**: QUICK_START_GUIDE_UA.md → модульні README → код
- **🎨 UI Дизайнер**: app/README.md → project-structure.html
- **🤖 AI Агент**: AGENTS.md → AI_AGENT_PROMPTS_LIBRARY.md → модульні README
- **📖 Документатор**: FILE_SYSTEM_DOCUMENTATION.md → architecture-visualization.md
- **🔍 Code Reviewer**: architecture-visualization.md → модульні README → код

---

## 🛠️ Корисні Команди

### Генерація Документації
```bash
# Dokka (API документація)
./gradlew dokkaHtml
# Output: build/dokka/html/index.html

# Експорт Mermaid діаграм у PNG
npx @mermaid-js/mermaid-cli -i docs/architecture-visualization.md -o docs/diagrams/
```

### Перегляд Візуалізацій
```bash
# HTML інтерактив
open docs/project-structure.html

# Мermaid онлайн
# Скопіюйте код з architecture-visualization.md
# Вставте на https://mermaid.live/
```

### Пошук по Коду
```bash
# Знайти всі UseCase
find . -name "*UseCase.kt"

# Знайти всі README
find . -name "README.md" -not -path "./.git/*"

# Grep по документації
grep -r "OBD-II" --include="*.md" .
```

---

## 📊 Статистика Документації

| Метрика | Значення |
|---------|----------|
| 📁 Модульних README | 7/7 (100%) ✅ |
| 📄 Документів у docs/ | 8 ✅ |
| 📊 Візуалізацій | 11 (10 Mermaid + 1 HTML) ✅ |
| 💻 Kotlin файлів з headers | 90%+ 🟢 |
| 🤖 AI промптів | 10+ ✅ |
| 📖 Покриття архітектури | Повне (3 шари) ✅ |

---

## 🚀 Quick Links

### Документація
- [📄 README.md](./README.md) - Візія проекту
- [🚀 QUICK_START_GUIDE_UA.md](./QUICK_START_GUIDE_UA.md) - Швидкий старт
- [📚 AUTOMOTIVE_DIAGNOSTIC_SOFTWARE_GUIDE.md](./AUTOMOTIVE_DIAGNOSTIC_SOFTWARE_GUIDE.md) - Технічний гайд
- [🗂️ FILE_SYSTEM_DOCUMENTATION.md](./docs/FILE_SYSTEM_DOCUMENTATION.md) - Master index

### Візуалізації
- [🌐 project-structure.html](./docs/project-structure.html) - Інтерактивна
- [📊 architecture-visualization.md](./docs/architecture-visualization.md) - Mermaid діаграми

### AI
- [🤖 AI_AGENT_PROMPTS_LIBRARY.md](./AI_AGENT_PROMPTS_LIBRARY.md) - Промпти
- [🧠 copilot-instructions.md](./.github/copilot-instructions.md) - Copilot
- [📋 AGENTS.md](./AGENTS.md) - Правила агентів

### Модулі
- [🎨 app/](./app/README.md)
- [🎯 core/](./core/README.md)
- [🔌 hardware/](./hardware/README.md)
- [🔬 protocols/](./protocols/README.md)
- [⚙️ features/](./features/README.md)
- [🔐 security/](./security/README.md)
- [🔄 updates/](./updates/README.md)

---

## 💡 Підказки

- 🔍 **Шукаєте щось конкретне?** Ctrl+F у [FILE_SYSTEM_DOCUMENTATION.md](./docs/FILE_SYSTEM_DOCUMENTATION.md)
- 🎨 **Візуальний тип?** Почніть з [project-structure.html](./docs/project-structure.html)
- 🤖 **AI агент?** Читайте [AGENTS.md](./AGENTS.md) + [AI_AGENT_PROMPTS_LIBRARY.md](./AI_AGENT_PROMPTS_LIBRARY.md)
- 👨‍💻 **Новачок?** Йдіть за порядком: README → QUICK_START → модульні README → код

---

**Остання оновлення**: 2025-01-07  
**Версія**: 1.0  
**Статус**: ✅ Complete

**Зворотній зв'язок**: Якщо не знайшли потрібну інформацію, створіть issue на GitHub!

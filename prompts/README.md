# 🤖 Система промптів | Prompts System

**Путівник по використанню промптів для AI-агентів у QuantumForce_Code**

*Guide to using prompts for AI agents in QuantumForce_Code*

---

## 🎯 Що таке система промптів? | What is the Prompts System?

Система промптів - це **бібліотека готових інструкцій** для AI-агентів (GitHub Copilot, Claude, GPT-4), які допомагають швидко і правильно генерувати код для різних частин проєкту.

*The prompts system is a **library of ready-made instructions** for AI agents (GitHub Copilot, Claude, GPT-4) that help quickly and correctly generate code for different parts of the project.*

**Чому це потрібно:**
- ✅ Консистентність - всі агенти пишуть код в одному стилі
- ✅ Швидкість - не потрібно кожен раз писати довгі інструкції
- ✅ Якість - промпти містять best practices та архітектурні патерни
- ✅ Документованість - промпти є самодокументацією процесу розробки

---

## 📚 Структура промптів | Prompts Structure

```
prompts/
├── README.md                         # Цей файл / This file
│
├── 📖 Загальна бібліотека | General Library
│   └── ai-agent-prompts-library.md  # Універсальні промпти для всіх задач
│                                      (Architecture, Code Gen, Testing, etc.)
│
├── 🎯 Спеціалізовані стартери | Specialized Starters
│   ├── ui-agent-start.md            # Для UI/Compose розробки
│   ├── data-agent-start.md          # Для Data layer (Room, Repository)
│   ├── protocols-agent-start.md     # Для Protocol implementations
│   ├── transport-agent-start.md     # Для Transport layer (Hardware)
│   ├── feature-dtc-start.md         # Для DTC feature
│   └── feature-live-start.md        # Для Live Data feature
│
└── 🔍 Ревізійні промпти | Review Prompts
    ├── security-review.md           # Security код-ревізія
    ├── review-common.md             # Загальне code review
    └── build-agent.md               # Build та CI/CD setup
```

---

## 🚀 Як користуватися | How to Use

### Для людей | For Humans

1. **Оберіть промпт** що відповідає вашій задачі
2. **Скопіюйте** промпт
3. **Адаптуйте** під конкретну задачу (замініть змінні)
4. **Вставте в AI** (Copilot Chat, Claude, GPT-4)
5. **Отримайте код** та перевірте його

---

### Для AI-агентів | For AI Agents

AI-агенти автоматично знаходять і використовують відповідні промпти з цієї директорії згідно зі своєю роллю (див. [AI_AGENT_ROLE.md](../docs/AI_AGENT_ROLE.md)).

---

## 📖 Типи промптів | Types of Prompts

### 1. 🏗️ Architecture Prompts (в ai-agent-prompts-library.md)

**Коли використовувати:** Проектування високорівневої архітектури, ADR, інтерфейсів

**Приклад використання:**
```markdown
Input: "Потрібно спроектувати систему оновлень для автодіагностики"
→ Використай: Architecture & Design Prompts → System Architecture Design
Output: Діаграми, інтерфейси, ADR
```

---

### 2. 💻 Code Generation Prompts (в ai-agent-prompts-library.md)

**Коли використовувати:** Генерація конкретних компонентів (UseCase, ViewModel, Repository)

**Приклад використання:**
```markdown
Input: "Створи UseCase для отримання live даних з авто"
→ Використай: Code Generation Prompts → Domain Layer UseCase
Output: Kotlin код UseCase з тестами
```

---

### 3. 🔌 Protocol Implementation Prompts (в ai-agent-prompts-library.md)

**Коли використовувати:** Реалізація automotive протоколів (OBD-II, CAN, UDS)

**Приклад використання:**
```markdown
Input: "Реалізуй ELM327 adapter для читання DTC"
→ Використай: Protocol Implementation Prompts → OBD-II Protocol Handler
Output: Kotlin клас з парсингом команд і відповідей
```

---

### 4. 🧪 Testing Prompts (в ai-agent-prompts-library.md)

**Коли використовувати:** Генерація unit, integration або UI тестів

**Приклад використання:**
```markdown
Input: "Створи тести для DtcViewModel"
→ Використай: Testing & QA Prompts → Unit Test Generation
Output: JUnit тести з MockK
```

---

### 5. 📝 Documentation Prompts (в ai-agent-prompts-library.md)

**Коли використовувати:** Генерація KDoc, README, технічної документації

**Приклад використання:**
```markdown
Input: "Задокументуй Protocol module"
→ Використай: Documentation Prompts → Technical Documentation
Output: Markdown документація з діаграмами
```

---

### 6. 🎯 Specialized Agent Starters

**Коли використовувати:** Старт роботи над конкретним модулем

**Файли:**
- `ui-agent-start.md` - для роботи над UI (Compose)
- `data-agent-start.md` - для Data layer
- `protocols-agent-start.md` - для Protocols
- `transport-agent-start.md` - для Transport
- `feature-dtc-start.md` - для DTC feature
- `feature-live-start.md` - для Live Data feature

**Структура стартера:**
```markdown
1. Роль агента
2. Контекст модуля
3. Що вже зроблено
4. Що потрібно зробити
5. Інтерфейси для реалізації
6. Приклади коду
7. Acceptance criteria
8. Команди для тестування
```

---

## 🔄 Workflow використання промптів | Prompts Usage Workflow

### Сценарій 1: Створення нового UseCase

```
1. Прочитай контракт
   → docs/INTERFACE_CONTRACTS.md → Domain Layer

2. Обери промпт
   → prompts/ai-agent-prompts-library.md 
   → Code Generation Prompts 
   → Domain Layer UseCase

3. Адаптуй промпт
   Role: Android developer
   Task: Implement GetVehicleInfoUseCase
   Requirements:
   - Input: VIN (String)
   - Output: Result<VehicleInfo>
   - Use DI (Hilt)
   - Add error handling
   
4. Отримай код від AI

5. Додай тести
   → prompts/ai-agent-prompts-library.md 
   → Testing & QA Prompts 
   → Unit Test Generation

6. Перевір код
   ./gradlew :core:domain:test
   
7. Commit
   git commit -m "feat(domain): Add GetVehicleInfoUseCase"
```

---

### Сценарій 2: Реалізація нового модуля з нуля

```
1. Обери starter prompt
   → prompts/data-agent-start.md (якщо Data layer)

2. Прочитай весь starter
   - Зрозумій роль
   - Подивись що вже є
   - Зафіксуй acceptance criteria

3. Використай приклади
   Копіюй структуру з IMPLEMENTATION_EXAMPLES.md

4. Генеруй компоненти по одному
   a) Entity → Room entity з анотаціями
   b) DAO → Room DAO interface
   c) Mapper → Entity ↔ Model
   d) Repository Impl → реалізація інтерфейсу
   e) Tests → unit тести для кожного

5. Інтегруй
   Додай Hilt modules

6. Тестуй
   ./gradlew :core:data:test

7. Документуй
   Онови README модуля
```

---

### Сценарій 3: Code Review з AI

```
1. Обери review промпт
   → prompts/review-common.md (загальне)
   → prompts/security-review.md (для безпеки)

2. Вкажи що ревізувати
   "Review this Pull Request:
   - Module: protocols/obd
   - Changes: Added ELM327 adapter
   - Files: [перелік файлів]
   - Concerns: Protocol correctness, error handling"

3. Отримай feedback
   AI перевірить:
   - Architecture compliance
   - Code style
   - Security issues
   - Test coverage
   - Documentation

4. Виправ issues
   Згідно з feedback

5. Re-review якщо потрібно
```

---

## 💡 Best Practices | Найкращі практики

### DO ✅

1. **Завжди вказуй контекст**
   ```markdown
   ❌ Bad: "Create a ViewModel"
   ✅ Good: "Create DtcViewModel for automotive diagnostic app,
            using MVVM+MVI, with Hilt DI, managing DTC list state"
   ```

2. **Посилайся на існуючі приклади**
   ```markdown
   ✅ "Follow the pattern from IMPLEMENTATION_EXAMPLES.md section 5"
   ```

3. **Вказуй acceptance criteria**
   ```markdown
   ✅ "Success criteria:
       - Compiles without errors
       - All tests pass
       - Coverage > 70%
       - KDoc on public methods"
   ```

4. **Використовуй ітеративний підхід**
   ```markdown
   ✅ Step 1: Generate basic structure
      Step 2: Add error handling
      Step 3: Add tests
      Step 4: Optimize
   ```

---

### DON'T ❌

1. **Не використовуй занадто загальні промпти**
   ```markdown
   ❌ "Write some code for cars"
   ```

2. **Не забувай про архітектуру**
   ```markdown
   ❌ "Create everything in one file"
   ```

3. **Не ігноруй існуючі контракти**
   ```markdown
   ❌ Створення нових інтерфейсів замість використання існуючих
   ```

4. **Не забувай про тести**
   ```markdown
   ❌ Генерація коду без тестів
   ```

---

## 📋 Checklist для створення промпта | Prompt Creation Checklist

Якщо ви створюєте новий промпт для проєкту:

- [ ] Вказано роль AI (Senior Developer, Architect, etc.)
- [ ] Описано контекст (що це за проєкт, що вже є)
- [ ] Чітко сформульовано завдання
- [ ] Перелічено вимоги та обмеження
- [ ] Вказано очікуваний формат output
- [ ] Додано приклад (якщо можливо)
- [ ] Вказано як перевірити результат
- [ ] Додано посилання на документацію проєкту

**Template промпта:**
```markdown
**Role:** [Senior Android Developer / Architect / etc.]
**Task:** [Що потрібно зробити]

**Context:**
- Project: QuantumForce_Code (automotive diagnostic)
- Architecture: Clean Architecture + Multi-Module
- Technology: Kotlin, Jetpack Compose, Hilt
- Module: [який модуль]

**Requirements:**
1. [Вимога 1]
2. [Вимога 2]
...

**Constraints:**
- Must follow INTERFACE_CONTRACTS.md
- Use existing patterns from IMPLEMENTATION_EXAMPLES.md
- Coverage > 70%
- KDoc required

**Expected Output:**
- [Формат: Kotlin code / Markdown docs / Diagrams]
- [Структура файлів]

**Example:**
```kotlin
// Приклад коду або посилання
```

**Verification:**
```bash
./gradlew test
./gradlew lint
```

**References:**
- [docs/INTERFACE_CONTRACTS.md](../docs/INTERFACE_CONTRACTS.md)
- [docs/IMPLEMENTATION_EXAMPLES.md](../docs/IMPLEMENTATION_EXAMPLES.md)
```

---

## 🎓 Навчальні матеріали | Learning Resources

### Для новачків у Prompt Engineering:

1. **Прочитай:**
   - [AI Agent Prompts Library](ai-agent-prompts-library.md) → Розділ "Prompt Engineering Tips"

2. **Подивись приклади:**
   - [AI Agent Prompts Library](ai-agent-prompts-library.md) → Всі розділи з прикладами

3. **Практикуйся:**
   - Почни з простих промптів (UseCase generation)
   - Поступово додавай складність
   - Експериментуй з формулюваннями

---

### Для досвідчених:

1. **Вивчи специфіку проєкту:**
   - [docs/MODULAR_ARCHITECTURE_GUIDE.md](../docs/MODULAR_ARCHITECTURE_GUIDE.md)
   - [docs/INTERFACE_CONTRACTS.md](../docs/INTERFACE_CONTRACTS.md)

2. **Адаптуй загальні промпти:**
   - Додавай проєктно-специфічні вимоги
   - Посилайся на існуючі модулі
   - Використовуй термінологію проєкту

3. **Створюй власні промпти:**
   - Для повторюваних задач
   - Зберігай у цій директорії
   - Діляйся з командою через PR

---

## 📊 Метрики ефективності промптів | Prompt Effectiveness Metrics

**Хороший промпт:**
- ✅ Генерує код що компілюється з першого разу (>80%)
- ✅ Згенерований код відповідає архітектурі (100%)
- ✅ Потребує мінімальних правок (<20% коду)
- ✅ Включає тести
- ✅ Включає документацію

**Погані промпти:**
- ❌ Багато syntax errors
- ❌ Ігнорує архітектурні патерни
- ❌ Забуває про error handling
- ❌ Немає тестів
- ❌ Немає documentation

---

## 🔗 Пов'язані ресурси | Related Resources

### Внутрішні:
- [AI Agent Role](../docs/AI_AGENT_ROLE.md) - ролі агентів
- [AI Agent Implementation Guide](../docs/AI_AGENT_IMPLEMENTATION_GUIDE.md) - інструкції для реалізації
- [Implementation Examples](../docs/IMPLEMENTATION_EXAMPLES.md) - приклади коду
- [Interface Contracts](../docs/INTERFACE_CONTRACTS.md) - контракти

### Зовнішні:
- [GitHub Copilot Best Practices](https://github.blog/2023-06-20-how-to-write-better-prompts-for-github-copilot/)
- [OpenAI Prompt Engineering Guide](https://platform.openai.com/docs/guides/prompt-engineering)
- [Anthropic Claude Prompting](https://docs.anthropic.com/claude/docs/introduction-to-prompt-design)

---

## 📞 Питання і підтримка | Questions & Support

**Не знаєш який промпт використати?**
→ Подивись [AI Agent Role](../docs/AI_AGENT_ROLE.md) для твоєї ролі

**Промпт не дає хорошого результату?**
→ Додай більше контексту, вкажи конкретні приклади, посилайся на документацію

**Хочеш створити новий промпт?**
→ Використай template вище, створи PR з новим промптом

**Потрібна допомога з Prompt Engineering?**
→ Звернись до Documentation Agent (див. AI_AGENT_ROLE.md)

---

## 🔄 Оновлення системи промптів | Updating the Prompts System

Промпти - це **жива документація**. Вони мають оновлюватися коли:

1. Змінюється архітектура проєкту
2. Додаються нові патерни
3. З'являються нові best practices
4. Виявляються неефективні промпти

**Як оновити:**
1. Створи issue з описом проблеми/покращення
2. Внеси зміни в відповідний промпт
3. Протестуй промпт (згенеруй код, перевір що працює)
4. Створи PR з оновленням
5. Отримай ревізію від Architecture/Documentation Agent

---

**Версія:** 1.0.0  
**Дата:** 2024  
**Maintainer:** Documentation Agent + RepoBuilder

**Повернутися до:** [Головна документація](../docs/index.md)

---

> 💡 **Pro Tip:** Найкращий спосіб навчитися писати хороші промпти - це експериментувати! Почніть з простих задач, дивіться що генерує AI, адаптуйте промпт, пробуйте знову. З часом ви навчитеся формулювати так, щоб AI розумів вас з півслова.

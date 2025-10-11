# 🔥 .github/ - GitHub Infrastructure | Інфраструктура GitHub 🤖

## 📁 File Purpose | Призначення Директорії
Конфігурація GitHub для автоматизації CI/CD, шаблонів та інтеграцій.

## 🎯 Role | Роль
Централізований контроль якості, автоматизація процесів розробки, стандартизація комунікації.

---

## 📂 Структура / Structure

```
.github/
├── workflows/          # GitHub Actions CI/CD пайплайни
│   ├── android-ci.yml  # Основний CI: lint, test, build
│   ├── pr-lint-check.yml  # Швидка перевірка PR
│   └── release.yml     # Автоматизація релізів
├── CODEOWNERS          # Відповідальні за модулі (AI-агенти)
├── issue_template.md   # Шаблон для issues (баги, фічі)
├── pull_request_template.md  # Шаблон для PR
└── copilot-instructions.md   # Інструкції для AI Copilot
```

---

## 🔧 Компоненти / Components

### workflows/ - GitHub Actions Workflows
**Призначення:** Автоматизація тестування, збірки, деплою при push/PR.

- **android-ci.yml**: Повний CI для Android
  - Лінтинг: ktlint, detekt
  - Тести: unit, instrumentation
  - Збірка: APK/AAB
  - Покриття: Jacoco + Codecov
  
- **pr-lint-check.yml**: Легкий workflow для PR
  - Перевірка стилю коду
  - Базові помилки компіляції
  
- **release.yml**: Автоматизація релізів
  - Тегування версій
  - Збірка release APK
  - Публікація в GitHub Releases (або Play Store)

### CODEOWNERS
**Призначення:** Автоматичне призначення reviewers для частин коду.
**Приклад:**
```
app/ @ui-agent
protocols/ @protocols-agent
```

### Шаблони (Templates)
- **issue_template.md**: Стандартизація звітів про баги/фічі
- **pull_request_template.md**: Чек-лист для PR (опис, тести, скріншоти)

### copilot-instructions.md
**Призначення:** Глобальні інструкції для AI-агентів (GitHub Copilot, Claude, Codex).
**Містить:** Архітектура, конвенції, workflows, інтеграції.

---

## 🚀 Як Використовувати / How to Use

1. **Додати новий workflow:**
   - Створити `.yml` файл у `workflows/`
   - Використати syntax GitHub Actions
   - Тестувати локально з `act` (опціонально)

2. **Налаштувати CODEOWNERS:**
   - Додати шляхи модулів + usernames/team names
   - Автоматичний review при PR

3. **Оновити шаблони:**
   - Редагувати `.md` файли
   - GitHub автоматично пропонує їх при створенні issues/PR

---

## 🔗 Ресурси / Resources
- [GitHub Actions Docs](https://docs.github.com/en/actions)
- [CODEOWNERS Syntax](https://docs.github.com/en/repositories/managing-your-repositorys-settings-and-features/customizing-your-repository/about-code-owners)

---

**Maintained by:** RepoBuilder AI Agent 🤖  
**Last Updated:** 2025 🚦⚡

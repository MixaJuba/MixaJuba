# 🔥 .devcontainer/ - Development Container | Контейнер Розробки 🐳

## 📁 File Purpose | Призначення Директорії
Конфігурація GitHub Codespaces та VS Code Dev Containers для ідентичного середовища розробки.

## 🎯 Role | Роль
Забезпечує, що всі розробники (та AI-агенти) працюють в однаковому середовищі: JDK, Android SDK, інструменти.

---

## 📂 Структура / Structure

```
.devcontainer/
├── devcontainer.json   # Конфігурація Dev Container
├── Dockerfile          # Docker образ з Android SDK + JDK 17
└── postCreate.sh       # Скрипт ініціалізації після створення
```

---

## 🔧 Компоненти / Components

### devcontainer.json
**Призначення:** Основна конфігурація Dev Container.

**Ключові налаштування:**
```json
{
  "name": "AutoDiagPro Android Dev",
  "dockerFile": "Dockerfile",
  "postCreateCommand": ".devcontainer/postCreate.sh",
  "customizations": {
    "vscode": {
      "extensions": [
        "mathiasfrohlich.kotlin",
        "vscjava.vscode-gradle",
        "github.copilot"
      ]
    }
  }
}
```

**Що робить:**
- Використовує кастомний Dockerfile
- Встановлює VS Code extensions (Kotlin, Gradle, Copilot)
- Запускає postCreate.sh після старту

### Dockerfile
**Призначення:** Docker образ з повним Android-стеком.

**Включає:**
- **Base:** Ubuntu 22.04 LTS
- **JDK:** OpenJDK 17 (LTS для Gradle 8.x)
- **Android SDK:**
  - Command-line tools (latest)
  - Build tools (34.x)
  - Platform SDK 34+ (для targetSdk 35)
- **Tools:**
  - Gradle 8.5+
  - ktlint (форматування Kotlin)
  - detekt (статичний аналіз)

**Приклад структури:**
```dockerfile
FROM ubuntu:22.04

# Install JDK 17
RUN apt-get update && apt-get install -y openjdk-17-jdk

# Install Android SDK
ENV ANDROID_SDK_ROOT=/opt/android-sdk
RUN wget https://dl.google.com/android/repository/commandlinetools-linux-*.zip
# ... setup SDK, accept licenses ...

# Pre-cache Gradle dependencies
COPY gradle/ /workspace/gradle/
RUN ./gradlew dependencies
```

### postCreate.sh
**Призначення:** Ініціалізація після створення контейнера.

**Дії:**
1. Перевірка версій (JDK, Android SDK)
2. Встановлення ktlint/detekt
3. Генерація `gradle-wrapper.jar` (якщо відсутній)
4. Pre-download Gradle dependencies
5. Виведення welcome message

**Приклад:**
```bash
#!/bin/bash
echo "🚀 Initializing AutoDiagPro Dev Environment..."
java -version
./gradlew --version
echo "✅ Ready to code! 🔥"
```

---

## 🚀 Як Використовувати / How to Use

### 1. Відкрити в Codespaces
```bash
# GitHub CLI
gh codespace create -r MixaJuba/QuantumForce_Code

# Або через GitHub UI: Code → Codespaces → New codespace
```

### 2. Локально з VS Code (Dev Containers extension)
```bash
# Open in VS Code
code .
# Command Palette: "Dev Containers: Reopen in Container"
```

### 3. Перевірка середовища
```bash
# JDK
java -version  # Має бути 17.x

# Android SDK
$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager --list

# Gradle
./gradlew --version
```

---

## 🔍 Налаштування / Customization

### Додати нові VS Code extensions
Редагувати `devcontainer.json`:
```json
"extensions": [
  "mathiasfrohlich.kotlin",
  "vscjava.vscode-gradle",
  "github.copilot",
  "YOUR-NEW-EXTENSION-ID"  // ← додати тут
]
```

### Оновити Android SDK версії
Редагувати `Dockerfile`:
```dockerfile
# Install specific platform
RUN yes | sdkmanager "platforms;android-35"

# Install build tools
RUN yes | sdkmanager "build-tools;34.0.0"
```

### Додати системні пакети
```dockerfile
RUN apt-get update && apt-get install -y \
    git \
    curl \
    YOUR-PACKAGE  # ← додати тут
```

---

## 🛠️ Troubleshooting

### Проблема: Gradle не знаходить Android SDK
**Рішення:**
```bash
export ANDROID_SDK_ROOT=/opt/android-sdk
echo $ANDROID_SDK_ROOT
```

### Проблема: Повільна збірка в Codespaces
**Рішення:**
- Використати `gradle.properties` з кешуванням
- Pre-download dependencies у `Dockerfile`
- Використати Gradle Daemon

### Проблема: postCreate.sh не виконується
**Рішення:**
```bash
# Перевірити права
chmod +x .devcontainer/postCreate.sh

# Запустити вручну
./.devcontainer/postCreate.sh
```

---

## 🔗 Ресурси / Resources
- [VS Code Dev Containers](https://code.visualstudio.com/docs/devcontainers/containers)
- [GitHub Codespaces Docs](https://docs.github.com/en/codespaces)
- [Android in Docker](https://github.com/thyrlian/AndroidSDK)

---

## 📊 Performance Tips

### Оптимізація Dockerfile
```dockerfile
# Кешувати dependencies окремим layer
COPY gradle/ /workspace/gradle/
COPY build.gradle.kts settings.gradle.kts /workspace/
RUN ./gradlew dependencies --no-daemon

# Копіювати код після dependencies (швидший rebuild)
COPY . /workspace/
```

### Gradle налаштування
```properties
# gradle.properties
org.gradle.daemon=true
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.configureondemand=true
```

---

**Maintained by:** RepoBuilder AI Agent 🤖  
**Last Updated:** 2025 🚦⚡  
**Environment:** JDK 17 + Android SDK 34+ + Gradle 8.5+ 🐳

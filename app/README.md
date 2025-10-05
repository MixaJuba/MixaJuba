# 🎨 App Module | Головний UI Модуль

## 📋 Призначення

Головний Android-модуль застосунку **AutoDiagPro** — збирається в APK/AAB. Містить користувацький інтерфейс на Jetpack Compose, навігацію та інтеграцію всіх компонентів системи.

## 🏗️ Архітектура

```
app/
├── src/main/
│   ├── java/com/quantumforce_code/app/
│   │   ├── MainActivity.kt          - Точка входу, запускає Compose UI
│   │   ├── App.kt                   - Application клас з Hilt DI
│   │   ├── AutoDiagProApp.kt        - Головний Composable
│   │   ├── ui/
│   │   │   ├── screens/             - Екрани додатку
│   │   │   │   ├── DashboardScreen  - Головна панель
│   │   │   │   ├── DtcScreen        - Діагностичні коди
│   │   │   │   └── LiveScreen       - Live моніторинг
│   │   │   ├── components/          - Перевикористовувані UI компоненти
│   │   │   └── theme/               - Material3 тема
│   │   └── navigation/
│   │       └── NavGraph.kt          - Граф маршрутів
│   ├── res/                         - Android ресурси
│   └── AndroidManifest.xml          - Маніфест з дозволами
└── src/test/                        - UI тести (Compose Testing)
```

## 🎯 Ключові Компоненти

### MainActivity.kt
- **Роль**: Entry point застосунку
- **Відповідальність**: Ініціалізація Compose, налаштування теми
- **DI**: `@AndroidEntryPoint` для Hilt injection

### AutoDiagProApp.kt
- **Роль**: Головний контейнер UI
- **Відповідальність**: Scaffold, навігація, глобальні компоненти
- **Стан**: Управляє глобальним UI станом

### Screens (Екрани)
- **DashboardScreen**: Головна панель з меню та статистикою
- **DtcScreen**: Читання та аналіз DTC кодів
- **LiveScreen**: Моніторинг параметрів у реальному часі

### Navigation
- **NavGraph**: Jetpack Navigation Compose
- **Маршрути**: Dashboard, DTC, Live, Settings

## 🔗 Залежності

```kotlin
// Jetpack Compose
androidx.compose.ui
androidx.compose.material3
androidx.navigation:navigation-compose

// Dependency Injection
com.google.dagger:hilt-android
androidx.hilt:hilt-navigation-compose

// ViewModel
androidx.lifecycle:lifecycle-viewmodel-compose

// Internal modules
project(":core:domain")
project(":core:data")
project(":features:dtc")
project(":features:live")
```

## 🧪 Тестування

### Unit Tests
- UI логіка ViewModels
- Навігаційні сценарії
- State management

### UI Tests (Compose Testing)
- Взаємодія з екранами
- Навігація між екранами
- Accessibility

Запуск: `./gradlew :app:test`

## 📱 Конфігурація Android

```kotlin
android {
    namespace = "com.quantumforce_code.app"
    compileSdk = 35
    
    defaultConfig {
        applicationId = "com.quantumforce_code.autodiagpro"
        minSdk = 26  // Android 8.0 для OTG/BT
        targetSdk = 35  // Android 15
    }
}
```

## 🎨 UI/UX Концепція

- **Стиль**: Кіберпанк-технічний з темною темою
- **Палітра**: Синій, бірюзовий, сірий
- **Типографія**: Roboto, Monospace для технічних даних
- **Адаптивність**: Підтримка різних розмірів екранів

## 📚 Документація

- [UI Agent Guidelines](./docs/ui-agent-guidelines.md) - Правила для AI агентів
- [Design System](#) - Компоненти та стилі (TBD)
- [Accessibility](#) - Вимоги доступності (TBD)

## 🚀 Швидкий Старт

```bash
# Збірка
./gradlew :app:assembleDebug

# Запуск тестів
./gradlew :app:test

# Встановлення на пристрій
./gradlew :app:installDebug
```

---

**Пакет**: `com.quantumforce_code.app`  
**Тип**: Android Application Module  
**Min SDK**: 26 | **Target SDK**: 35

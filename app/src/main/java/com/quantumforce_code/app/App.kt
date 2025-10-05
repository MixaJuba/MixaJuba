// 1. File Purpose: Application class for Hilt setup
// 2. Role: Initializes dependency injection and app-wide configuration
// 3. Architecture: Entry point for Dagger Hilt DI container
// 4. Lifecycle: Created once when app starts, survives activity recreations
// 5. Responsibilities:
//    - Hilt component initialization via @HiltAndroidApp
//    - App-level singletons (database, network, preferences)
//    - Global exception handling (future)
//    - Crash reporting setup (future)
// 6. Related: MainActivity.kt (uses injected dependencies)

package com.quantumforce_code.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Головний Application клас для AutoDiagPro.
 * 
 * Анотація @HiltAndroidApp генерує базовий Hilt component для додатку,
 * який надає залежності для всіх Activity, Fragment, ViewModel, тощо.
 * 
 * Цей клас створюється один раз при старті додатку і живе протягом всього
 * lifecycle'у процесу. Використовується для ініціалізації app-scope компонентів:
 * - Room Database instance
 * - Network clients (Retrofit)
 * - Shared Preferences
 * - WorkManager (для фонових задач)
 * 
 * @see MainActivity Головна activity з @AndroidEntryPoint
 * @see [Hilt Documentation](https://developer.android.com/training/dependency-injection/hilt-android)
 */
@HiltAndroidApp
class App : Application() {
    // TODO: Додати crash reporting (Firebase Crashlytics)
    // TODO: Ініціалізувати WorkManager для синхронізації даних
    // TODO: Налаштувати StrictMode для dev builds
}

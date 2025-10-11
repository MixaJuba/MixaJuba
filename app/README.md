# 📱 app/ - Android Application Module | Головний Модуль Застосунку

## 📋 File Purpose | Призначення
Головний Android модуль, який збирається в APK/AAB. Містить UI (Jetpack Compose), навігацію та інтеграцію всіх компонентів.

## 🎯 Role | Роль
Presentation layer - відповідає за відображення даних, взаємодію з користувачем та координацію між features.

---

## 📂 Структура / Structure

```
app/
├── build.gradle.kts           # Gradle конфігурація модуля
├── src/
│   ├── main/
│   │   ├── java/com/quantumforce_code/app/
│   │   │   ├── MainActivity.kt          # Entry point
│   │   │   ├── App.kt                   # Application class (Hilt)
│   │   │   ├── AutoDiagProApp.kt        # Main Compose app
│   │   │   ├── ui/
│   │   │   │   ├── screens/             # Composable screens
│   │   │   │   │   ├── DashboardScreen.kt
│   │   │   │   │   ├── DtcScreen.kt
│   │   │   │   │   └── LiveScreen.kt
│   │   │   │   ├── components/          # Reusable UI
│   │   │   │   │   └── CommonUi.kt
│   │   │   │   └── theme/               # Material Theme
│   │   │   └── navigation/
│   │   │       └── NavGraph.kt          # Navigation routes
│   │   ├── res/                         # Resources
│   │   │   ├── values/
│   │   │   │   ├── strings.xml
│   │   │   │   ├── colors.xml
│   │   │   │   └── themes.xml
│   │   │   ├── drawable/                # Icons, images
│   │   │   └── mipmap/                  # App icons
│   │   └── AndroidManifest.xml          # Permissions, activities
│   └── test/
│       └── java/com/quantumforce_code/app/
│           └── AppUiTest.kt             # UI tests
└── docs/
    └── ui-agent-guidelines.md           # UI development rules
```

---

## 🧩 Компоненти / Components

### MainActivity.kt
**Призначення:** Точка входу Android застосунку.

**Що робить:**
- Ініціалізує Jetpack Compose
- Встановлює Material Theme
- Запускає `AutoDiagProApp()` root composable

**Ключові features:**
- `@AndroidEntryPoint` - Hilt dependency injection
- Single activity architecture (рекомендація Android)

**Приклад використання:**
```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AutoDiagProTheme {
                AutoDiagProApp()
            }
        }
    }
}
```

---

### App.kt
**Призначення:** Application class для глобальної ініціалізації.

**Що робить:**
- Ініціалізує Hilt DI
- Налаштовує логування
- Встановлює глобальні конфігурації

**Приклад:**
```kotlin
@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // Init logging, crash reporting, etc.
    }
}
```

---

### AutoDiagProApp.kt
**Призначення:** Головний Compose root composable.

**Що робить:**
- Встановлює NavHost для навігації
- Координує features (dtc, live)
- Керує глобальним UI станом

**Архітектура:**
```kotlin
@Composable
fun AutoDiagProApp() {
    val navController = rememberNavController()
    
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route
        ) {
            composable(Screen.Dashboard.route) { DashboardScreen() }
            composable(Screen.Dtc.route) { DtcScreen() }
            composable(Screen.Live.route) { LiveScreen() }
        }
    }
}
```

---

## 🎨 UI Layer (ui/)

### screens/
**DashboardScreen.kt**
- Головний екран з меню
- Кнопки: Read DTC, Live Data, Settings
- Статистика: last scan, vehicle info

**DtcScreen.kt**
- Екран DTC-кодів
- LazyColumn з DTC cards
- Фільтрація: Active/Pending/Stored
- Дії: Clear codes, Export report

**LiveScreen.kt**
- Live-моніторинг параметрів
- Графіки (RPM, Speed, Temp)
- Selector для PIDs
- Real-time updates (coroutines)

---

### components/CommonUi.kt
**Призначення:** Перевикористовувані UI-компоненти.

**Містить:**
- `AutoDiagButton` - кастомна кнопка з cyberpunk стилем
- `LoadingIndicator` - індикатор завантаження
- `ErrorMessage` - error state UI
- `EmptyState` - порожній стан (no data)

**Приклад використання:**
```kotlin
@Composable
fun AutoDiagButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = CyberPunkBlue
        )
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}
```

---

### theme/
**Призначення:** Material 3 Design System.

**Містить:**
- `Color.kt` - кольорова палітра (CyberPunk theme: Blue, Purple, Neon Green)
- `Type.kt` - Typography (Roboto, sizes)
- `Theme.kt` - LightTheme/DarkTheme setup

**Кольори проекту:**
```kotlin
val CyberPunkBlue = Color(0xFF00D9FF)
val CyberPunkPurple = Color(0xFFFF00FF)
val DarkBackground = Color(0xFF0A0E27)
```

---

## 🧭 Navigation Layer (navigation/)

### NavGraph.kt
**Призначення:** Визначає маршрути навігації.

**Sealed class для routes:**
```kotlin
sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Dtc : Screen("dtc")
    object Live : Screen("live")
    object Settings : Screen("settings")
}
```

**NavHost setup:**
```kotlin
@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) { DashboardScreen(navController) }
        composable(Screen.Dtc.route) { DtcScreen() }
        composable(Screen.Live.route) { LiveScreen() }
        composable(Screen.Settings.route) { SettingsScreen() }
    }
}
```

---

## 📦 Resources (res/)

### values/
- **strings.xml** - Всі текстові рядки (українська/англійська)
- **colors.xml** - Додаткові кольори (Material палітра)
- **themes.xml** - XML теми (для legacy views)

### drawable/
- Векторні іконки (SVG → XML)
- Фонові градієнти
- Кастомні shapes (rounded buttons)

### mipmap/
- App icons (різні розміри: mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi)
- Adaptive icon (foreground + background)

---

## 📄 AndroidManifest.xml
**Призначення:** Маніфест застосунку.

**Ключові елементи:**
```xml
<manifest>
    <!-- Permissions -->
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
    <uses-permission android:name="android.permission.USB_PERMISSION" />
    
    <application
        android:name=".App"
        android:icon="@mipmap/ic_launcher"
        android:theme="@style/Theme.AutoDiagPro">
        
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

---

## 🧪 Testing (test/)

### AppUiTest.kt
**Призначення:** UI тести з Compose Testing.

**Приклади тестів:**
```kotlin
@Test
fun dashboardScreen_displaysButtons() {
    composeTestRule.setContent {
        DashboardScreen()
    }
    
    composeTestRule
        .onNodeWithText("Read DTC")
        .assertIsDisplayed()
}

@Test
fun navigation_toConnectScreen_works() {
    // Test navigation flow
}
```

---

## 🔧 build.gradle.kts
**Призначення:** Конфігурація Gradle для app модуля.

**Ключові залежності:**
```kotlin
dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    
    // Compose
    implementation("androidx.compose.ui:ui:1.6.0")
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("androidx.navigation:navigation-compose:2.7.6")
    
    // Hilt DI
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-compiler:2.50")
    
    // Project modules
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":features:dtc"))
    implementation(project(":features:live"))
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.0")
}
```

---

## 🎯 Архітектурні Принципи / Architecture Principles

### MVVM Pattern
- **View (Composables):** UI screens
- **ViewModel:** Логіка UI (у features/)
- **Model:** Domain entities (у core/)

### Single Activity Architecture
- Одна `MainActivity`
- Всі screens - Composables
- Навігація через Navigation Compose

### Unidirectional Data Flow
```
User Action → Screen → ViewModel → UseCase → Repository → Database/Network
                ↑                       ↓
            UI State Updates       Result<Data>
```

### Dependency Injection (Hilt)
- `@AndroidEntryPoint` - для activities/fragments
- `@HiltViewModel` - для ViewModels (у features/)
- Modules для dependencies (NetworkModule, DatabaseModule)

---

## 🚀 Як Додати Новий Екран / Adding New Screen

1. **Створити Screen Composable:**
```kotlin
// ui/screens/NewScreen.kt
@Composable
fun NewScreen(navController: NavController) {
    Column {
        Text("New Screen")
        AutoDiagButton("Back") { navController.popBackStack() }
    }
}
```

2. **Додати Route у NavGraph:**
```kotlin
sealed class Screen(val route: String) {
    object New : Screen("new_screen")
}

// У NavHost
composable(Screen.New.route) { NewScreen(navController) }
```

3. **Додати Навігацію:**
```kotlin
// У іншому екрані
AutoDiagButton("Go to New") {
    navController.navigate(Screen.New.route)
}
```

---

## 🔗 Ресурси / Resources
- [Jetpack Compose Docs](https://developer.android.com/jetpack/compose)
- [Material 3 Design](https://m3.material.io/)
- [Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
- [Hilt Documentation](https://dagger.dev/hilt/)

---

## 📊 UI Guidelines
Детальні правила у `docs/ui-agent-guidelines.md`:
- Accessibility (content descriptions, semantic properties)
- Theme consistency (використовувати Material colors)
- Preview functions для кожного Composable
- Error handling (LoadingState, ErrorState, SuccessState)

---

**Maintained by:** UI Agent / RepoBuilder 🤖  
**Tech Stack:** Jetpack Compose + Material 3 + Hilt + Navigation Compose  
**Last Updated:** 2025 🚦⚡  
**Style:** Cyberpunk Tech Noir 🌃🔥

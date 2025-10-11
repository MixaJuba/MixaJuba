# 🧪 Гайдлайни тестування | Testing Guidelines

**Стратегія тестування для проєкту QuantumForce_Code**

*Testing strategy for QuantumForce_Code project*

---

## 🎯 Філософія тестування | Testing Philosophy

Тестування в QuantumForce_Code - це не просто перевірка що код працює, а **гарантія якості** професійного інструменту для автомобільної діагностики. Погані тести можуть призвести до неправильної діагностики, що може коштувати грошей і репутації.

*Testing in QuantumForce_Code is not just checking that code works, but a **quality guarantee** for a professional automotive diagnostic tool. Poor tests can lead to incorrect diagnostics, which can cost money and reputation.*

**Основні принципи:**
- 🎯 **Якість > Кількість** - краще 50 хороших тестів ніж 500 поганих
- ⚡ **Швидкість** - тести мають бути швидкими (unit < 1s, integration < 10s)
- 🔄 **Repeatability** - тести завжди дають однаковий результат
- 📊 **Coverage** - aim for 70-80%+ для критичних модулів
- 🚫 **No Flaky Tests** - тести не мають випадково падати

---

## 🏛️ Піраміда тестів | Test Pyramid

```
              /\
             /  \
            / UI \          10% - E2E Tests (slow, expensive)
           /______\
          /        \
         / Integr.  \      20% - Integration Tests (medium)
        /____________\
       /              \
      /   Unit Tests   \   70% - Unit Tests (fast, cheap)
     /__________________\

```

**Розподіл зусиль:**
- **70% Unit Tests** - швидкі, ізольовані, тестують одну одиницю коду
- **20% Integration Tests** - перевіряють взаємодію між компонентами
- **10% UI/E2E Tests** - перевіряють критичні user flows

---

## 🧩 Типи тестів | Test Types

### 1. 📦 Unit Tests (70%)

**Що тестуємо:**
- UseCase логіку
- Repository implementations
- ViewModels (з fake dependencies)
- Utility functions
- Data mappers
- Protocol parsers

**Інструменти:**
- JUnit 5
- MockK (для Kotlin)
- Truth (assertions)
- Turbine (Flow testing)
- Coroutines Test

**Приклад структури:**
```
src/
├── main/kotlin/com/quantumforce_code/
│   └── domain/usecases/GetVehicleDtcsUseCase.kt
└── test/kotlin/com/quantumforce_code/
    └── domain/usecases/GetVehicleDtcsUseCaseTest.kt
```

**Dependency в build.gradle.kts:**
```kotlin
dependencies {
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("com.google.truth:truth:1.1.5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("app.cash.turbine:turbine:1.0.0")
}
```

---

### 2. 🔗 Integration Tests (20%)

**Що тестуємо:**
- Взаємодію ViewModel + UseCase + Repository
- Room database operations
- Transport + Protocol взаємодія
- Hilt DI configuration

**Інструменти:**
- Robolectric (для Android components без emulator)
- Room in-memory database
- Fake implementations

**Приклад структури:**
```
src/
└── test/kotlin/com/quantumforce_code/
    └── features/dtc/
        └── DtcFeatureIntegrationTest.kt
```

---

### 3. 🖥️ UI Tests (10%)

**Що тестуємо:**
- Compose UI компоненти
- User interactions (clicks, input)
- Navigation flows
- Critical user scenarios

**Інструменти:**
- Compose Testing
- Espresso (для Views якщо є)
- UI Automator (для cross-app scenarios)

**Приклад структури:**
```
src/
└── androidTest/kotlin/com/quantumforce_code/
    └── ui/screens/
        └── DtcScreenTest.kt
```

**Dependency:**
```kotlin
androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.4")
debugImplementation("androidx.compose.ui:ui-test-manifest:1.5.4")
```

---

## 📝 Написання хороших тестів | Writing Good Tests

### AAA Pattern (Arrange-Act-Assert)

```kotlin
@Test
fun `getDtcs returns list of DTCs when vehicle exists`() {
    // Arrange (Підготовка)
    val fakeRepository = FakeDtcRepository()
    fakeRepository.setDtcs(listOf(
        Dtc("P0301", "Misfire Cylinder 1"),
        Dtc("P0420", "Catalyst System Low Efficiency")
    ))
    val useCase = GetVehicleDtcsUseCase(fakeRepository)
    
    // Act (Дія)
    val result = runBlocking { useCase("VIN123") }
    
    // Assert (Перевірка)
    assertThat(result).isNotEmpty()
    assertThat(result).hasSize(2)
    assertThat(result[0].code).isEqualTo("P0301")
}
```

---

### Given-When-Then (BDD Style)

```kotlin
@Test
fun `given invalid VIN when getDtcs called then returns empty list`() {
    // Given
    val repository = FakeDtcRepository()
    val useCase = GetVehicleDtcsUseCase(repository)
    
    // When
    val result = runBlocking { useCase("INVALID_VIN") }
    
    // Then
    assertThat(result).isEmpty()
}
```

---

### Тестування корутин | Testing Coroutines

```kotlin
@ExperimentalCoroutinesApi
class GetVehicleDtcsUseCaseTest {
    // Test dispatcher для контролю часу
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `useCase executes on correct dispatcher`() = runTest {
        val repository = FakeDtcRepository()
        val useCase = GetVehicleDtcsUseCase(repository)
        
        val job = launch {
            useCase("VIN123")
        }
        
        // Контролюємо виконання
        advanceUntilIdle()
        
        assertThat(repository.wasInvoked).isTrue()
    }
}
```

---

### Тестування Flows | Testing Flows

```kotlin
@Test
fun `ViewModel emits correct states`() = runTest {
    val repository = FakeDtcRepository()
    val viewModel = DtcViewModel(GetVehicleDtcsUseCase(repository))
    
    // Turbine для тестування Flow
    viewModel.state.test {
        // Initial state
        assertThat(awaitItem()).isEqualTo(DtcState.Loading)
        
        // Trigger action
        viewModel.loadDtcs("VIN123")
        
        // Loading state
        assertThat(awaitItem()).isEqualTo(DtcState.Loading)
        
        // Success state
        val successState = awaitItem() as DtcState.Success
        assertThat(successState.dtcs).hasSize(2)
    }
}
```

---

## 🎯 Приклади тестів | Test Examples

### Приклад 1: UseCase Unit Test

```kotlin
package com.quantumforce_code.domain.usecases

import com.google.common.truth.Truth.assertThat
import com.quantumforce_code.domain.model.Dtc
import com.quantumforce_code.domain.repository.DtcRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetVehicleDtcsUseCaseTest {

    private lateinit var repository: DtcRepository
    private lateinit var useCase: GetVehicleDtcsUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetVehicleDtcsUseCase(repository)
    }

    @Test
    fun `invoke with valid VIN returns DTCs from repository`() = runTest {
        // Given
        val vin = "1HGBH41JXMN109186"
        val expectedDtcs = listOf(
            Dtc("P0301", "Cylinder 1 Misfire Detected", "Engine"),
            Dtc("P0420", "Catalyst System Efficiency Below Threshold", "Emission")
        )
        coEvery { repository.getDtcs(vin) } returns Result.success(expectedDtcs)

        // When
        val result = useCase(vin)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(expectedDtcs)
        coVerify(exactly = 1) { repository.getDtcs(vin) }
    }

    @Test
    fun `invoke with empty VIN returns failure`() = runTest {
        // Given
        val emptyVin = ""

        // When
        val result = useCase(emptyVin)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull())
            .hasMessageThat()
            .contains("VIN cannot be empty")
        coVerify(exactly = 0) { repository.getDtcs(any()) }
    }

    @Test
    fun `invoke propagates repository errors`() = runTest {
        // Given
        val vin = "1HGBH41JXMN109186"
        val exception = RuntimeException("Database connection failed")
        coEvery { repository.getDtcs(vin) } returns Result.failure(exception)

        // When
        val result = useCase(vin)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isSameInstanceAs(exception)
    }

    @Test
    fun `invoke with VIN containing only spaces returns failure`() = runTest {
        // Given
        val invalidVin = "    "

        // When
        val result = useCase(invalidVin)

        // Then
        assertThat(result.isFailure).isTrue()
    }
}
```

---

### Приклад 2: Compose UI Test

```kotlin
package com.quantumforce_code.ui.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.google.common.truth.Truth.assertThat
import com.quantumforce_code.domain.model.Dtc
import com.quantumforce_code.ui.theme.QuantumForceTheme
import org.junit.Rule
import org.junit.Test

class DtcScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun dtcScreen_displaysLoadingState() {
        // Given
        val state = DtcState.Loading

        // When
        composeTestRule.setContent {
            QuantumForceTheme {
                DtcScreen(
                    state = state,
                    onScanClick = {},
                    onClearClick = {},
                    onDtcClick = {}
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithTag("loading_indicator")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun dtcScreen_displaysListOfDtcs() {
        // Given
        val dtcs = listOf(
            Dtc("P0301", "Cylinder 1 Misfire", "Engine"),
            Dtc("P0420", "Catalyst Issue", "Emission")
        )
        val state = DtcState.Success(dtcs)

        // When
        composeTestRule.setContent {
            QuantumForceTheme {
                DtcScreen(
                    state = state,
                    onScanClick = {},
                    onClearClick = {},
                    onDtcClick = {}
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("P0301")
            .assertExists()
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Cylinder 1 Misfire")
            .assertExists()
        
        composeTestRule
            .onNodeWithText("P0420")
            .assertExists()
    }

    @Test
    fun dtcScreen_scanButtonClick_triggersCallback() {
        // Given
        var scanClicked = false
        val state = DtcState.Initial

        // When
        composeTestRule.setContent {
            QuantumForceTheme {
                DtcScreen(
                    state = state,
                    onScanClick = { scanClicked = true },
                    onClearClick = {},
                    onDtcClick = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("Scan Vehicle")
            .performClick()

        // Then
        assertThat(scanClicked).isTrue()
    }

    @Test
    fun dtcScreen_dtcItemClick_triggersCallbackWithCorrectDtc() {
        // Given
        var clickedDtc: Dtc? = null
        val targetDtc = Dtc("P0301", "Cylinder 1 Misfire", "Engine")
        val state = DtcState.Success(listOf(targetDtc))

        // When
        composeTestRule.setContent {
            QuantumForceTheme {
                DtcScreen(
                    state = state,
                    onScanClick = {},
                    onClearClick = {},
                    onDtcClick = { clickedDtc = it }
                )
            }
        }

        composeTestRule
            .onNodeWithText("P0301")
            .performClick()

        // Then
        assertThat(clickedDtc).isEqualTo(targetDtc)
    }

    @Test
    fun dtcScreen_errorState_displaysErrorMessage() {
        // Given
        val errorMessage = "Failed to connect to vehicle"
        val state = DtcState.Error(errorMessage)

        // When
        composeTestRule.setContent {
            QuantumForceTheme {
                DtcScreen(
                    state = state,
                    onScanClick = {},
                    onClearClick = {},
                    onDtcClick = {}
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(errorMessage)
            .assertExists()
            .assertIsDisplayed()
    }
}
```

---

## 🛠️ Інструменти тестування | Testing Tools

### Основні бібліотеки | Core Libraries

```kotlin
// build.gradle.kts (project level)
dependencies {
    // JUnit 5 (latest)
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    
    // MockK (Kotlin mocking)
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("io.mockk:mockk-android:1.13.8")
    
    // Truth (better assertions)
    testImplementation("com.google.truth:truth:1.1.5")
    
    // Coroutines Test
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    
    // Turbine (Flow testing)
    testImplementation("app.cash.turbine:turbine:1.0.0")
    
    // Robolectric (Android without emulator)
    testImplementation("org.robolectric:robolectric:4.11")
    
    // Room testing
    testImplementation("androidx.room:room-testing:2.6.0")
    
    // Compose Testing
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.4")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.5.4")
    
    // Hilt Testing
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.48")
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:2.48")
}
```

---

### Налаштування JUnit 5

```kotlin
// build.gradle.kts (module level)
android {
    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}
```

---

## 📊 Code Coverage | Покриття коду

### Цілі coverage | Coverage Goals

**Мінімальні вимоги:**
- 🎯 **Domain layer**: 80%+ (критична бізнес-логіка)
- 🎯 **Data layer**: 70%+ (Repository, DAOs)
- 🎯 **ViewModels**: 75%+ (UI logic)
- 🎯 **Protocol parsers**: 85%+ (critical parsing logic)
- 📊 **UI layer**: 50%+ (Compose testing важко)
- 📊 **Transport layer**: 60%+ (hardware залежність)

---

### Налаштування JaCoCo

```kotlin
// build.gradle.kts
plugins {
    id("jacoco")
}

android {
    buildTypes {
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
    }
}

jacoco {
    toolVersion = "0.8.11"
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")
    
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
    
    sourceDirectories.setFrom(files("src/main/kotlin"))
    classDirectories.setFrom(files("build/tmp/kotlin-classes/debug"))
    executionData.setFrom(fileTree(buildDir) {
        include("**/*.exec", "**/*.ec")
    })
    
    // Exclude generated files
    classDirectories.setFrom(files(classDirectories.files.map {
        fileTree(it) {
            exclude(
                "**/R.class",
                "**/R$*.class",
                "**/*\$ViewInjector*.*",
                "**/*\$ViewBinder*.*",
                "**/BuildConfig.*",
                "**/Manifest*.*",
                "**/*_Hilt*.*",
                "**/*Module*.*",
                "**/*Dagger*.*",
                "**/*MembersInjector*.*"
            )
        }
    }))
}
```

**Запуск:**
```bash
# Запустити тести з coverage
./gradlew jacocoTestReport

# Результати в
open build/reports/jacoco/jacocoTestReport/html/index.html
```

---

## 🚀 Команди для тестування | Testing Commands

### Локальні команди | Local Commands

```bash
# Запустити всі unit тести
./gradlew test

# Запустити тести конкретного модуля
./gradlew :core:domain:test

# Запустити тести з coverage
./gradlew testDebugUnitTest jacocoTestReport

# Запустити integration/UI тести (потребує emulator)
./gradlew connectedAndroidTest

# Запустити specific test class
./gradlew test --tests "GetVehicleDtcsUseCaseTest"

# Запустити specific test method
./gradlew test --tests "GetVehicleDtcsUseCaseTest.invoke with valid VIN returns DTCs"

# Debug mode
./gradlew test --debug-jvm

# Generate HTML test report
./gradlew test
open build/reports/tests/test/index.html
```

---

### CI/CD команди | CI/CD Commands

```yaml
# .github/workflows/android-ci.yml
name: Android CI

on:
  pull_request:
    branches: [ main, develop ]
  push:
    branches: [ main, develop ]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
          
      - name: Grant execute permission for gradlew
        run: chmod +x gradlew
        
      - name: Run Unit Tests
        run: ./gradlew test
        
      - name: Generate Coverage Report
        run: ./gradlew jacocoTestReport
        
      - name: Upload Coverage to Codecov
        uses: codecov/codecov-action@v3
        with:
          files: ./build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml
          fail_ci_if_error: true
          
      - name: Run Lint
        run: ./gradlew lint
        
      - name: Upload Test Results
        if: always()
        uses: actions/upload-artifact@v3
        with:
          name: test-results
          path: '**/build/test-results/**/*.xml'
          
      - name: Comment Coverage on PR
        uses: madrapps/jacoco-report@v1.3
        with:
          paths: build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml
          token: ${{ secrets.GITHUB_TOKEN }}
          min-coverage-overall: 70
          min-coverage-changed-files: 80
```

---

## 🚦 CI Gates | Контрольні точки в CI

### Pull Request Gates

**Мінімальні вимоги для merge:**

1. ✅ **All tests pass** - жодних failing tests
2. ✅ **Coverage gate** - мінімум 70% для змінених файлів
3. ✅ **Lint pass** - без critical/error issues
4. ✅ **Build success** - проєкт компілюється
5. ✅ **Code review approved** - від мінімум 1 reviewer

**Warning gates (не блокують, але вимагають пояснення):**
- ⚠️ Coverage down >5% - чому покриття впало?
- ⚠️ New flaky tests - чи тест стабільний?
- ⚠️ Performance regression - чи не уповільнилося?

---

### Приклад configuration

```kotlin
// build.gradle.kts
tasks.register("prGate") {
    group = "verification"
    description = "Run all checks required for PR"
    
    dependsOn(
        "test",                  // Unit tests
        "jacocoTestReport",      // Coverage
        "lint",                  // Static analysis
        "detekt",                // Kotlin linting
        "assembleDebug"          // Build check
    )
    
    doLast {
        println("✅ All PR gates passed!")
    }
}
```

**Використання:**
```bash
./gradlew prGate
```

---

## 💡 Best Practices | Найкращі практики

### DO ✅

1. **Тестуй поведінку, а не реалізацію**
   ```kotlin
   // ✅ Good - tests behavior
   @Test
   fun `user can see list of DTCs after scan`()
   
   // ❌ Bad - tests implementation
   @Test
   fun `repository.getDtcs is called with correct parameter`()
   ```

2. **Один концепт на тест**
   ```kotlin
   // ✅ Good
   @Test
   fun `returns error when VIN is empty`()
   
   @Test
   fun `returns error when VIN is invalid format`()
   
   // ❌ Bad
   @Test
   fun `validates VIN correctly`() {
       // tests empty, invalid format, too long, too short...
   }
   ```

3. **Використовуй descriptive test names**
   ```kotlin
   // ✅ Good
   @Test
   fun `clears all DTCs when clear button is clicked and user confirms`()
   
   // ❌ Bad
   @Test
   fun `test1`()
   ```

4. **Ізолюй тести** - кожен тест незалежний
   ```kotlin
   @Before
   fun setup() {
       // Fresh state for each test
       repository = FakeDtcRepository()
       useCase = GetVehicleDtcsUseCase(repository)
   }
   ```

---

### DON'T ❌

1. **Не тестуй Android framework**
   ```kotlin
   // ❌ Bad - testing Android framework
   @Test
   fun `TextView displays text correctly`() {
       textView.text = "test"
       assertEquals("test", textView.text)
   }
   ```

2. **Не використовуй sleep() у тестах**
   ```kotlin
   // ❌ Bad
   @Test
   fun test() {
       viewModel.loadData()
       Thread.sleep(1000) // Flaky!
       assert(viewModel.data.isNotEmpty())
   }
   
   // ✅ Good
   @Test
   fun test() = runTest {
       viewModel.loadData()
       advanceUntilIdle() // Controlled timing
       assert(viewModel.data.isNotEmpty())
   }
   ```

3. **Не ігноруй failing tests**
   ```kotlin
   // ❌ Bad
   @Ignore("TODO: fix later")
   @Test
   fun importantTest() { ... }
   ```

4. **Не робі тести залежними один від одного**
   ```kotlin
   // ❌ Bad
   @Test
   fun test1_setupData() { ... }
   
   @Test
   fun test2_useData() { /* depends on test1 */ }
   ```

---

## 📋 Чек-ліст тестування | Testing Checklist

### Перед написанням коду:

- [ ] Розумію що саме потрібно протестувати
- [ ] Знаю які edge cases можливі
- [ ] Маю план тестів (happy path + edge cases + errors)

### При написанні коду:

- [ ] Пишу тестований код (single responsibility, dependency injection)
- [ ] Додаю тести одразу після реалізації
- [ ] Запускаю тести локально після кожної зміни

### Перед commit:

- [ ] Всі нові тести проходять
- [ ] Всі старі тести все ще проходять
- [ ] Coverage для нового коду >70%
- [ ] Немає ignored/skipped tests без Issue
- [ ] Тести не flaky (запустив 5 разів - 5 разів pass)

### Під час Code Review:

- [ ] Перевірити що є тести для нового коду
- [ ] Перевірити якість тестів (чи тестують поведінку)
- [ ] Перевірити coverage report
- [ ] Перевірити що немає test pollution

---

## 🔗 Пов'язані ресурси | Related Resources

### Внутрішні документи:
- [Implementation Examples](IMPLEMENTATION_EXAMPLES.md) - приклади тестів
- [Contributing Guidelines](CONTRIBUTING.md) - стандарти тестування
- [CI/CD Workflows](../.github/workflows/) - автоматизація

### Зовнішні ресурси:
- [Android Testing Guide](https://developer.android.com/training/testing)
- [Compose Testing Cheatsheet](https://developer.android.com/jetpack/compose/testing-cheatsheet)
- [MockK Documentation](https://mockk.io/)
- [Turbine (Flow testing)](https://github.com/cashapp/turbine)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)

---

## 📞 Питання і підтримка | Questions & Support

**Не впевнений як тестувати?**
- Подивись приклади в [IMPLEMENTATION_EXAMPLES.md](IMPLEMENTATION_EXAMPLES.md)
- Звернись до Testing Agent в AI_AGENT_ROLE.md

**Тести падають?**
- Переконайся що використовуєш test dispatcher для coroutines
- Перевір що моки налаштовані правильно
- Використовуй `runTest { }` для coroutine tests

**Low coverage?**
- Зосередься на критичних шляхах спочатку
- Тестуй публічні API, не приватні методи
- Використовуй JaCoCo для виявлення untested code

---

**Версія:** 1.0.0  
**Дата:** 2024  
**Maintainer:** Testing Agent + RepoBuilder

**Повернутися до:** [Головна документація](index.md)

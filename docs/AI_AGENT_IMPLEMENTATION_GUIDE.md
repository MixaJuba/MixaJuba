# 🤖 Повний Посібник для AI-Агентів з Впровадження Модульної Архітектури

## 📚 Документація проєкту

Ви — AI-агент (Copilot, Claude, Codex), який впроваджує модульну архітектуру для Android-додатку **QuantumForce_Code** - професійного автомобільного діагностичного сканера.

### Основні документи (читати в цьому порядку):

1. **README.md** - Загальний огляд проєкту, візія, цілі
2. **AUTOMOTIVE_DIAGNOSTIC_SOFTWARE_GUIDE.md** - Повний технічний гайд
3. **QUICK_START_GUIDE_UA.md** - Швидкий старт для розробників
4. **AI_AGENT_PROMPTS_LIBRARY.md** - Бібліотека промптів для різних задач

### Архітектурна документація (ваші основні джерела):

5. **docs/MODULAR_ARCHITECTURE_GUIDE.md** ⭐ - Головний посібник з архітектури
   - Принципи Clean Architecture
   - Опис всіх шарів і модулів
   - Діаграми взаємодії
   - Чек-лист впровадження

6. **docs/IMPLEMENTATION_EXAMPLES.md** ⭐ - Реальні приклади коду
   - Domain Layer: UseCase, Repository
   - Data Layer: RepositoryImpl, Mappers
   - Transport Layer: Bluetooth, USB
   - Protocol Layer: ELM327
   - Features Layer: ViewModel з MVI
   - UI Layer: Jetpack Compose
   - Integration: DI з Hilt

7. **docs/INTERFACE_CONTRACTS.md** ⭐ - Єдине джерело істини
   - Список всіх інтерфейсів
   - Методи з сигнатурами
   - Залежності між шарами
   - Матриця відповідальностей

8. **docs/architecture-visualization.md** - Візуальні діаграми
   - Mermaid схеми
   - Структура директорій
   - Data flow diagrams

---

## 🎯 Ваші завдання як AI-агента

### Етап 1: Розуміння (10-15 хв)
1. Прочитай `MODULAR_ARCHITECTURE_GUIDE.md` (розділи 1-3)
2. Вивчи структуру проєкту в `architecture-visualization.md`
3. Ознайомся з існуючими інтерфейсами в `INTERFACE_CONTRACTS.md`

### Етап 2: Планування (5-10 хв)
1. Визнач який модуль ти будеш реалізовувати
2. Знайди відповідні інтерфейси в `INTERFACE_CONTRACTS.md`
3. Вивчи приклади в `IMPLEMENTATION_EXAMPLES.md`

### Етап 3: Реалізація (основна робота)
1. Створи файли згідно структури
2. Використовуй приклади як шаблони
3. Дотримуйся контрактів інтерфейсів
4. Додай KDoc коментарі

### Етап 4: Інтеграція (5-10 хв)
1. Перевір залежності між модулями
2. Додай Hilt DI модулі
3. Оновлення build.gradle.kts
4. Тестування інтеграції

---

## 📋 Структура модулів (file system)

```
QuantumForce_Code/
├── app/                          # UI Layer - Android Application
│   └── src/main/
│       ├── java/com/quantumforce_code/app/
│       │   ├── MainActivity.kt
│       │   ├── App.kt (Hilt)
│       │   ├── ui/
│       │   │   ├── screens/
│       │   │   │   ├── DtcScreen.kt
│       │   │   │   ├── LiveScreen.kt
│       │   │   │   └── DashboardScreen.kt
│       │   │   └── components/
│       │   │       └── CommonUi.kt
│       │   ├── navigation/
│       │   │   └── NavGraph.kt
│       │   └── di/
│       │       └── AppModule.kt
│       └── AndroidManifest.xml
│
├── core/                         # Core Modules
│   ├── domain/                   # Business Logic (No Android deps)
│   │   └── src/main/kotlin/com/quantumforce_code/core/domain/
│   │       ├── UseCase.kt        ✅ ГОТОВО
│   │       ├── DtcCode.kt        ✅ ГОТОВО
│   │       ├── Vehicle.kt        ✅ ГОТОВО
│   │       ├── DiagnosticSession.kt ✅ ГОТОВО
│   │       ├── repository/
│   │       │   ├── DtcRepository.kt        ✅ ГОТОВО
│   │       │   ├── VehicleRepository.kt    ✅ ГОТОВО
│   │       │   └── DiagnosticSessionRepository.kt ✅ ГОТОВО
│   │       └── usecase/
│   │           ├── GetDtcCodesUseCase.kt   ✅ ГОТОВО
│   │           ├── ClearDtcCodesUseCase.kt ✅ ГОТОВО
│   │           ├── GetVehicleUseCase.kt    ⬜ TODO
│   │           └── SaveVehicleUseCase.kt   ⬜ TODO
│   │
│   └── data/                     # Data Layer (Repository implementations)
│       └── src/main/kotlin/com/quantumforce_code/core/data/
│           ├── db/
│           │   ├── AppDatabase.kt          ⬜ TODO
│           │   ├── DtcDao.kt               ⬜ TODO
│           │   ├── DtcEntity.kt            ⬜ TODO
│           │   ├── VehicleDao.kt           ⬜ TODO
│           │   └── VehicleEntity.kt        ⬜ TODO
│           ├── repo/
│           │   ├── DtcRepositoryImpl.kt    ⬜ TODO
│           │   └── VehicleRepositoryImpl.kt ⬜ TODO
│           └── DataMappers.kt              ⬜ TODO (є приклад)
│
├── hardware/                     # Hardware Communication
│   └── transport/
│       └── src/main/kotlin/com/quantumforce_code/hardware/transport/
│           ├── Port.kt                   ✅ ГОТОВО
│           ├── ConnectionManager.kt      ✅ ГОТОВО
│           ├── BluetoothPort.kt          ⬜ TODO (є приклад)
│           ├── UsbSerialPort.kt          ⬜ TODO
│           ├── TcpPort.kt                ⬜ TODO
│           └── ConnectionManagerImpl.kt  ⬜ TODO (є приклад)
│
├── protocols/                    # OBD-II Protocols
│   └── obd/
│       └── src/main/kotlin/com/quantumforce_code/protocols/obd/
│           ├── ObdInterface.kt         ✅ ГОТОВО
│           ├── ObdProtocol.kt          ✅ ГОТОВО
│           ├── ObdCommand.kt           🔄 ЧАСТКОВО (є приклад)
│           ├── ObdResponse.kt          ✅ ГОТОВО
│           ├── Elm327.kt               ⬜ TODO (є приклад)
│           ├── PidParser.kt            ⬜ TODO
│           └── DtcParser.kt            ⬜ TODO
│
├── features/                     # Feature Modules
│   ├── dtc/
│   │   └── src/main/kotlin/com/quantumforce_code/features/dtc/
│   │       ├── DtcViewModel.kt       ⬜ TODO (є повний приклад)
│   │       ├── DtcUiState.kt         🔄 ЧАСТКОВО
│   │       ├── DtcScreenModel.kt     🔄 ЧАСТКОВО
│   │       └── DtcRepositoryBridge.kt ⬜ TODO
│   │
│   └── live/
│       └── src/main/kotlin/com/quantumforce_code/features/live/
│           ├── LiveDataViewModel.kt   ⬜ TODO
│           ├── LiveUiState.kt         ⬜ TODO
│           └── LiveChartRenderer.kt   🔄 ЧАСТКОВО
│
├── security/                     # Security Module
│   └── src/main/kotlin/com/quantumforce_code/security/
│       ├── SecurityPolicy.kt     🔄 ЧАСТКОВО
│       └── LoggerPolicy.kt       🔄 ЧАСТКОВО
│
└── updates/                      # Updates Module
    └── src/main/kotlin/com/quantumforce_code/updates/
        ├── UpdateChecker.kt      ⬜ TODO
        ├── UpdateRepository.kt   🔄 ЧАСТКОВО
        └── ManifestClient.kt     🔄 ЧАСТКОВО
```

**Легенда:**
- ✅ ГОТОВО - Інтерфейс/клас повністю реалізований
- 🔄 ЧАСТКОВО - Є placeholder, потрібна реалізація
- ⬜ TODO - Потрібно створити з нуля

---

## 🛠️ Інструкції для кожного типу модуля

### Domain Layer (core/domain) - Ваш пріоритет #1

**Що робити:**
1. Створюй Use Cases згідно шаблону:
   ```kotlin
   class MyUseCase(
       private val repository: Repository
   ) : UseCase<Params, Result>() {
       data class Params(...)
       override suspend fun execute(parameters: Params): Result<Result> {
           // Бізнес-логіка
       }
   }
   ```

2. Кожен Use Case має:
   - Конструктор з repository залежностями
   - Inner data class Params
   - Метод execute з бізнес-логікою
   - KDoc коментарі українською та англійською

**Приклади:**
- `GetDtcCodesUseCase.kt` ✅ (використовуй як шаблон)
- `ClearDtcCodesUseCase.kt` ✅ (використовуй як шаблон)

**Що створити:**
- `GetVehicleUseCase` - отримання авто за ID
- `SaveVehicleUseCase` - збереження нового авто
- `GetDiagnosticSessionUseCase` - отримання активної сесії
- `StartDiagnosticSessionUseCase` - початок нової сесії
- `CompleteDiagnosticSessionUseCase` - завершення сесії

---

### Data Layer (core/data) - Пріоритет #2

**Що робити:**
1. Створюй Room entities:
   ```kotlin
   @Entity(tableName = "table_name")
   data class MyEntity(
       @PrimaryKey val id: String,
       @ColumnInfo(name = "field") val field: Type
   )
   ```

2. Створюй DAOs:
   ```kotlin
   @Dao
   interface MyDao {
       @Query("SELECT * FROM table_name")
       suspend fun getAll(): List<MyEntity>
       
       @Insert(onConflict = OnConflictStrategy.REPLACE)
       suspend fun insert(entity: MyEntity)
   }
   ```

3. Реалізуй repositories:
   ```kotlin
   class MyRepositoryImpl(
       private val dao: MyDao,
       private val otherDependency: Dependency
   ) : MyRepository {
       // Імплементація всіх методів з інтерфейсу
   }
   ```

**Важливо:**
- Repository має працювати з domain моделями, а не entities
- Використовуй DataMappers для конвертації
- Всі операції suspend functions
- Обробляй помилки через Result<T>

**Приклад:**
Див. `IMPLEMENTATION_EXAMPLES.md` розділ 2

---

### Transport Layer (hardware/transport) - Пріоритет #3

**Що робити:**
1. Реалізуй Port інтерфейс для кожного типу:
   ```kotlin
   class BluetoothPort(
       private val device: BluetoothDevice,
       private val context: Context
   ) : Port {
       private var socket: BluetoothSocket? = null
       // ... реалізація всіх методів
   }
   ```

2. Реалізуй ConnectionManager:
   ```kotlin
   class ConnectionManagerImpl(
       private val context: Context
   ) : ConnectionManager {
       private val _connectionState = MutableStateFlow(...)
       // ... реалізація всіх методів
   }
   ```

**Важливо:**
- Всі IO операції через withContext(Dispatchers.IO)
- Обробляй IOException
- Використовуй Result<T> для повернення
- Не забувай close() у finally блоках

**Приклад:**
Див. `IMPLEMENTATION_EXAMPLES.md` розділ 3 (повна реалізація BluetoothPort)

---

### Protocol Layer (protocols/obd) - Пріоритет #4

**Що робити:**
1. Реалізуй ObdInterface для конкретного адаптера:
   ```kotlin
   class Elm327Adapter(
       private val port: Port
   ) : ObdInterface {
       private var currentProtocol: ObdProtocol? = null
       
       override suspend fun initialize(): Result<Boolean> {
           // Ініціалізація адаптера
       }
       
       override suspend fun sendCommand(command: ObdCommand): Result<ObdResponse> {
           // Відправка команди та парсинг відповіді
       }
       // ... інші методи
   }
   ```

2. Створи парсери:
   ```kotlin
   object PidParser {
       fun parse(rawData: String, pid: String): PidData {
           // Парсинг hex даних у значення
       }
   }
   ```

**Важливо:**
- Всі команди через Port interface
- Парсинг hex відповідей у людські значення
- Обробка всіх типів помилок (NO DATA, ERROR, TIMEOUT)
- Таймаути для кожної команди

**Приклад:**
Див. `IMPLEMENTATION_EXAMPLES.md` розділ 4 (повна реалізація Elm327Adapter)

---

### Features Layer (features/dtc, features/live) - Пріоритет #5

**Що робити:**
1. Створи ViewModel з MVI pattern:
   ```kotlin
   class MyViewModel(
       private val useCase: UseCase
   ) : ViewModel() {
       private val _uiState = MutableStateFlow(MyUiState())
       val uiState: StateFlow<MyUiState> = _uiState.asStateFlow()
       
       private val _effects = MutableSharedFlow<MyEffect>()
       val effects: SharedFlow<MyEffect> = _effects.asSharedFlow()
       
       fun handleEvent(event: MyEvent) {
           // Обробка подій
       }
   }
   ```

2. Визнач UiState:
   ```kotlin
   data class MyUiState(
       val isLoading: Boolean = false,
       val data: List<Item> = emptyList(),
       val error: String? = null
   )
   ```

3. Визнач Events та Effects:
   ```kotlin
   sealed class MyEvent {
       object Load : MyEvent()
       data class Select(val item: Item) : MyEvent()
   }
   
   sealed class MyEffect {
       data class ShowToast(val message: String) : MyEffect()
       object NavigateBack : MyEffect()
   }
   ```

**Важливо:**
- Використовуй StateFlow для стану
- SharedFlow для одноразових ефектів
- Всі операції через viewModelScope.launch
- Обробляй Result з use cases

**Приклад:**
Див. `IMPLEMENTATION_EXAMPLES.md` розділ 5 (повна реалізація DtcViewModel)

---

### UI Layer (app/ui) - Пріоритет #6

**Що робити:**
1. Створи Compose screens:
   ```kotlin
   @Composable
   fun MyScreen(
       viewModel: MyViewModel,
       onNavigate: () -> Unit
   ) {
       val uiState by viewModel.uiState.collectAsState()
       
       LaunchedEffect(Unit) {
           viewModel.effects.collect { effect ->
               // Обробка effects
           }
       }
       
       Scaffold(...) {
           // UI content
       }
   }
   ```

2. Створи reusable composables:
   ```kotlin
   @Composable
   fun MyCard(
       data: Data,
       onClick: () -> Unit
   ) {
       Card(onClick = onClick) {
           // Card content
       }
   }
   ```

**Важливо:**
- Material 3 design
- collectAsState для State Flow
- LaunchedEffect для Effects
- Modifier chains для styling

**Приклад:**
Див. `IMPLEMENTATION_EXAMPLES.md` розділ 6 (повна DtcScreen)

---

## 🔗 Dependency Injection з Hilt

### Кроки інтеграції:

1. **Додай Hilt до build.gradle.kts:**
   ```kotlin
   plugins {
       id("com.google.dagger.hilt.android")
       kotlin("kapt")
   }
   
   dependencies {
       implementation("com.google.dagger:hilt-android:2.48")
       kapt("com.google.dagger:hilt-compiler:2.48")
   }
   ```

2. **Створи Application class:**
   ```kotlin
   @HiltAndroidApp
   class App : Application()
   ```

3. **Створи DI модулі:**
   ```kotlin
   @Module
   @InstallIn(SingletonComponent::class)
   object DataModule {
       
       @Provides
       @Singleton
       fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
           return AppDatabase.create(context)
       }
       
       @Provides
       fun provideDtcRepository(
           database: AppDatabase,
           obdInterface: ObdInterface
       ): DtcRepository {
           return DtcRepositoryImpl(database.dtcDao(), obdInterface)
       }
   }
   ```

4. **Inject у ViewModel:**
   ```kotlin
   @HiltViewModel
   class MyViewModel @Inject constructor(
       private val useCase: UseCase
   ) : ViewModel()
   ```

5. **Inject у Activity:**
   ```kotlin
   @AndroidEntryPoint
   class MainActivity : ComponentActivity()
   ```

**Повний приклад:**
Див. `IMPLEMENTATION_EXAMPLES.md` розділ 7

---

## ✅ Чек-лист перед commit

Перед тим як закоммітити код, перевір:

### Code Quality
- [ ] Всі методи інтерфейсу реалізовані
- [ ] Додані KDoc коментарі українською
- [ ] Обробка помилок через Result<T>
- [ ] Всі suspend функції використовують відповідний Dispatcher
- [ ] Немає hardcoded values (використовуй constants)
- [ ] Логування додано для важливих операцій

### Architecture
- [ ] Дотримання Dependency Inversion (domain не залежить від деталей)
- [ ] Правильні залежності між шарами
- [ ] Використання інтерфейсів замість конкретних класів
- [ ] Immutability для data classes

### Testing (якщо додаєш тести)
- [ ] Unit тести для Use Cases
- [ ] Unit тести для Repositories (з моками)
- [ ] Integration тести для критичної функціональності

### Documentation
- [ ] Оновлено README якщо додано нову функціональність
- [ ] Оновлено INTERFACE_CONTRACTS.md якщо додано нові інтерфейси
- [ ] Додано приклади у IMPLEMENTATION_EXAMPLES.md якщо складна логіка

---

## 🎓 Найкращі практики

### 1. Іменування
- **Interfaces**: `Repository`, `Manager`, `Interface`, `Parser`
- **Implementations**: `RepositoryImpl`, `ManagerImpl`, `Elm327Adapter`
- **Use Cases**: `GetXxxUseCase`, `SaveXxxUseCase`, `ClearXxxUseCase`
- **ViewModels**: `XxxViewModel`
- **UiState**: `XxxUiState`
- **Events**: `XxxEvent`
- **Effects**: `XxxEffect`

### 2. Package Structure
```
com.quantumforce_code
├── app/                  # UI & DI
├── core/
│   ├── domain/          # Business logic
│   └── data/            # Implementations
├── hardware/            # Transport layer
├── protocols/           # OBD protocols
├── features/            # Feature modules
├── security/            # Security
└── updates/             # Updates
```

### 3. Коментарі
```kotlin
/**
 * Короткий опис українською.
 * 
 * Призначення: Детальний опис призначення класу/методу.
 * Принципи: Які SOLID принципи використовуються.
 * 
 * Приклад використання:
 * ```
 * val result = myFunction(params)
 * ```
 * 
 * @param param опис параметра
 * @return опис результату
 */
```

### 4. Error Handling
```kotlin
suspend fun myFunction(): Result<Data> {
    return try {
        val data = fetchData()
        Result.success(data)
    } catch (e: IOException) {
        Result.failure(e)
    } catch (e: Exception) {
        Result.failure(Exception("Unexpected error: ${e.message}"))
    }
}
```

### 5. Coroutines
```kotlin
// Repository
override suspend fun getData(): Data = withContext(Dispatchers.IO) {
    // IO операції
}

// ViewModel
fun loadData() {
    viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        
        val result = useCase(params)
        
        _uiState.update { 
            it.copy(
                isLoading = false,
                data = result.getOrNull(),
                error = result.exceptionOrNull()?.message
            )
        }
    }
}
```

---

## 🚀 Швидкий старт для конкретних завдань

### Задача: "Створити новий Use Case"

1. Відкрий `INTERFACE_CONTRACTS.md` → знайди потрібний Repository
2. Відкрий `IMPLEMENTATION_EXAMPLES.md` → розділ 1 → скопіюй шаблон
3. Створи файл у `core/domain/src/main/kotlin/.../usecase/`
4. Заміни назви, додай параметри, реалізуй логіку
5. Додай KDoc коментарі
6. Оновлення `INTERFACE_CONTRACTS.md` (додай UseCase у список)

### Задача: "Реалізувати Repository"

1. Відкрий `INTERFACE_CONTRACTS.md` → знайди Repository interface
2. Відкрий `IMPLEMENTATION_EXAMPLES.md` → розділ 2 → вивчи приклад
3. Створи файл у `core/data/src/main/kotlin/.../repo/`
4. Реалізуй всі методи інтерфейсу
5. Додай Hilt інтеграцію в AppModule
6. Напиши unit тести (optional але recommended)

### Задача: "Створити ViewModel"

1. Відкрий `IMPLEMENTATION_EXAMPLES.md` → розділ 5
2. Скопіюй шаблон DtcViewModel
3. Створи файл у `features/xxx/src/main/kotlin/.../`
4. Визнач UiState, Events, Effects
5. Реалізуй handleEvent()
6. Додай @HiltViewModel annotation
7. Створи Compose screen (розділ 6)

### Задача: "Додати новий OBD протокол"

1. Відкрий `protocols/obd/src/main/kotlin/.../`
2. Створи новий adapter клас (extend ObdInterface)
3. Скопіюй структуру з Elm327Adapter (розділ 4 examples)
4. Реалізуй специфічні команди протоколу
5. Додай парсинг відповідей
6. Оновлення ObdProtocol enum якщо потрібно

---

## 📞 Джерела допомоги

Якщо щось незрозуміло:

1. **Спочатку перевір документацію:**
   - `MODULAR_ARCHITECTURE_GUIDE.md` - концепції
   - `IMPLEMENTATION_EXAMPLES.md` - приклади
   - `INTERFACE_CONTRACTS.md` - контракти

2. **Подивись існуючий код:**
   - `UseCase.kt`, `DtcCode.kt`, `Vehicle.kt` - domain приклади
   - `Port.kt`, `ObdInterface.kt` - interface приклади
   - `ObdProtocol.kt`, `ObdResponse.kt` - protocol приклади

3. **Використовуй AI_AGENT_PROMPTS_LIBRARY.md:**
   - Готові промпти для типових задач
   - Специфічні приклади для різних компонентів

4. **Перевір architecture-visualization.md:**
   - Візуальні схеми модулів
   - Data flow diagrams
   - Структура директорій

---

## 🎯 Ваша мета

Створити **production-ready** Android додаток з:
- ✅ Clean Architecture
- ✅ SOLID principles
- ✅ Testable code
- ✅ Type-safe interfaces
- ✅ Comprehensive documentation
- ✅ Real-world examples

Всі інтерфейси вже визначені. Всі приклади готові. Просто follow the guide!

---

**Автор**: RepoBuilder AI Agent  
**Версія**: 1.0.0  
**Останнє оновлення**: 2024

**Для питань**: Перечитай документацію, там є відповіді на 95% питань! 🚀

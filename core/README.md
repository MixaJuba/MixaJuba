# 🧠 core/ - Core Business Logic | Основна Бізнес-Логіка

## 📋 File Purpose | Призначення
Центральний модуль проекту, що містить чисту бізнес-логіку (domain) та шар даних (data). Незалежний від Android framework.

## 🎯 Role | Роль
Реалізує Clean Architecture principles - domain models, use cases, repositories, database access.

---

## 📂 Структура / Structure

```
core/
├── domain/                    # Business Logic Layer
│   ├── build.gradle.kts
│   └── src/main/kotlin/com/quantumforce_code/core/domain/
│       ├── UseCase.kt         # Base UseCase class
│       ├── Vehicle.kt         # Vehicle business model
│       ├── DtcCode.kt         # DTC business model
│       └── DiagnosticSession.kt  # Session model
│
├── data/                      # Data Layer
│   ├── build.gradle.kts
│   └── src/main/kotlin/com/quantumforce_code/core/data/
│       ├── db/                # Room Database
│       │   ├── AppDatabase.kt
│       │   ├── DtcDao.kt
│       │   ├── VehicleDao.kt
│       │   └── entities/
│       │       ├── DtcEntity.kt
│       │       └── VehicleEntity.kt
│       ├── repo/              # Repositories
│       │   ├── DtcRepository.kt
│       │   ├── VehicleRepository.kt
│       │   └── impl/
│       │       ├── DtcRepositoryImpl.kt
│       │       └── VehicleRepositoryImpl.kt
│       └── DataMappers.kt     # Entity ↔ Domain mappers
│
└── test/
    └── DataUnitTests.kt       # Unit tests
```

---

## 🏛️ Clean Architecture Layers | Шари Чистої Архітектури

```
┌─────────────────────────────────────┐
│   app/ (Presentation)               │  ← UI, ViewModels
├─────────────────────────────────────┤
│   features/ (Feature Modules)       │  ← Feature-specific logic
├─────────────────────────────────────┤
│   core/domain (Business Logic)      │  ← Use Cases, Models
├─────────────────────────────────────┤
│   core/data (Data Access)           │  ← Repositories, DB
├─────────────────────────────────────┤
│   protocols/, hardware/ (External)  │  ← OBD-II, Bluetooth
└─────────────────────────────────────┘
```

**Правила залежностей:**
- ✅ Зовнішні шари залежать від внутрішніх
- ✅ `data` → `domain` (тільки в одному напрямку)
- ❌ `domain` НЕ залежить від `data` (інверсія залежностей)
- ❌ `core/` НЕ залежить від `app/` або `features/`

---

## 💼 core/domain - Business Logic Layer

### Призначення
Чиста бізнес-логіка без залежностей від Android або frameworks. Тільки Kotlin stdlib.

---

### UseCase.kt
**Базовий клас для всіх Use Cases.**

**Що таке Use Case?**
Use Case - це одна бізнес-операція (наприклад, "Read DTC Codes", "Save Vehicle Info"). Інкапсулює логіку і робить код тестабельним.

**Приклад реалізації:**
```kotlin
// 1. File Purpose: Base class for all use cases in the app
// 2. Role: Provides common execute() pattern with coroutines and Result wrapper

abstract class UseCase<in P, out R> {
    suspend operator fun invoke(params: P): Result<R> {
        return try {
            Result.success(execute(params))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    @Throws(RuntimeException::class)
    protected abstract suspend fun execute(params: P): R
}

// Приклад використання
class ReadDtcCodesUseCase(
    private val dtcRepository: DtcRepository
) : UseCase<Unit, List<DtcCode>>() {
    override suspend fun execute(params: Unit): List<DtcCode> {
        return dtcRepository.getAllDtcCodes()
    }
}
```

**Переваги патерну:**
- ✅ Тестабельність (легко мокати репозиторії)
- ✅ Error handling (автоматичний Result wrapper)
- ✅ Reusability (один Use Case = одна операція)

---

### Vehicle.kt
**Business model для автомобіля.**

```kotlin
// 1. File Purpose: Domain model for vehicle information
// 2. Role: Represents a vehicle in the business logic layer

data class Vehicle(
    val id: Long = 0,
    val vin: String,              // VIN (Vehicle Identification Number)
    val make: String,             // Виробник (Toyota, BMW, etc.)
    val model: String,            // Модель (Camry, 3-Series)
    val year: Int,                // Рік випуску
    val licensePlate: String? = null,
    val lastConnected: Long? = null,  // Timestamp останнього підключення
    val protocols: List<String> = emptyList()  // Підтримувані протоколи (ISO 15765, etc.)
)
```

**Чому domain model, а не entity?**
- Domain model - "як ми думаємо про дані" (бізнес-логіка)
- Entity - "як ми зберігаємо дані" (база даних)
- Розділення дозволяє змінювати БД без впливу на бізнес-логіку

---

### DtcCode.kt
**Business model для DTC (Diagnostic Trouble Code).**

```kotlin
// 1. File Purpose: Domain model for Diagnostic Trouble Codes
// 2. Role: Represents a DTC in the business logic layer

data class DtcCode(
    val id: Long = 0,
    val code: String,             // P0301, U0100, etc.
    val description: String,      // "Cylinder 1 Misfire Detected"
    val category: DtcCategory,    // Powertrain, Chassis, Body, Network
    val status: DtcStatus,        // Active, Pending, Stored, Permanent
    val freezeFrameData: String? = null,  // Snapshot даних при помилці
    val timestamp: Long,          // Коли виявлено
    val vehicleId: Long           // FK до Vehicle
)

enum class DtcCategory {
    POWERTRAIN,   // P - двигун, трансмісія
    CHASSIS,      // C - ABS, suspension
    BODY,         // B - airbag, climate
    NETWORK       // U - CAN, communication
}

enum class DtcStatus {
    ACTIVE,       // Помилка активна зараз
    PENDING,      // Виявлено, але ще не підтверджено
    STORED,       // Збережено в історії
    PERMANENT     // Критична помилка (не можна очистити)
}
```

**Приклади DTC:**
- `P0301` - Cylinder 1 Misfire (пропуски запалювання 1-го циліндру)
- `P0171` - System Too Lean (бідна суміш)
- `U0100` - Lost Communication with ECM/PCM (втрата зв'язку з ECU)

---

### DiagnosticSession.kt
**Business model для діагностичної сесії.**

```kotlin
// 1. File Purpose: Domain model for diagnostic session
// 2. Role: Represents a single diagnostic session (scan event)

data class DiagnosticSession(
    val id: Long = 0,
    val vehicleId: Long,
    val startTime: Long,
    val endTime: Long? = null,
    val dtcCount: Int = 0,
    val status: SessionStatus,
    val protocol: String,         // ISO 15765-4 CAN (11 bit, 500 kbit/s)
    val adapterType: String       // ELM327, OBDLink, etc.
)

enum class SessionStatus {
    IN_PROGRESS,
    COMPLETED,
    FAILED,
    CANCELLED
}
```

---

## 💾 core/data - Data Access Layer

### Призначення
Управління даними: Room Database, репозиторії, кешування. Залежить від `core/domain`.

---

### db/ - Room Database

#### AppDatabase.kt
**Main Room Database class.**

```kotlin
// 1. File Purpose: Room database definition for AutoDiagPro
// 2. Role: Centralizes all DAO and database configuration

@Database(
    entities = [
        DtcEntity::class,
        VehicleEntity::class,
        DiagnosticSessionEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dtcDao(): DtcDao
    abstract fun vehicleDao(): VehicleDao
    abstract fun sessionDao(): DiagnosticSessionDao
    
    companion object {
        const val DATABASE_NAME = "autodiagpro.db"
    }
}
```

**Room setup у Hilt Module:**
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()  // Для dev (видаляє БД при зміні схеми)
            .build()
    }
    
    @Provides
    fun provideDtcDao(db: AppDatabase) = db.dtcDao()
}
```

---

#### DtcDao.kt
**Data Access Object для DTC.**

```kotlin
// 1. File Purpose: DAO for DTC CRUD operations
// 2. Role: Defines SQL queries for DTC entity

@Dao
interface DtcDao {
    @Query("SELECT * FROM dtc_codes WHERE status = :status ORDER BY timestamp DESC")
    suspend fun getDtcsByStatus(status: String): List<DtcEntity>
    
    @Query("SELECT * FROM dtc_codes WHERE vehicle_id = :vehicleId")
    suspend fun getDtcsByVehicle(vehicleId: Long): List<DtcEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDtc(dtc: DtcEntity): Long
    
    @Query("DELETE FROM dtc_codes WHERE vehicle_id = :vehicleId AND status != 'PERMANENT'")
    suspend fun clearNonPermanentDtcs(vehicleId: Long)
    
    @Query("SELECT COUNT(*) FROM dtc_codes WHERE status = 'ACTIVE'")
    suspend fun getActiveDtcCount(): Int
}
```

**Приклад Entity:**
```kotlin
@Entity(tableName = "dtc_codes")
data class DtcEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "code") val code: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "status") val status: String,
    @ColumnInfo(name = "freeze_frame_data") val freezeFrameData: String?,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
    @ColumnInfo(name = "vehicle_id") val vehicleId: Long
)
```

---

### repo/ - Repositories

#### DtcRepository.kt
**Interface репозиторію (domain layer).**

```kotlin
// 1. File Purpose: Repository interface for DTC data access
// 2. Role: Abstraction layer between domain and data layers

interface DtcRepository {
    suspend fun getAllDtcCodes(): List<DtcCode>
    suspend fun getDtcsByStatus(status: DtcStatus): List<DtcCode>
    suspend fun getDtcsByVehicle(vehicleId: Long): List<DtcCode>
    suspend fun saveDtc(dtc: DtcCode): Long
    suspend fun clearNonPermanentDtcs(vehicleId: Long)
    suspend fun getActiveDtcCount(): Int
}
```

#### DtcRepositoryImpl.kt
**Реалізація репозиторію (data layer).**

```kotlin
// 1. File Purpose: Implementation of DtcRepository
// 2. Role: Handles data operations (DB access, caching, mapping)

class DtcRepositoryImpl @Inject constructor(
    private val dtcDao: DtcDao,
    private val dataMappers: DataMappers
) : DtcRepository {
    
    override suspend fun getAllDtcCodes(): List<DtcCode> {
        return dtcDao.getDtcsByStatus("ACTIVE")
            .map { dataMappers.toDtcDomain(it) }
    }
    
    override suspend fun saveDtc(dtc: DtcCode): Long {
        val entity = dataMappers.toDtcEntity(dtc)
        return dtcDao.insertDtc(entity)
    }
    
    // ... інші методи
}
```

---

### DataMappers.kt
**Мапери між Entity (БД) та Domain (бізнес-логіка).**

```kotlin
// 1. File Purpose: Mappers between database entities and domain models
// 2. Role: Converts data between layers, keeps layers decoupled

class DataMappers {
    fun toDtcDomain(entity: DtcEntity): DtcCode {
        return DtcCode(
            id = entity.id,
            code = entity.code,
            description = entity.description,
            category = DtcCategory.valueOf(entity.category),
            status = DtcStatus.valueOf(entity.status),
            freezeFrameData = entity.freezeFrameData,
            timestamp = entity.timestamp,
            vehicleId = entity.vehicleId
        )
    }
    
    fun toDtcEntity(domain: DtcCode): DtcEntity {
        return DtcEntity(
            id = domain.id,
            code = domain.code,
            description = domain.description,
            category = domain.category.name,
            status = domain.status.name,
            freezeFrameData = domain.freezeFrameData,
            timestamp = domain.timestamp,
            vehicleId = domain.vehicleId
        )
    }
    
    // ... інші мапери
}
```

**Чому потрібні мапери?**
- Entity має `String` для enum (БД не підтримує enum)
- Domain model може мати додаткові обчислювані властивості
- Розділення відповідальностей (БД не впливає на бізнес-логіку)

---

## 🧪 Testing (test/)

### DataUnitTests.kt
**Unit тести для data layer.**

```kotlin
class DtcRepositoryTest {
    private lateinit var repository: DtcRepository
    private lateinit var dtcDao: DtcDao  // Mock
    
    @Before
    fun setup() {
        dtcDao = mock()
        repository = DtcRepositoryImpl(dtcDao, DataMappers())
    }
    
    @Test
    fun `getAllDtcCodes returns mapped domain models`() = runBlocking {
        // Given
        val entities = listOf(
            DtcEntity(code = "P0301", description = "Misfire", ...)
        )
        `when`(dtcDao.getDtcsByStatus("ACTIVE")).thenReturn(entities)
        
        // When
        val result = repository.getAllDtcCodes()
        
        // Then
        assertEquals(1, result.size)
        assertEquals("P0301", result[0].code)
    }
}
```

---

## 🔧 build.gradle.kts

### core/domain/build.gradle.kts
```kotlin
plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}

dependencies {
    // Тільки Kotlin stdlib - NO Android dependencies
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
}
```

### core/data/build.gradle.kts
```kotlin
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")  // Для Room
}

dependencies {
    // Domain layer
    implementation(project(":core:domain"))
    
    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    
    // Hilt
    implementation("com.google.dagger:hilt-android:2.50")
    ksp("com.google.dagger:hilt-compiler:2.50")
    
    // Testing
    testImplementation("androidx.room:room-testing:2.6.1")
}
```

---

## 🎯 Ключові Принципи / Key Principles

### 1. Dependency Inversion
```
app → features → core/data → core/domain
                            ↑
                     (залежить від interfaces)
```

### 2. Offline-First
Всі дані спочатку зберігаються локально (Room), потім можна синхронізувати.

### 3. Result Wrapper
```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
```

### 4. Immutability
Всі domain models - `data class` (immutable). Зміни через `copy()`.

---

## 🔗 Ресурси / Resources
- [Clean Architecture by Uncle Bob](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Room Persistence Library](https://developer.android.com/training/data-storage/room)
- [Repository Pattern](https://developer.android.com/codelabs/basic-android-kotlin-training-repository-pattern)

---

**Maintained by:** Data Agent / RepoBuilder 🤖  
**Tech Stack:** Kotlin + Room + Coroutines + Hilt  
**Last Updated:** 2025 🚦⚡  
**Architecture:** Clean Architecture + Repository Pattern 🏛️

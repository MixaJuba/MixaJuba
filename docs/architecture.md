# 🏗️ Огляд архітектури | Architecture Overview

**Високорівневий огляд архітектури QuantumForce_Code**

*High-level overview of QuantumForce_Code architecture*

---

## 🎯 Швидкий огляд | Quick Overview

QuantumForce_Code - це **професійний автомобільний діагностичний інструмент** для Android, побудований на принципах **Clean Architecture** з модульною структурою. Проєкт розроблений для масштабованості, тестованості та підтримки AI-driven розробки.

*QuantumForce_Code is a **professional automotive diagnostic tool** for Android, built on **Clean Architecture** principles with a modular structure. The project is designed for scalability, testability, and AI-driven development support.*

---

## 📊 Архітектурні шари | Architecture Layers

```
┌──────────────────────────────────────────────────────────────┐
│                      📱 PRESENTATION                          │
│                    UI + ViewModels                            │
│     • features/dtc (Коди помилок / DTCs)                     │
│     • features/live (Живі дані / Live Data)                  │
│     • app (Головний модуль / Main module)                    │
├──────────────────────────────────────────────────────────────┤
│                      💼 DOMAIN (Business Logic)               │
│               UseCases + Entities + Interfaces                │
│     • core/domain (Бізнес-правила / Business rules)          │
│     • Repository interfaces                                   │
│     • Model entities                                          │
├──────────────────────────────────────────────────────────────┤
│                      💾 DATA (Repositories)                   │
│          Repository Impl + DataSources + Mappers              │
│     • core/data (Реалізація сховищ / Storage impl)           │
│     • Room database (SQLite)                                  │
│     • Data mappers (Entity ↔ Model)                          │
├──────────────────────────────────────────────────────────────┤
│                   🔧 INFRASTRUCTURE (Frameworks)              │
│           Hardware + Protocols + Security                     │
│     • hardware/transport (OBD адаптери / OBD adapters)       │
│     • protocols/obd (Протоколи / Protocols)                  │
│     • security (Безпека / Security)                           │
│     • updates (Оновлення / Updates)                           │
└──────────────────────────────────────────────────────────────┘
```

---

## 🧩 Основні модулі | Core Modules

### 1. **app** - Головний модуль | Main Module
**Відповідальність:** Точка входу, DI setup (Hilt), навігація

**Ключові файли:**
- `MainActivity.kt` - точка входу
- `Navigation.kt` - навігаційний граф
- `Application.kt` - Hilt setup

---

### 2. **core/domain** - Бізнес-логіка | Business Logic
**Відповідальність:** Незалежна бізнес-логіка, інтерфейси контрактів

**Що містить:**
- ✅ UseCase інтерфейси та реалізації
- ✅ Domain entities (Dtc, Vehicle, LiveData)
- ✅ Repository interfaces
- ❌ НЕ залежить від Android
- ❌ НЕ знає про UI або базу даних

**Приклад:**
```kotlin
// Domain layer - pure Kotlin, no Android
interface DtcRepository {
    suspend fun getDtcs(vin: String): Result<List<Dtc>>
    suspend fun clearDtcs(vin: String): Result<Boolean>
}

class GetVehicleDtcsUseCase(
    private val repository: DtcRepository
) {
    suspend operator fun invoke(vin: String) = repository.getDtcs(vin)
}
```

---

### 3. **core/data** - Дані | Data Layer
**Відповідальність:** Реалізація repositories, робота з базою даних

**Що містить:**
- ✅ Repository implementations
- ✅ Room database (DAOs, Entities)
- ✅ Data mappers (DB Entity → Domain Model)
- ✅ Data sources (local/remote)

**Приклад:**
```kotlin
// Data layer implementation
class DtcRepositoryImpl(
    private val dao: DtcDao,
    private val mapper: DtcMapper
) : DtcRepository {
    override suspend fun getDtcs(vin: String): Result<List<Dtc>> {
        return try {
            val entities = dao.getDtcsByVin(vin)
            val models = entities.map { mapper.toDomain(it) }
            Result.success(models)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

---

### 4. **hardware/transport** - Транспортний шар | Transport Layer
**Відповідальність:** Комунікація з OBD адаптерами (Bluetooth/USB/TCP)

**Що містить:**
- ✅ Port interface (абстракція над hardware)
- ✅ BluetoothPort, UsbSerialPort, TcpPort implementations
- ✅ ConnectionManager для управління з'єднаннями

**Приклад:**
```kotlin
interface Port {
    suspend fun connect(address: String): Result<Unit>
    suspend fun send(data: ByteArray): Result<Unit>
    suspend fun receive(): Result<ByteArray>
    suspend fun disconnect()
}
```

---

### 5. **protocols/obd** - Протоколи діагностики | Diagnostic Protocols
**Відповідальність:** Реалізація OBD-II, CAN, UDS протоколів

**Що містить:**
- ✅ Protocol adapters (ELM327, KWP2000, UDS)
- ✅ Command builders
- ✅ Response parsers (DTC, PID)
- ✅ Protocol-specific logic

**Приклад:**
```kotlin
interface ObdProtocol {
    suspend fun readDtcs(): Result<List<Dtc>>
    suspend fun clearDtcs(): Result<Boolean>
    suspend fun readPid(pid: String): Result<PidData>
}

class Elm327Adapter(
    private val port: Port
) : ObdProtocol {
    override suspend fun readDtcs(): Result<List<Dtc>> {
        // Send "03" command, parse response
    }
}
```

---

### 6. **features/dtc** - Feature: Коди помилок | Feature: DTCs
**Відповідальність:** UI та логіка для роботи з кодами помилок

**Що містить:**
- ✅ DtcViewModel (MVI pattern)
- ✅ DtcScreen (Jetpack Compose)
- ✅ DtcState (UI states)
- ✅ DtcEvent (UI events)

---

### 7. **features/live** - Feature: Живі дані | Feature: Live Data
**Відповідальність:** Відображення real-time даних з автомобіля

**Що містить:**
- ✅ LiveDataViewModel
- ✅ LiveDataScreen (Compose)
- ✅ Charts та gauges для візуалізації

---

### 8. **security** - Безпека | Security
**Відповідальність:** Шифрування, secure storage, authentication

**Що містить:**
- ✅ Keystore management
- ✅ Encrypted preferences
- ✅ Security utilities

---

### 9. **updates** - Система оновлень | Update System
**Відповідальність:** Завантаження та встановлення оновлень

**Що містить:**
- ✅ Update checker
- ✅ Download manager
- ✅ Version control

---

## 🔄 Потік даних | Data Flow

### Приклад: Зчитування кодів помилок | Example: Reading DTCs

```
User clicks "Scan" button
        ↓
   [UI Layer]
DtcScreen → DtcViewModel
        ↓
  [Domain Layer]
ViewModel → GetVehicleDtcsUseCase
        ↓
UseCase → DtcRepository (interface)
        ↓
   [Data Layer]
DtcRepositoryImpl → DtcDao (Room)
        ↑
   [або | or]
        ↓
[Infrastructure Layer]
ProtocolAdapter → Transport Port → OBD Hardware
        ↓
   [Response flow]
Hardware → Port → Protocol → Repository → UseCase → ViewModel → UI
```

---

## 🎨 Патерни проєктування | Design Patterns

### 1. **Repository Pattern**
Абстракція доступу до даних, приховує деталі реалізації.

### 2. **UseCase Pattern**
Інкапсулює бізнес-операції, one use case = one business operation.

### 3. **MVI (Model-View-Intent)**
Односпрямований потік даних для ViewModel + Compose UI.

### 4. **Dependency Injection (Hilt)**
Автоматичне надання залежностей, полегшує тестування.

### 5. **Strategy Pattern**
Різні Protocol адаптери (ELM327, KWP2000, UDS).

### 6. **Adapter Pattern**
Transport Port - єдиний інтерфейс для різних типів з'єднань.

---

## 🔗 Залежності між модулями | Module Dependencies

```
app
 ├─ features/dtc
 ├─ features/live
 ├─ core/domain
 └─ core/data

features/dtc
 └─ core/domain

features/live
 └─ core/domain

core/data
 ├─ core/domain
 ├─ hardware/transport
 └─ protocols/obd

protocols/obd
 └─ hardware/transport

hardware/transport
 └─ (no dependencies on other modules)
```

**Правило:** Залежності спрямовані тільки **вниз** або **вглиб**, ніколи не вгору.

---

## 🧪 Тестовність | Testability

### Unit Tests (Domain Layer)
```kotlin
@Test
fun `UseCase returns DTCs from repository`() = runTest {
    // Arrange
    val fakeRepo = FakeDtcRepository()
    val useCase = GetVehicleDtcsUseCase(fakeRepo)
    
    // Act
    val result = useCase("VIN123")
    
    // Assert
    assertThat(result.isSuccess).isTrue()
}
```

### Integration Tests (Data + Domain)
```kotlin
@Test
fun `Repository fetches from database correctly`() = runTest {
    val db = createInMemoryDatabase()
    val repo = DtcRepositoryImpl(db.dtcDao(), DtcMapper())
    
    val result = repo.getDtcs("VIN123")
    
    assertThat(result.isSuccess).isTrue()
}
```

---

## 📚 Детальна документація | Detailed Documentation

Це високорівневий огляд. Для детальної інформації:

### Обов'язково прочитати | Must Read:
1. **[Modular Architecture Guide](MODULAR_ARCHITECTURE_GUIDE.md)** ⭐
   - Повний опис всіх принципів Clean Architecture
   - Детальні інтерфейси з коментарями
   - Діаграми взаємодії
   - 1700+ рядків документації

2. **[Interface Contracts](INTERFACE_CONTRACTS.md)**
   - 23 інтерфейси з повними сигнатурами
   - 120+ методів
   - Матриця залежностей

3. **[Implementation Examples](IMPLEMENTATION_EXAMPLES.md)**
   - Real-world приклади для кожного шару
   - Domain, Data, Transport, Protocol, Features, UI
   - Hilt DI setup

### Для візуального розуміння | For Visual Understanding:
4. **[Architecture Visualization](architecture-visualization.md)**
   - Mermaid діаграми
   - Структура директорій
   - Data flow diagrams

### Для розробки | For Development:
5. **[AI Agent Implementation Guide](AI_AGENT_IMPLEMENTATION_GUIDE.md)**
   - Покрокові інструкції для реалізації
   - Чек-лісти перед commit
   - Статуси файлів

---

## 🎯 Ключові архітектурні рішення | Key Architectural Decisions

### ✅ Чому Clean Architecture?
- Незалежність від фреймворків
- Висока тестовність
- Гнучкість до змін
- Підходить для великих проєктів

### ✅ Чому модульна структура?
- Паралельна розробка
- Швидша компіляція (тільки змінені модулі)
- Перевикористання модулів
- Чіткі межі відповідальності

### ✅ Чому Jetpack Compose?
- Сучасний декларативний UI
- Менше boilerplate коду
- Краща інтеграція з Kotlin
- Офіційна рекомендація Google

### ✅ Чому Hilt?
- Офіційний DI для Android
- Інтеграція з Jetpack
- Compile-time safety
- Простіший за Dagger

---

## 🔍 Architectural Decision Records (ADR)

Всі важливі архітектурні рішення документуються в ADR:

- [ADR-001: Initial Architecture](adr/adr-001-initial-architecture.md) - Вибір Clean Architecture + модулів
- [ADR-002: UI Framework](adr/adr-002-ui-framework.md) - (Coming) Чому Jetpack Compose
- [ADR-003: DI Solution](adr/adr-003-di-solution.md) - (Coming) Чому Hilt

---

## 🚀 Швидкий старт | Quick Start

### Якщо ви новачок | If you're a beginner:
1. Прочитайте [Glossary](glossary.md) - зрозумійте терміни
2. Подивіться [Architecture Visualization](architecture-visualization.md) - побачте картинку
3. Читайте [Modular Architecture Guide](MODULAR_ARCHITECTURE_GUIDE.md) розділи 1-3

### Якщо ви розробник | If you're a developer:
1. [Modular Architecture Guide](MODULAR_ARCHITECTURE_GUIDE.md) - зрозумійте принципи
2. [Interface Contracts](INTERFACE_CONTRACTS.md) - що реалізувати
3. [Implementation Examples](IMPLEMENTATION_EXAMPLES.md) - як реалізувати

### Якщо ви AI-агент | If you're an AI agent:
1. [AI Agent Implementation Guide](AI_AGENT_IMPLEMENTATION_GUIDE.md)
2. [Interface Contracts](INTERFACE_CONTRACTS.md)
3. [Implementation Examples](IMPLEMENTATION_EXAMPLES.md)

---

## 💡 Ключові концепції | Key Concepts

### Dependency Rule
```
Зовнішні шари → Внутрішні шари
External layers → Internal layers

Infrastructure → Data → Domain
UI → Domain
```
**Domain не залежить від нічого. Domain is independent.**

### Interface Segregation
```kotlin
// ✅ Good - маленькі, специфічні інтерфейси
interface DtcReader {
    suspend fun readDtcs(): Result<List<Dtc>>
}

interface DtcClearer {
    suspend fun clearDtcs(): Result<Boolean>
}

// ❌ Bad - монолітний інтерфейс
interface DtcEverything {
    suspend fun readDtcs(): Result<List<Dtc>>
    suspend fun clearDtcs(): Result<Boolean>
    suspend fun analyzeDtcs(): Result<Analysis>
    suspend fun exportDtcs(): Result<File>
    // ... 20 more methods
}
```

### Single Responsibility
```kotlin
// ✅ Good - одна відповідальність
class GetVehicleDtcsUseCase(
    private val repository: DtcRepository
) {
    suspend operator fun invoke(vin: String) = 
        repository.getDtcs(vin)
}

// ❌ Bad - кілька відповідальностей
class VehicleManager {
    fun getDtcs() { }
    fun saveDtcs() { }
    fun exportDtcs() { }
    fun analyzeDtcs() { }
    fun connectToVehicle() { }
    fun sendCommand() { }
}
```

---

## 📊 Метрики архітектури | Architecture Metrics

**Цільові показники:**

- 📦 **Модулів**: 8-10 (поточно: 9)
- 🔗 **Середня глибина залежностей**: 2-3 рівні
- 🎯 **Cyclomatic Complexity**: < 10 для критичних методів
- 📏 **Lines of Code per module**: < 5000
- 🧪 **Test Coverage**: 70-80%+ для Domain
- ⚡ **Build time**: < 2 хвилини (clean build)
- 🔄 **Incremental build**: < 30 секунд

---

## 🔗 Пов'язані ресурси | Related Resources

### Внутрішні:
- [Complete Automotive Diagnostic Software Guide](guides/automotive-diagnostic-software-guide.md)
- [Quick Start Guide](guides/quick-start-ua.md)
- [Testing Guidelines](testing-guidelines.md)
- [Contributing Guidelines](CONTRIBUTING.md)

### Зовнішні:
- [Clean Architecture by Uncle Bob](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Android App Architecture Guide](https://developer.android.com/topic/architecture)
- [Modularization Guide](https://developer.android.com/topic/modularization)

---

## 📞 Питання? | Questions?

**Архітектурні питання:**
→ Читай детальніше в [Modular Architecture Guide](MODULAR_ARCHITECTURE_GUIDE.md)

**Не розумієш терміни:**
→ [Glossary](glossary.md)

**Не знаєш як реалізувати:**
→ [Implementation Examples](IMPLEMENTATION_EXAMPLES.md)

**Питання про тестування:**
→ [Testing Guidelines](testing-guidelines.md)

---

**Версія:** 2.0.0  
**Дата:** 2024  
**Автор:** RepoBuilder AI Agent

**Повернутися до:** [Головна документація](index.md)

---

> 💡 **Tip:** Це огляд для швидкого розуміння. Для детальної інформації про кожен шар та модуль, обов'язково прочитайте [Modular Architecture Guide](MODULAR_ARCHITECTURE_GUIDE.md) - там 1700+ рядків детальної документації з прикладами коду!

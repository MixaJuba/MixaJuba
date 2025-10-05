# 📋 Контракти Інтерфейсів Модульної Архітектури | Interface Contracts Summary

## 📍 Навігація

Цей документ містить повний список всіх інтерфейсів проєкту з їх призначенням, методами та взаємозв'язками.

---

## 🎯 Domain Layer Interfaces

### UseCase<P, R>
**Файл**: `core/domain/src/main/kotlin/.../UseCase.kt`  
**Призначення**: Базовий клас для всіх бізнес-операцій  
**Принципи**: Single Responsibility, Command Pattern

#### Методи:
```kotlin
abstract suspend fun execute(parameters: P): Result<R>
suspend operator fun invoke(parameters: P): Result<R>
```

---

### DtcRepository
**Файл**: `core/domain/src/main/kotlin/.../repository/DtcRepository.kt`  
**Призначення**: Контракт для доступу до DTC кодів  
**Принципи**: Repository Pattern, Dependency Inversion

#### Методи:
```kotlin
suspend fun getDtcCodes(vehicleId: String, includeCleared: Boolean = false): List<DtcCode>
suspend fun getDtcByCode(code: String): DtcCode?
suspend fun saveDtcCode(dtcCode: DtcCode): Result<Unit>
suspend fun saveDtcCodes(dtcCodes: List<DtcCode>): Result<Int>
suspend fun clearDtcCodes(vehicleId: String): Result<Unit>
suspend fun markDtcAsCleared(code: String): Result<Unit>
suspend fun searchDtcCodes(query: String): List<DtcCode>
suspend fun getActiveDtcCount(vehicleId: String): Int
suspend fun getCriticalDtcCodes(vehicleId: String): List<DtcCode>
```

**Реалізації**: `DtcRepositoryImpl` (в core/data)  
**Використовується**: GetDtcCodesUseCase, ClearDtcCodesUseCase

---

### VehicleRepository
**Файл**: `core/domain/src/main/kotlin/.../repository/VehicleRepository.kt`  
**Призначення**: Контракт для управління автомобілями  
**Принципи**: CRUD Operations, Repository Pattern

#### Методи:
```kotlin
suspend fun getAllVehicles(): List<Vehicle>
suspend fun getVehicleById(id: String): Vehicle?
suspend fun getVehicleByVin(vin: String): Vehicle?
suspend fun saveVehicle(vehicle: Vehicle): Result<String>
suspend fun updateVehicle(vehicle: Vehicle): Result<Unit>
suspend fun deleteVehicle(id: String): Result<Unit>
suspend fun searchVehicles(query: String): List<Vehicle>
suspend fun getLastUsedVehicle(): Vehicle?
suspend fun setLastUsedVehicle(id: String): Result<Unit>
```

**Реалізації**: `VehicleRepositoryImpl` (в core/data)  
**Використовується**: GetVehicleUseCase, SaveVehicleUseCase

---

### DiagnosticSessionRepository
**Файл**: `core/domain/src/main/kotlin/.../repository/DiagnosticSessionRepository.kt`  
**Призначення**: Контракт для управління сесіями діагностики  
**Принципи**: Session Management, Repository Pattern

#### Методи:
```kotlin
suspend fun createSession(vehicleId: String): DiagnosticSession
suspend fun updateSession(session: DiagnosticSession): Result<Unit>
suspend fun completeSession(sessionId: String, dtcCodes: List<DtcCode>): DiagnosticSession
suspend fun cancelSession(sessionId: String): DiagnosticSession
suspend fun getActiveSession(): DiagnosticSession?
suspend fun getSessionById(sessionId: String): DiagnosticSession?
suspend fun getSessionHistory(vehicleId: String, limit: Int = 50): List<DiagnosticSession>
suspend fun getLastCompletedSession(vehicleId: String): DiagnosticSession?
suspend fun deleteSession(sessionId: String): Result<Unit>
suspend fun deleteAllSessions(vehicleId: String): Result<Unit>
suspend fun getSessionStatistics(vehicleId: String): SessionStatistics
```

**Вкладені типи**: `SessionStatistics`  
**Реалізації**: `DiagnosticSessionRepositoryImpl` (в core/data)

---

## 🔌 Transport Layer Interfaces

### Port
**Файл**: `hardware/transport/src/main/kotlin/.../Port.kt`  
**Призначення**: Базовий інтерфейс для транспортних каналів  
**Принципи**: Interface Segregation, Dependency Inversion

#### Методи:
```kotlin
suspend fun open(): Result<Boolean>
suspend fun close()
suspend fun write(data: ByteArray): Result<Boolean>
suspend fun read(timeout: Long = 1000): Result<ByteArray>
suspend fun flush(): Result<Unit>
val isConnected: Boolean
val info: PortInfo
```

**Вкладені типи**:
- `PortInfo(type, address, name, capabilities)`
- `PortType` enum: BLUETOOTH, USB_SERIAL, TCP, MOCK
- `Capability` enum: HIGH_SPEED, BIDIRECTIONAL, FLOW_CONTROL, AUTO_RECONNECT

**Реалізації**:
- `BluetoothPort` - Bluetooth SPP комунікація
- `UsbSerialPort` - USB з CH340/FTDI/CP2102
- `TcpPort` - TCP/IP (WiFi адаптери)
- `MockPort` - Для тестування

**Використовується**: ConnectionManager, ObdInterface

---

### ConnectionManager
**Файл**: `hardware/transport/src/main/kotlin/.../ConnectionManager.kt`  
**Призначення**: Управління життєвим циклом з'єднань  
**Принципи**: Facade Pattern, Connection Pooling

#### Методи:
```kotlin
val connectionState: StateFlow<ConnectionState>
suspend fun connect(config: ConnectionConfig): Result<Port>
suspend fun disconnect()
fun getCurrentPort(): Port?
fun getCurrentConfig(): ConnectionConfig?
suspend fun isDeviceAvailable(config: ConnectionConfig): Boolean
suspend fun scanDevices(): List<DeviceInfo>
suspend fun scanDevices(type: Port.PortType): List<DeviceInfo>
fun setAutoReconnect(enabled: Boolean, maxAttempts: Int = 3, retryDelay: Long = 2000)
suspend fun checkConnection(): Result<Boolean>
```

**Вкладені типи**:
- `ConnectionConfig(type, address, baudRate, timeout, name)`
- `DeviceInfo(name, address, type, isAvailable, signalStrength, isPaired)`
- `ConnectionState` sealed class: Disconnected, Connecting, Connected, Error, Reconnecting

**Реалізації**: `ConnectionManagerImpl`  
**Використовується**: DtcViewModel, LiveDataViewModel, UI екрани

---

## 🔧 Protocol Layer Interfaces

### ObdInterface
**Файл**: `protocols/obd/src/main/kotlin/.../ObdInterface.kt`  
**Призначення**: Контракт для OBD-II діагностики  
**Принципи**: Command Pattern, Strategy Pattern

#### Методи:
```kotlin
suspend fun initialize(): Result<Boolean>
suspend fun sendCommand(command: ObdCommand): Result<ObdResponse>
suspend fun readDtcCodes(): Result<List<String>>
suspend fun readPendingDtcCodes(): Result<List<String>>
suspend fun clearDtcCodes(): Result<Boolean>
suspend fun readPid(pid: String): Result<PidData>
suspend fun readMultiplePids(pids: List<String>): Result<Map<String, PidData>>
suspend fun detectProtocol(): Result<ObdProtocol>
suspend fun setProtocol(protocol: ObdProtocol): Result<Unit>
fun isConnected(): Boolean
suspend fun getAdapterInfo(): Result<AdapterInfo>
suspend fun readVin(): Result<String>
suspend fun readFreezeFrame(dtcCode: String): Result<FreezeFrameData>
suspend fun close()
```

**Вкладені типи**:
- `PidData(pid, value, unit, name, timestamp)`
- `AdapterInfo(type, version, protocols, currentProtocol)`
- `FreezeFrameData(dtcCode, engineSpeed, vehicleSpeed, coolantTemp, loadValue, fuelPressure, throttlePosition)`

**Реалізації**:
- `Elm327Adapter` - ELM327 адаптери (найпоширеніші)
- `KWP2000Adapter` - VAG протокол
- `UDSAdapter` - Unified Diagnostic Services

**Використовується**: DtcRepository, LiveDataRepository

---

## 📊 Domain Models

### DtcCode
**Файл**: `core/domain/src/main/kotlin/.../DtcCode.kt`  
**Тип**: Data Class (Rich Domain Model)

#### Властивості:
```kotlin
val code: String
val description: String
val severity: Severity
val causes: List<String>
val solutions: List<String>
val affectedSystems: List<String>
val timestamp: Long?
val freezeFrame: FreezeFrame?
```

#### Методи:
```kotlin
fun requiresImmediateAttention(): Boolean
fun formatForDisplay(): String
fun isActive(): Boolean
fun getSystemType(): SystemType
```

**Вкладені типи**:
- `Severity` enum: LOW, MEDIUM, HIGH, CRITICAL
- `SystemType` enum: POWERTRAIN, CHASSIS, BODY, NETWORK, UNKNOWN
- `FreezeFrame` data class

---

### Vehicle
**Файл**: `core/domain/src/main/kotlin/.../Vehicle.kt`  
**Тип**: Data Class (Value Object)

#### Властивості:
```kotlin
val id: String
val make: String
val model: String
val year: Int
val vin: String
val protocol: Protocol
val ecuAddresses: List<String>
val engineType: EngineType
val transmissionType: TransmissionType
```

#### Методи:
```kotlin
fun supportsAdvancedDiagnostics(): Boolean
fun hasValidVin(): Boolean
fun getDisplayName(): String
fun isModernVehicle(): Boolean
fun requiresSpecializedAdapter(): Boolean
```

**Вкладені типи**:
- `Protocol` enum: 10 протоколів (ISO, CAN, KWP, UDS, manufacturer-specific)
- `EngineType` enum: GASOLINE, DIESEL, HYBRID, ELECTRIC, NATURAL_GAS, LPG
- `TransmissionType` enum: MANUAL, AUTOMATIC, CVT, DCT

---

### DiagnosticSession
**Файл**: `core/domain/src/main/kotlin/.../DiagnosticSession.kt`  
**Тип**: Data Class (Aggregate Root)

#### Властивості:
```kotlin
val id: String
val vehicleId: String
val startTime: Long
val endTime: Long?
val status: SessionStatus
val dtcCodes: List<DtcCode>
val liveData: Map<String, Any>
val notes: String
val connectionType: ConnectionType
```

#### Методи:
```kotlin
fun complete(dtcCodes: List<DtcCode>): DiagnosticSession
fun fail(reason: String): DiagnosticSession
fun cancel(): DiagnosticSession
fun addLiveData(pid: String, value: Any): DiagnosticSession
fun addNote(note: String): DiagnosticSession
fun isActive(): Boolean
fun isCompleted(): Boolean
fun getDuration(): Long?
fun getFormattedDuration(): String
fun hasCriticalErrors(): Boolean
fun countDtcCodesBySeverity(severity: DtcCode.Severity): Int
```

**Вкладені типи**:
- `ConnectionType` enum: BLUETOOTH, WIFI, USB, UNKNOWN

**Зовнішні типи**:
- `SessionStatus` enum: ACTIVE, COMPLETED, FAILED, CANCELLED

---

## 🎨 Presentation Layer Interfaces

### BaseViewModel<S, E>
**Призначення**: Базовий контракт для ViewModels  
**Принципи**: MVI Pattern, Unidirectional Data Flow

#### Методи:
```kotlin
val uiState: StateFlow<S>
val effects: SharedFlow<UiEffect>
fun handleEvent(event: E)
```

**Маркерні інтерфейси**:
- `UiState` - для класів стану UI
- `UiEvent` - для подій користувача
- `UiEffect` - для одноразових ефектів (навігація, toast)

---

## 📐 Protocol Support Types

### ObdProtocol
**Файл**: `protocols/obd/src/main/kotlin/.../ObdProtocol.kt`  
**Тип**: Enum Class

#### Значення:
- `J1850_PWM` - SAE J1850 PWM (Ford старі)
- `J1850_VPW` - SAE J1850 VPW (GM, Chrysler старі)
- `ISO_9141_2` - ISO 9141-2 (азіатські старі)
- `ISO_14230_4_KWP_5BAUD` - ISO 14230-4 KWP
- `ISO_14230_4_KWP_FAST` - ISO 14230-4 KWP fast init
- `ISO_15765_4_CAN_11BIT_500K` - **НАЙПОШИРЕНІШИЙ** (сучасні авто)
- `ISO_15765_4_CAN_29BIT_500K` - CAN 29-bit
- `ISO_15765_4_CAN_11BIT_250K` - CAN 11-bit 250k
- `ISO_15765_4_CAN_29BIT_250K` - CAN 29-bit 250k
- `J1939_CAN` - SAE J1939 (вантажівки)
- `AUTO` - автоматичне визначення

#### Методи:
```kotlin
fun isStandardObd2(): Boolean
fun supportsExtendedAddressing(): Boolean
```

---

### ObdCommand
**Файл**: `protocols/obd/src/main/kotlin/.../ObdCommand.kt`  
**Тип**: Sealed Class

#### Ієрархія:
- `ObdCommand.AT` - команди адаптера (Reset, Echo, SetProtocol)
- `ObdCommand.Mode01` - поточні дані (EngineRPM, VehicleSpeed, ThrottlePosition)
- `ObdCommand.ReadDTC` - Mode 03
- `ObdCommand.ClearDTC` - Mode 04
- `ObdCommand.Mode09` - інформація про авто (VIN, CalibrationID)
- `ObdCommand.Custom` - довільна команда

---

### ObdResponse
**Файл**: `protocols/obd/src/main/kotlin/.../ObdResponse.kt`  
**Тип**: Data Class

#### Властивості:
```kotlin
val rawData: String
val status: Status
val parsedValue: Any?
val unit: String?
val timestamp: Long
val command: ObdCommand?
```

#### Методи:
```kotlin
fun isSuccess(): Boolean
fun isError(): Boolean
fun asInt(): Int?
fun asFloat(): Float?
fun asString(): String?
fun asBoolean(): Boolean?
fun formatForDisplay(): String
fun getErrorMessage(): String?
fun isRetryable(): Boolean
```

**Вкладені типи**:
- `Status` enum: SUCCESS, ERROR, NO_DATA, TIMEOUT, UNSUPPORTED, INVALID_FORMAT, CAN_ERROR, BUS_BUSY, BUFFER_FULL, STOPPED

---

## 🔗 Залежності між шарами

```
┌─────────────────────────────────────────┐
│           UI Layer (app)                │
│    DtcScreen, LiveScreen, NavGraph      │
└──────────────┬──────────────────────────┘
               │ uses
               ▼
┌─────────────────────────────────────────┐
│      Features Layer (features)          │
│  DtcViewModel, LiveDataViewModel        │
└──────────────┬──────────────────────────┘
               │ uses
               ▼
┌─────────────────────────────────────────┐
│    Domain Layer (core/domain)           │
│   UseCases, Entities, Repository IF     │
└──────────────┬──────────────────────────┘
               │ implemented by
               ▼
┌─────────────────────────────────────────┐
│     Data Layer (core/data)              │
│  Repository Impl, DAOs, Mappers         │
└──────────────┬──────────────────────────┘
               │ uses
               ▼
┌─────────────────────────────────────────┐
│   Infrastructure Layer                  │
│  Transport, Protocols, Security         │
└─────────────────────────────────────────┘
```

---

## 📊 Матриця відповідальностей

| Шар | Відповідальність | Залежить від | Використовується |
|-----|------------------|--------------|------------------|
| **UI** | Відображення, user input | Features | - |
| **Features** | UI логіка, states | Domain, Transport | UI |
| **Domain** | Бізнес-правила | - | Features, Data |
| **Data** | Persistence, sync | Domain, Infrastructure | Domain |
| **Transport** | Hardware комунікація | - | Data, Protocols |
| **Protocols** | OBD-II стандарти | Transport | Data |
| **Security** | Безпека, шифрування | - | Всі шари |
| **Updates** | Система оновлень | - | Data |

---

## ✅ Чек-лист реалізації інтерфейсів

### Domain Layer
- [x] UseCase базовий клас
- [x] DtcRepository interface
- [x] VehicleRepository interface
- [x] DiagnosticSessionRepository interface
- [x] DtcCode модель з методами
- [x] Vehicle модель з протоколами
- [x] DiagnosticSession модель з life-cycle

### Data Layer
- [ ] DtcRepositoryImpl
- [ ] VehicleRepositoryImpl
- [ ] DiagnosticSessionRepositoryImpl
- [ ] Room Database entities
- [ ] DAOs
- [ ] DataMappers

### Transport Layer
- [x] Port interface повний
- [x] ConnectionManager interface повний
- [ ] BluetoothPort реалізація
- [ ] UsbSerialPort реалізація
- [ ] TcpPort реалізація
- [ ] ConnectionManagerImpl

### Protocol Layer
- [x] ObdInterface повний
- [x] ObdProtocol enum
- [x] ObdCommand sealed class
- [x] ObdResponse модель
- [ ] Elm327Adapter реалізація
- [ ] PidParser
- [ ] DtcParser

### Features Layer
- [ ] BaseViewModel interface
- [ ] DtcViewModel
- [ ] DtcUiState
- [ ] DtcEvent sealed class
- [ ] DtcEffect sealed class
- [ ] LiveDataViewModel
- [ ] LiveUiState

### UI Layer
- [ ] DtcScreen Compose
- [ ] LiveScreen Compose
- [ ] VehicleSelectionScreen
- [ ] SettingsScreen
- [ ] NavGraph

---

**Автор**: RepoBuilder AI Agent  
**Версія**: 1.0.0  
**Останнє оновлення**: 2024

Цей документ служить єдиним джерелом істини для всіх контрактів інтерфейсів у проєкті QuantumForce_Code.

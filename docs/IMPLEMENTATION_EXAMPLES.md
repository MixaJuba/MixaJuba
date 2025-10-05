# 📝 Приклади Реалізації Модульної Архітектури | Implementation Examples

## 📋 Зміст

1. [Domain Layer Examples](#domain-layer-examples)
2. [Data Layer Examples](#data-layer-examples)
3. [Transport Layer Examples](#transport-layer-examples)
4. [Protocol Layer Examples](#protocol-layer-examples)
5. [Features Layer Examples](#features-layer-examples)
6. [UI Layer Examples](#ui-layer-examples)
7. [Integration Examples](#integration-examples)

---

## 1️⃣ Domain Layer Examples

### UseCase Implementation Example

```kotlin
// GetDtcCodesUseCase.kt
package com.quantumforce_code.core.domain.usecase

import com.quantumforce_code.core.domain.DtcCode
import com.quantumforce_code.core.domain.UseCase
import com.quantumforce_code.core.domain.repository.DtcRepository

/**
 * Use Case для отримання DTC кодів з фільтрацією та сортуванням.
 */
class GetDtcCodesUseCase(
    private val dtcRepository: DtcRepository
) : UseCase<GetDtcCodesUseCase.Params, List<DtcCode>>() {
    
    data class Params(
        val vehicleId: String,
        val includeCleared: Boolean = false,
        val filterBySeverity: DtcCode.Severity? = null
    )
    
    override suspend fun execute(parameters: Params): Result<List<DtcCode>> {
        return try {
            // 1. Отримуємо коди з репозиторію
            val codes = dtcRepository.getDtcCodes(
                vehicleId = parameters.vehicleId,
                includeCleared = parameters.includeCleared
            )
            
            // 2. Фільтруємо за severity якщо заданий
            val filteredCodes = parameters.filterBySeverity?.let { severity ->
                codes.filter { it.severity == severity }
            } ?: codes
            
            // 3. Сортуємо: критичні першими
            val sortedCodes = filteredCodes.sortedByDescending { 
                it.severity.ordinal 
            }
            
            Result.success(sortedCodes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// Використання в ViewModel:
class DtcViewModel(
    private val getDtcCodesUseCase: GetDtcCodesUseCase
) : ViewModel() {
    
    fun loadDtcCodes(vehicleId: String) {
        viewModelScope.launch {
            val result = getDtcCodesUseCase(
                GetDtcCodesUseCase.Params(vehicleId)
            )
            
            result.fold(
                onSuccess = { codes ->
                    _uiState.update { it.copy(dtcCodes = codes) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(error = error.message) }
                }
            )
        }
    }
}
```

---

## 2️⃣ Data Layer Examples

### Repository Implementation

```kotlin
// DtcRepositoryImpl.kt
package com.quantumforce_code.core.data.repo

import com.quantumforce_code.core.data.db.DtcDao
import com.quantumforce_code.core.data.DataMappers.toDomain
import com.quantumforce_code.core.data.DataMappers.toEntity
import com.quantumforce_code.core.domain.DtcCode
import com.quantumforce_code.core.domain.repository.DtcRepository
import com.quantumforce_code.protocols.obd.ObdInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Реалізація DtcRepository.
 * 
 * Поєднує локальну базу даних (Room) та OBD інтерфейс.
 * Якщо дані є в БД, повертає їх. Інакше читає з автомобіля.
 */
class DtcRepositoryImpl(
    private val dtcDao: DtcDao,
    private val obdInterface: ObdInterface
) : DtcRepository {
    
    override suspend fun getDtcCodes(
        vehicleId: String,
        includeCleared: Boolean
    ): List<DtcCode> = withContext(Dispatchers.IO) {
        // Спочатку спробуємо отримати з локальної БД
        val localCodes = dtcDao.getAllDtcCodes()
            .map { it.toDomain() }
        
        // Якщо немає локальних даних, читаємо з автомобіля
        if (localCodes.isEmpty() && obdInterface.isConnected()) {
            val result = obdInterface.readDtcCodes()
            result.getOrNull()?.let { codes ->
                // Зберігаємо в БД для наступного разу
                codes.forEach { code ->
                    // Тут потрібна додаткова логіка для створення повного DtcCode
                }
            }
        }
        
        localCodes
    }
    
    override suspend fun getDtcByCode(code: String): DtcCode? = withContext(Dispatchers.IO) {
        dtcDao.getDtcByCode(code)?.toDomain()
    }
    
    override suspend fun saveDtcCode(dtcCode: DtcCode): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            dtcDao.insertDtcCode(dtcCode.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun clearDtcCodes(vehicleId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Очищаємо коди через OBD
            if (obdInterface.isConnected()) {
                obdInterface.clearDtcCodes()
            }
            
            // Очищаємо локальну БД
            dtcDao.clearAllDtcCodes()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun searchDtcCodes(query: String): List<DtcCode> = withContext(Dispatchers.IO) {
        dtcDao.searchDtcCodes(query)
            .map { it.toDomain() }
    }
    
    // Інші методи...
}
```

### Data Mapper Example

```kotlin
// DataMappers.kt
package com.quantumforce_code.core.data

import com.quantumforce_code.core.data.db.DtcEntity
import com.quantumforce_code.core.domain.DtcCode
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

object DataMappers {
    private val json = Json { ignoreUnknownKeys = true }
    
    /**
     * Конвертує DtcEntity (database) в DtcCode (domain).
     */
    fun DtcEntity.toDomain(): DtcCode {
        return DtcCode(
            code = code,
            description = description,
            severity = DtcCode.Severity.valueOf(severity),
            causes = json.decodeFromString(causes),
            solutions = json.decodeFromString(solutions),
            affectedSystems = json.decodeFromString(affectedSystems),
            timestamp = timestamp,
            freezeFrame = freezeFrame?.let { 
                json.decodeFromString<DtcCode.FreezeFrame>(it) 
            }
        )
    }
    
    /**
     * Конвертує DtcCode (domain) в DtcEntity (database).
     */
    fun DtcCode.toEntity(): DtcEntity {
        return DtcEntity(
            code = code,
            description = description,
            severity = severity.name,
            causes = json.encodeToString(causes),
            solutions = json.encodeToString(solutions),
            affectedSystems = json.encodeToString(affectedSystems),
            timestamp = timestamp,
            freezeFrame = freezeFrame?.let { 
                json.encodeToString(it) 
            }
        )
    }
}
```

---

## 3️⃣ Transport Layer Examples

### Bluetooth Port Implementation

```kotlin
// BluetoothPort.kt
package com.quantumforce_code.hardware.transport

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

/**
 * Реалізація Bluetooth SPP (Serial Port Profile) транспорту.
 */
class BluetoothPort(
    private val device: BluetoothDevice,
    private val context: Context
) : Port {
    
    private var socket: BluetoothSocket? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null
    
    companion object {
        // UUID для Bluetooth SPP
        private val SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }
    
    override suspend fun open(): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            // Створюємо RFCOMM socket
            socket = device.createRfcommSocketToServiceRecord(SPP_UUID)
            
            // Підключаємося (блокуюча операція)
            socket?.connect()
            
            // Отримуємо streams
            inputStream = socket?.inputStream
            outputStream = socket?.outputStream
            
            Result.success(true)
        } catch (e: IOException) {
            close()
            Result.failure(e)
        }
    }
    
    override suspend fun close() = withContext(Dispatchers.IO) {
        try {
            outputStream?.close()
            inputStream?.close()
            socket?.close()
        } catch (e: IOException) {
            // Логуємо але не кидаємо exception
        } finally {
            outputStream = null
            inputStream = null
            socket = null
        }
    }
    
    override suspend fun write(data: ByteArray): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            outputStream?.write(data)
            outputStream?.flush()
            Result.success(true)
        } catch (e: IOException) {
            Result.failure(e)
        }
    }
    
    override suspend fun read(timeout: Long): Result<ByteArray> = withContext(Dispatchers.IO) {
        try {
            val buffer = ByteArray(1024)
            val startTime = System.currentTimeMillis()
            
            // Чекаємо дані з таймаутом
            while (inputStream?.available() == 0) {
                if (System.currentTimeMillis() - startTime > timeout) {
                    return@withContext Result.success(ByteArray(0))
                }
                Thread.sleep(10)
            }
            
            val bytesRead = inputStream?.read(buffer) ?: 0
            Result.success(buffer.copyOf(bytesRead))
        } catch (e: IOException) {
            Result.failure(e)
        }
    }
    
    override suspend fun flush(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Читаємо всі доступні дані
            while (inputStream?.available() ?: 0 > 0) {
                inputStream?.skip(inputStream?.available()?.toLong() ?: 0)
            }
            Result.success(Unit)
        } catch (e: IOException) {
            Result.failure(e)
        }
    }
    
    override val isConnected: Boolean
        get() = socket?.isConnected == true
    
    override val info: Port.PortInfo
        get() = Port.PortInfo(
            type = Port.PortType.BLUETOOTH,
            address = device.address,
            name = device.name ?: "Unknown Device",
            capabilities = setOf(
                Port.Capability.BIDIRECTIONAL,
                Port.Capability.AUTO_RECONNECT
            )
        )
}
```

### Connection Manager Implementation

```kotlin
// ConnectionManagerImpl.kt
package com.quantumforce_code.hardware.transport

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ConnectionManagerImpl(
    private val context: Context
) : ConnectionManager {
    
    private var currentPort: Port? = null
    private var currentConfig: ConnectionManager.ConnectionConfig? = null
    
    private val _connectionState = MutableStateFlow<ConnectionManager.ConnectionState>(
        ConnectionManager.ConnectionState.Disconnected
    )
    override val connectionState: StateFlow<ConnectionManager.ConnectionState> = 
        _connectionState.asStateFlow()
    
    private var autoReconnectEnabled = false
    private var maxReconnectAttempts = 3
    private var reconnectDelay = 2000L
    
    override suspend fun connect(config: ConnectionManager.ConnectionConfig): Result<Port> {
        // Закриваємо попереднє з'єднання якщо є
        disconnect()
        
        _connectionState.value = ConnectionManager.ConnectionState.Connecting(
            config.name ?: config.address
        )
        
        val port = when (config.type) {
            Port.PortType.BLUETOOTH -> {
                createBluetoothPort(config.address)
            }
            Port.PortType.USB_SERIAL -> {
                createUsbSerialPort(config.address)
            }
            Port.PortType.TCP -> {
                createTcpPort(config.address, config.baudRate)
            }
            Port.PortType.MOCK -> {
                createMockPort()
            }
        }
        
        return port?.let { p ->
            val result = p.open()
            if (result.isSuccess) {
                currentPort = p
                currentConfig = config
                _connectionState.value = ConnectionManager.ConnectionState.Connected(
                    config.name ?: config.address,
                    p
                )
                Result.success(p)
            } else {
                _connectionState.value = ConnectionManager.ConnectionState.Error(
                    "Failed to open connection: ${result.exceptionOrNull()?.message}",
                    result.exceptionOrNull()
                )
                Result.failure(result.exceptionOrNull() ?: Exception("Unknown error"))
            }
        } ?: Result.failure(Exception("Failed to create port"))
    }
    
    override suspend fun disconnect() {
        currentPort?.close()
        currentPort = null
        currentConfig = null
        _connectionState.value = ConnectionManager.ConnectionState.Disconnected
    }
    
    override fun getCurrentPort(): Port? = currentPort
    
    override fun getCurrentConfig(): ConnectionManager.ConnectionConfig? = currentConfig
    
    override suspend fun scanDevices(): List<ConnectionManager.DeviceInfo> {
        val devices = mutableListOf<ConnectionManager.DeviceInfo>()
        
        // Bluetooth devices
        devices.addAll(scanBluetoothDevices())
        
        // USB devices
        devices.addAll(scanUsbDevices())
        
        // TCP devices (можна сканувати локальну мережу)
        // devices.addAll(scanTcpDevices())
        
        return devices
    }
    
    private fun scanBluetoothDevices(): List<ConnectionManager.DeviceInfo> {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        val adapter = bluetoothManager?.adapter ?: return emptyList()
        
        return adapter.bondedDevices.map { device ->
            ConnectionManager.DeviceInfo(
                name = device.name ?: "Unknown",
                address = device.address,
                type = Port.PortType.BLUETOOTH,
                isAvailable = true,
                isPaired = true
            )
        }
    }
    
    private fun createBluetoothPort(address: String): Port? {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        val adapter = bluetoothManager?.adapter ?: return null
        val device = adapter.getRemoteDevice(address)
        return BluetoothPort(device, context)
    }
    
    // Інші методи...
}
```

---

## 4️⃣ Protocol Layer Examples

### ELM327 Adapter Implementation

```kotlin
// Elm327Adapter.kt
package com.quantumforce_code.protocols.obd

import com.quantumforce_code.hardware.transport.Port
import kotlinx.coroutines.delay

/**
 * Реалізація ObdInterface для ELM327 адаптерів.
 */
class Elm327Adapter(
    private val port: Port
) : ObdInterface {
    
    private var currentProtocol: ObdProtocol? = null
    private var isInitialized = false
    
    override suspend fun initialize(): Result<Boolean> {
        if (!port.isConnected) {
            return Result.failure(Exception("Port not connected"))
        }
        
        try {
            // 1. Скидаємо адаптер
            sendRawCommand("ATZ")
            delay(1000) // Чекаємо ініціалізацію
            
            // 2. Вимикаємо echo
            sendRawCommand("ATE0")
            
            // 3. Вимикаємо заголовки
            sendRawCommand("ATH0")
            
            // 4. Вимикаємо spaces
            sendRawCommand("ATS0")
            
            // 5. Вимикаємо line feeds
            sendRawCommand("ATL0")
            
            // 6. Автоматичне визначення протоколу
            val protocolResult = detectProtocol()
            if (protocolResult.isSuccess) {
                currentProtocol = protocolResult.getOrNull()
                isInitialized = true
                return Result.success(true)
            }
            
            return Result.failure(Exception("Failed to detect protocol"))
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
    
    override suspend fun sendCommand(command: ObdCommand): Result<ObdResponse> {
        if (!isInitialized) {
            return Result.failure(Exception("Adapter not initialized"))
        }
        
        try {
            val rawResponse = sendRawCommand(command.command)
            return parseResponse(rawResponse, command)
        } catch (e: Exception) {
            return Result.success(
                ObdResponse.error(
                    rawData = "",
                    status = ObdResponse.Status.ERROR,
                    command = command
                )
            )
        }
    }
    
    override suspend fun readDtcCodes(): Result<List<String>> {
        val response = sendCommand(ObdCommand.ReadDTC)
        
        if (response.isFailure) {
            return Result.failure(response.exceptionOrNull()!!)
        }
        
        val obdResponse = response.getOrNull()!!
        if (!obdResponse.isSuccess()) {
            return Result.success(emptyList())
        }
        
        // Парсимо коди з відповіді
        val codes = parseDtcCodes(obdResponse.rawData)
        return Result.success(codes)
    }
    
    override suspend fun clearDtcCodes(): Result<Boolean> {
        val response = sendCommand(ObdCommand.ClearDTC)
        return if (response.isSuccess && response.getOrNull()?.isSuccess() == true) {
            Result.success(true)
        } else {
            Result.failure(Exception("Failed to clear DTCs"))
        }
    }
    
    override suspend fun readPid(pid: String): Result<ObdInterface.PidData> {
        val command = ObdCommand.Custom("01$pid")
        val response = sendCommand(command)
        
        if (response.isFailure || !response.getOrNull()!!.isSuccess()) {
            return Result.failure(Exception("Failed to read PID"))
        }
        
        val obdResponse = response.getOrNull()!!
        return Result.success(
            ObdInterface.PidData(
                pid = pid,
                value = obdResponse.parsedValue ?: "",
                unit = obdResponse.unit ?: "",
                name = getPidName(pid)
            )
        )
    }
    
    override suspend fun detectProtocol(): Result<ObdProtocol> {
        // Спробуємо автоматичне визначення
        sendRawCommand("ATSP0")
        
        // Відправимо тестову команду
        val testResponse = sendRawCommand("0100")
        
        // Запитаємо який протокол визначився
        val protocolResponse = sendRawCommand("ATDPN")
        
        val protocol = when (protocolResponse.trim()) {
            "6" -> ObdProtocol.ISO_15765_4_CAN_11BIT_500K
            "7" -> ObdProtocol.ISO_15765_4_CAN_29BIT_500K
            "8" -> ObdProtocol.ISO_15765_4_CAN_11BIT_250K
            "9" -> ObdProtocol.ISO_15765_4_CAN_29BIT_250K
            "3" -> ObdProtocol.ISO_9141_2
            "4" -> ObdProtocol.ISO_14230_4_KWP_5BAUD
            "5" -> ObdProtocol.ISO_14230_4_KWP_FAST
            else -> return Result.failure(Exception("Unknown protocol"))
        }
        
        return Result.success(protocol)
    }
    
    override fun isConnected(): Boolean {
        return port.isConnected && isInitialized
    }
    
    override suspend fun close() {
        port.close()
        isInitialized = false
    }
    
    /**
     * Відправляє сиру команду до адаптера.
     */
    private suspend fun sendRawCommand(command: String): String {
        // Очищаємо буфер
        port.flush()
        
        // Відправляємо команду з \r
        val data = "$command\r".toByteArray()
        val writeResult = port.write(data)
        
        if (writeResult.isFailure) {
            throw writeResult.exceptionOrNull()!!
        }
        
        // Читаємо відповідь
        val readResult = port.read(timeout = 2000)
        if (readResult.isFailure) {
            throw readResult.exceptionOrNull()!!
        }
        
        val response = readResult.getOrNull()!!.toString(Charsets.US_ASCII)
        return response.trim().replace(">", "")
    }
    
    /**
     * Парсить відповідь OBD у ObdResponse.
     */
    private fun parseResponse(rawData: String, command: ObdCommand): Result<ObdResponse> {
        when {
            rawData.contains("NO DATA") -> {
                return Result.success(ObdResponse.noData(command))
            }
            rawData.contains("ERROR") || rawData.contains("?") -> {
                return Result.success(ObdResponse.error(rawData, ObdResponse.Status.ERROR, command))
            }
            rawData.contains("UNABLE TO CONNECT") -> {
                return Result.success(ObdResponse.error(rawData, ObdResponse.Status.TIMEOUT, command))
            }
        }
        
        // Парсимо успішну відповідь
        // Тут має бути логіка парсингу залежно від команди
        return Result.success(
            ObdResponse.success(
                rawData = rawData,
                parsedValue = rawData,
                command = command
            )
        )
    }
    
    private fun parseDtcCodes(rawData: String): List<String> {
        // Парсимо DTC коди з hex відповіді
        // Формат: 43 01 03 01 04 (кількість кодів + hex коди)
        val codes = mutableListOf<String>()
        
        // Видаляємо пробіли та розбиваємо на байти
        val bytes = rawData.replace(" ", "").chunked(2)
        
        // Перший байт після "43" - кількість кодів
        // Далі йдуть коди по 2 байти кожен
        
        // Спрощена логіка:
        for (i in 2 until bytes.size step 2) {
            val code = convertDtcBytes(bytes[i], bytes[i + 1])
            if (code != "P0000") {
                codes.add(code)
            }
        }
        
        return codes
    }
    
    private fun convertDtcBytes(byte1: String, byte2: String): String {
        // Конвертує 2 hex байти в DTC код (P0301 формат)
        val firstByte = byte1.toInt(16)
        val secondByte = byte2.toInt(16)
        
        val prefix = when ((firstByte shr 6) and 0x03) {
            0 -> "P"
            1 -> "C"
            2 -> "B"
            3 -> "U"
            else -> "P"
        }
        
        val digit1 = (firstByte shr 4) and 0x03
        val digit2 = firstByte and 0x0F
        val digit3 = (secondByte shr 4) and 0x0F
        val digit4 = secondByte and 0x0F
        
        return "$prefix$digit1$digit2$digit3$digit4"
    }
    
    private fun getPidName(pid: String): String {
        return when (pid) {
            "0C" -> "Engine RPM"
            "0D" -> "Vehicle Speed"
            "05" -> "Coolant Temperature"
            "11" -> "Throttle Position"
            "0A" -> "Fuel Pressure"
            else -> "Unknown PID"
        }
    }
}
```

---

## 5️⃣ Features Layer Examples

### DTC ViewModel with State Management

```kotlin
// DtcViewModel.kt
package com.quantumforce_code.features.dtc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quantumforce_code.core.domain.DtcCode
import com.quantumforce_code.core.domain.Vehicle
import com.quantumforce_code.core.domain.usecase.GetDtcCodesUseCase
import com.quantumforce_code.core.domain.usecase.ClearDtcCodesUseCase
import com.quantumforce_code.hardware.transport.ConnectionManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel для екрану DTC діагностики.
 * 
 * Відповідає за:
 * - Управління станом UI
 * - Виклик use cases
 * - Обробку подій користувача
 */
class DtcViewModel(
    private val getDtcCodesUseCase: GetDtcCodesUseCase,
    private val clearDtcCodesUseCase: ClearDtcCodesUseCase,
    private val connectionManager: ConnectionManager
) : ViewModel() {
    
    // UI State
    private val _uiState = MutableStateFlow(DtcUiState())
    val uiState: StateFlow<DtcUiState> = _uiState.asStateFlow()
    
    // UI Effects (одноразові події)
    private val _effects = MutableSharedFlow<DtcEffect>()
    val effects: SharedFlow<DtcEffect> = _effects.asSharedFlow()
    
    init {
        // Підписуємося на зміни стану з'єднання
        viewModelScope.launch {
            connectionManager.connectionState.collect { state ->
                _uiState.update { 
                    it.copy(connectionStatus = state.toConnectionStatus()) 
                }
            }
        }
    }
    
    /**
     * Обробляє події з UI.
     */
    fun handleEvent(event: DtcEvent) {
        when (event) {
            is DtcEvent.LoadDtcCodes -> loadDtcCodes()
            is DtcEvent.ClearDtcCodes -> clearDtcCodes()
            is DtcEvent.RefreshConnection -> refreshConnection()
            is DtcEvent.SelectVehicle -> selectVehicle(event.vehicle)
            is DtcEvent.SelectDtcCode -> showDtcDetails(event.code)
            is DtcEvent.FilterBySeverity -> filterBySeverity(event.severity)
        }
    }
    
    private fun loadDtcCodes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val vehicleId = _uiState.value.selectedVehicle?.id
            if (vehicleId == null) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Виберіть автомобіль"
                    ) 
                }
                _effects.emit(DtcEffect.ShowToast("Виберіть автомобіль"))
                return@launch
            }
            
            val result = getDtcCodesUseCase(
                GetDtcCodesUseCase.Params(
                    vehicleId = vehicleId,
                    filterBySeverity = _uiState.value.selectedSeverityFilter
                )
            )
            
            result.fold(
                onSuccess = { codes ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            dtcCodes = codes,
                            error = null
                        ) 
                    }
                    
                    if (codes.isEmpty()) {
                        _effects.emit(DtcEffect.ShowToast("Коди помилок не знайдено"))
                    }
                },
                onFailure = { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = error.message
                        ) 
                    }
                    _effects.emit(DtcEffect.ShowToast("Помилка: ${error.message}"))
                }
            )
        }
    }
    
    private fun clearDtcCodes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val vehicleId = _uiState.value.selectedVehicle?.id ?: return@launch
            
            clearDtcCodesUseCase(
                ClearDtcCodesUseCase.Params(vehicleId = vehicleId)
            ).fold(
                onSuccess = {
                    _effects.emit(DtcEffect.ShowToast("DTC коди очищено"))
                    loadDtcCodes() // Перезавантажуємо список
                },
                onFailure = { error ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = error.message
                        ) 
                    }
                    _effects.emit(DtcEffect.ShowToast("Помилка очищення: ${error.message}"))
                }
            )
        }
    }
    
    private fun refreshConnection() {
        viewModelScope.launch {
            val port = connectionManager.getCurrentPort()
            if (port != null) {
                val result = connectionManager.checkConnection()
                if (result.isSuccess) {
                    _effects.emit(DtcEffect.ShowToast("З'єднання активне"))
                } else {
                    _effects.emit(DtcEffect.ShowToast("З'єднання втрачено"))
                }
            } else {
                _effects.emit(DtcEffect.ShowToast("Немає з'єднання"))
            }
        }
    }
    
    private fun selectVehicle(vehicle: Vehicle) {
        _uiState.update { it.copy(selectedVehicle = vehicle) }
    }
    
    private fun showDtcDetails(code: DtcCode) {
        viewModelScope.launch {
            _effects.emit(DtcEffect.NavigateToDetails(code))
        }
    }
    
    private fun filterBySeverity(severity: DtcCode.Severity?) {
        _uiState.update { it.copy(selectedSeverityFilter = severity) }
        loadDtcCodes() // Перезавантажуємо з фільтром
    }
    
    private fun ConnectionManager.ConnectionState.toConnectionStatus(): DtcUiState.ConnectionStatus {
        return when (this) {
            is ConnectionManager.ConnectionState.Connected -> DtcUiState.ConnectionStatus.CONNECTED
            is ConnectionManager.ConnectionState.Connecting -> DtcUiState.ConnectionStatus.CONNECTING
            is ConnectionManager.ConnectionState.Disconnected -> DtcUiState.ConnectionStatus.DISCONNECTED
            is ConnectionManager.ConnectionState.Error -> DtcUiState.ConnectionStatus.ERROR
            is ConnectionManager.ConnectionState.Reconnecting -> DtcUiState.ConnectionStatus.CONNECTING
        }
    }
}

/**
 * UI стан для DTC екрану.
 */
data class DtcUiState(
    val isLoading: Boolean = false,
    val dtcCodes: List<DtcCode> = emptyList(),
    val selectedVehicle: Vehicle? = null,
    val selectedSeverityFilter: DtcCode.Severity? = null,
    val error: String? = null,
    val connectionStatus: ConnectionStatus = ConnectionStatus.DISCONNECTED
) {
    enum class ConnectionStatus {
        CONNECTED,
        DISCONNECTED,
        CONNECTING,
        ERROR
    }
}

/**
 * UI події від користувача.
 */
sealed class DtcEvent {
    object LoadDtcCodes : DtcEvent()
    object ClearDtcCodes : DtcEvent()
    object RefreshConnection : DtcEvent()
    data class SelectVehicle(val vehicle: Vehicle) : DtcEvent()
    data class SelectDtcCode(val code: DtcCode) : DtcEvent()
    data class FilterBySeverity(val severity: DtcCode.Severity?) : DtcEvent()
}

/**
 * UI ефекти (одноразові події).
 */
sealed class DtcEffect {
    data class ShowToast(val message: String) : DtcEffect()
    data class NavigateToDetails(val dtcCode: DtcCode) : DtcEffect()
    object NavigateBack : DtcEffect()
}
```

---

## 6️⃣ UI Layer Examples

### Jetpack Compose Screen

```kotlin
// DtcScreen.kt
package com.quantumforce_code.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.quantumforce_code.core.domain.DtcCode
import com.quantumforce_code.features.dtc.DtcViewModel
import com.quantumforce_code.features.dtc.DtcEvent
import com.quantumforce_code.features.dtc.DtcEffect

/**
 * Екран діагностики DTC кодів.
 */
@Composable
fun DtcScreen(
    viewModel: DtcViewModel,
    onNavigateToDetails: (DtcCode) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Обробка ефектів
    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is DtcEffect.ShowToast -> {
                    // Показати toast
                }
                is DtcEffect.NavigateToDetails -> {
                    onNavigateToDetails(effect.dtcCode)
                }
                DtcEffect.NavigateBack -> {
                    // Навігація назад
                }
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Діагностика DTC") },
                actions = {
                    ConnectionStatusIndicator(uiState.connectionStatus)
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.handleEvent(DtcEvent.LoadDtcCodes) }
            ) {
                Text("Читати")
            }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Фільтр за severity
            SeverityFilterChips(
                selectedSeverity = uiState.selectedSeverityFilter,
                onSeveritySelected = { severity ->
                    viewModel.handleEvent(DtcEvent.FilterBySeverity(severity))
                }
            )
            
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                uiState.error != null -> {
                    ErrorView(
                        message = uiState.error!!,
                        onRetry = { viewModel.handleEvent(DtcEvent.LoadDtcCodes) }
                    )
                }
                uiState.dtcCodes.isEmpty() -> {
                    EmptyView(
                        message = "Немає кодів помилок"
                    )
                }
                else -> {
                    DtcCodesList(
                        codes = uiState.dtcCodes,
                        onCodeClick = { code ->
                            viewModel.handleEvent(DtcEvent.SelectDtcCode(code))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DtcCodesList(
    codes: List<DtcCode>,
    onCodeClick: (DtcCode) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(codes) { code ->
            DtcCodeCard(
                dtcCode = code,
                onClick = { onCodeClick(code) }
            )
        }
    }
}

@Composable
private fun DtcCodeCard(
    dtcCode: DtcCode,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = dtcCode.code,
                    style = MaterialTheme.typography.titleMedium
                )
                SeverityBadge(severity = dtcCode.severity)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = dtcCode.description,
                style = MaterialTheme.typography.bodyMedium
            )
            
            if (dtcCode.affectedSystems.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Системи: ${dtcCode.affectedSystems.joinToString()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SeverityBadge(
    severity: DtcCode.Severity,
    modifier: Modifier = Modifier
) {
    val (text, color) = when (severity) {
        DtcCode.Severity.CRITICAL -> "Критичний" to MaterialTheme.colorScheme.error
        DtcCode.Severity.HIGH -> "Високий" to MaterialTheme.colorScheme.errorContainer
        DtcCode.Severity.MEDIUM -> "Середній" to MaterialTheme.colorScheme.primaryContainer
        DtcCode.Severity.LOW -> "Низький" to MaterialTheme.colorScheme.surfaceVariant
    }
    
    Surface(
        color = color,
        shape = MaterialTheme.shapes.small,
        modifier = modifier
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall
        )
    }
}
```

---

## 7️⃣ Integration Examples

### Complete App Module Setup

```kotlin
// MainActivity.kt
package com.quantumforce_code.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.quantumforce_code.app.navigation.NavGraph
import com.quantumforce_code.app.ui.theme.AutoDiagProTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AutoDiagProTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph()
                }
            }
        }
    }
}
```

### Dependency Injection Module

```kotlin
// AppModule.kt
package com.quantumforce_code.app.di

import android.content.Context
import com.quantumforce_code.core.data.db.AppDatabase
import com.quantumforce_code.core.data.repo.DtcRepositoryImpl
import com.quantumforce_code.core.domain.repository.DtcRepository
import com.quantumforce_code.core.domain.usecase.GetDtcCodesUseCase
import com.quantumforce_code.core.domain.usecase.ClearDtcCodesUseCase
import com.quantumforce_code.hardware.transport.ConnectionManager
import com.quantumforce_code.hardware.transport.ConnectionManagerImpl
import com.quantumforce_code.protocols.obd.ObdInterface
import com.quantumforce_code.protocols.obd.Elm327Adapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return AppDatabase.create(context)
    }
    
    @Provides
    @Singleton
    fun provideConnectionManager(
        @ApplicationContext context: Context
    ): ConnectionManager {
        return ConnectionManagerImpl(context)
    }
    
    @Provides
    fun provideObdInterface(
        connectionManager: ConnectionManager
    ): ObdInterface {
        val port = connectionManager.getCurrentPort()
            ?: throw IllegalStateException("No active connection")
        return Elm327Adapter(port)
    }
    
    @Provides
    fun provideDtcRepository(
        database: AppDatabase,
        obdInterface: ObdInterface
    ): DtcRepository {
        return DtcRepositoryImpl(
            dtcDao = database.dtcDao(),
            obdInterface = obdInterface
        )
    }
    
    @Provides
    fun provideGetDtcCodesUseCase(
        dtcRepository: DtcRepository
    ): GetDtcCodesUseCase {
        return GetDtcCodesUseCase(dtcRepository)
    }
    
    @Provides
    fun provideClearDtcCodesUseCase(
        dtcRepository: DtcRepository
    ): ClearDtcCodesUseCase {
        return ClearDtcCodesUseCase(dtcRepository)
    }
}
```

---

Цей документ надає повні приклади реалізації для кожного шару архітектури. Використовуйте ці приклади як шаблони для створення власних компонентів у проєкті QuantumForce_Code.

Для AI-агентів: Ці приклади демонструють реальну реалізацію інтерфейсів, визначених у MODULAR_ARCHITECTURE_GUIDE.md. Дотримуйтеся цих патернів при генерації коду.

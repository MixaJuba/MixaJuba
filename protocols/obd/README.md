# 🛰️ protocols/obd - OBD-II Protocol Implementation | Реалізація Протоколу OBD-II

## 📋 File Purpose | Призначення
Імплементація OBD-II (On-Board Diagnostics II) протоколу для діагностики автомобілів. Підтримка ELM327 адаптера, парсинг PID та DTC відповідей.

## 🎯 Role | Роль
Абстракція протокольного шару - перетворення high-level команд (read DTC, read PID) у низькорівневі OBD команди та парсинг відповідей.

---

## 📂 Структура / Structure

```
protocols/obd/
├── build.gradle.kts
└── src/main/kotlin/com/quantumforce_code/protocols/obd/
    ├── ObdInterface.kt         # Abstract OBD command interface
    ├── Elm327.kt               # ELM327 adapter implementation
    ├── ObdCommand.kt           # OBD command model
    ├── PidParser.kt            # PID response parser
    └── DtcParser.kt            # DTC code parser
```

---

## 🚗 OBD-II Overview | Огляд OBD-II

### Що таке OBD-II?
**OBD-II (On-Board Diagnostics II)** - стандарт автомобільної діагностики, обов'язковий для всіх авто з 1996 року (США), 2001 (Європа).

### Основні можливості:
- ✅ Читання DTC (Diagnostic Trouble Codes) - коди помилок
- ✅ Очистка DTC
- ✅ Читання live-даних (швидкість, обороти, температура)
- ✅ Freeze Frame data - snapshot при помилці
- ✅ Oxygen sensor monitoring
- ✅ On-board tests (evap system, catalyst)

### Підтримувані протоколи:
1. **ISO 9141-2** - старий (К-line), до 10.4 kbit/s
2. **ISO 14230-4 (KWP2000)** - покращений К-line
3. **ISO 15765-4 (CAN)** - сучасний CAN bus, 250/500 kbit/s
4. **SAE J1850 PWM** - Ford, Mazda
5. **SAE J1850 VPW** - GM

---

## 🎨 Architecture | Архітектура

```
┌─────────────────────────────────────┐
│     features/ (ViewModels)          │  ← Бізнес-логіка
└─────────────────────────────────────┘
                 ↓ uses
┌─────────────────────────────────────┐
│     protocols/obd/                  │  ← Протокольний шар
│     (ObdInterface, Elm327)          │
└─────────────────────────────────────┘
         ↓ uses                ↓ uses
┌──────────────────┐    ┌──────────────────┐
│   PidParser      │    │   DtcParser      │
│   (decode PIDs)  │    │   (decode DTCs)  │
└──────────────────┘    └──────────────────┘
                 ↓ uses
┌─────────────────────────────────────┐
│     hardware/transport/             │  ← Фізичне підключення
│     (Port: Bluetooth/USB/TCP)       │
└─────────────────────────────────────┘
                 ↓
┌─────────────────────────────────────┐
│     ELM327 OBD-II Adapter           │  ← Залізо
└─────────────────────────────────────┘
```

---

## 🔌 ObdInterface.kt - Abstract Interface

### Призначення
Абстракція для OBD команд. Дозволяє мокати для тестів або додавати інші адаптери (не тільки ELM327).

### Код:
```kotlin
// 1. File Purpose: Abstract interface for OBD-II command execution
// 2. Role: Defines contract for all OBD implementations (ELM327, etc.)

interface ObdInterface {
    /**
     * Відправити OBD команду та отримати відповідь
     * @param command OBD команда (mode + PID)
     * @return рядок відповіді або null при помилці
     */
    suspend fun sendCommand(command: ObdCommand): String?
    
    /**
     * Ініціалізувати з'єднання з адаптером
     */
    suspend fun initialize(): Boolean
    
    /**
     * Перевірити стан з'єднання
     */
    fun isConnected(): Boolean
    
    /**
     * Закрити з'єднання
     */
    suspend fun disconnect()
    
    /**
     * Отримати протокол, який використовується
     */
    fun getProtocol(): String?
}
```

---

## 📟 Elm327.kt - ELM327 Adapter Implementation

### Призначення
Реалізація для найпопулярнішого OBD адаптера - ELM327 (чіп від Elm Electronics).

### Технічні деталі:
- **AT Commands:** Команди конфігурації (ATZ, ATE0, ATL0, ATSP0)
- **OBD Commands:** Режими 01-0A (01 = live data, 03 = read DTC, 04 = clear DTC)
- **Auto-protocol:** Автоматичне визначення протоколу авто

### Код:
```kotlin
// 1. File Purpose: ELM327 adapter implementation for OBD-II
// 2. Role: Handles ELM327-specific initialization and command execution

class Elm327(
    private val port: Port
) : ObdInterface {
    
    companion object {
        private const val PROMPT = ">"
        private const val TIMEOUT_MS = 2000L
    }
    
    private var currentProtocol: String? = null
    private var initialized = false
    
    override suspend fun initialize(): Boolean = withContext(Dispatchers.IO) {
        if (!port.isOpen()) {
            Log.e("Elm327", "Port not open")
            return@withContext false
        }
        
        try {
            // 1. Reset адаптер
            sendAtCommand("ATZ")  // Reset
            delay(1000)  // ELM327 потребує час на reset
            
            // 2. Вимкнути echo (щоб не отримувати команду назад)
            sendAtCommand("ATE0")
            
            // 3. Вимкнути spaces (компактний формат)
            sendAtCommand("ATS0")
            
            // 4. Вимкнути line feeds
            sendAtCommand("ATL0")
            
            // 5. Вимкнути заголовки (headers)
            sendAtCommand("ATH0")
            
            // 6. Автоматичний вибір протоколу
            sendAtCommand("ATSP0")
            
            // 7. Перевірити з'єднання з авто (01 00 = supported PIDs)
            val response = sendCommand(ObdCommand(mode = "01", pid = "00"))
            
            if (response != null && !response.contains("ERROR")) {
                // 8. Отримати протокол
                val protocolResponse = sendAtCommand("ATDPN")
                currentProtocol = parseProtocol(protocolResponse)
                
                initialized = true
                Log.i("Elm327", "Initialized with protocol: $currentProtocol")
                true
            } else {
                Log.e("Elm327", "No response from vehicle")
                false
            }
        } catch (e: Exception) {
            Log.e("Elm327", "Initialization failed: ${e.message}")
            false
        }
    }
    
    override suspend fun sendCommand(command: ObdCommand): String? {
        if (!initialized) {
            Log.e("Elm327", "Not initialized")
            return null
        }
        
        return try {
            val cmdString = "${command.mode}${command.pid}\r"
            port.write(cmdString)
            
            val response = port.read(TIMEOUT_MS)
            response?.replace(PROMPT, "")?.trim()
        } catch (e: Exception) {
            Log.e("Elm327", "Command failed: ${e.message}")
            null
        }
    }
    
    private suspend fun sendAtCommand(command: String): String? {
        port.write("$command\r")
        return port.read(TIMEOUT_MS)?.replace(PROMPT, "")?.trim()
    }
    
    private fun parseProtocol(response: String?): String? {
        return when (response?.firstOrNull()) {
            '1' -> "SAE J1850 PWM"
            '2' -> "SAE J1850 VPW"
            '3' -> "ISO 9141-2"
            '4' -> "ISO 14230-4 (KWP 5 baud)"
            '5' -> "ISO 14230-4 (KWP fast)"
            '6' -> "ISO 15765-4 (CAN 11/500)"
            '7' -> "ISO 15765-4 (CAN 29/500)"
            '8' -> "ISO 15765-4 (CAN 11/250)"
            '9' -> "ISO 15765-4 (CAN 29/250)"
            'A' -> "SAE J1939 (CAN 29/250)"
            else -> "Unknown"
        }
    }
    
    override fun isConnected() = initialized && port.isOpen()
    
    override suspend fun disconnect() {
        initialized = false
        currentProtocol = null
        port.close()
    }
    
    override fun getProtocol() = currentProtocol
}
```

### AT Commands пояснення:
- `ATZ` - Software reset адаптера
- `ATE0` - Echo OFF (не повертати команду)
- `ATS0` - Spaces OFF (41 0C 1F C0 → 410C1FC0)
- `ATL0` - Line feeds OFF
- `ATH0` - Headers OFF (не показувати CAN headers)
- `ATSP0` - Set Protocol 0 (auto-detect)
- `ATDPN` - Describe Protocol by Number

---

## 📊 ObdCommand.kt - Command Model

### Призначення
Модель OBD команди (mode + PID).

### OBD Modes (Services):
```
01 - Show current data (live PIDs)
02 - Show freeze frame data
03 - Show stored DTCs
04 - Clear DTCs and stored values
05 - Test results (oxygen sensor)
06 - Test results (non-continuously monitored)
07 - Show pending DTCs
08 - Control operation (evap test, etc.)
09 - Request vehicle information (VIN, calibration ID)
0A - Permanent DTCs (cannot be cleared)
```

### Код:
```kotlin
// 1. File Purpose: OBD command data model
// 2. Role: Represents a single OBD-II command (mode + PID)

data class ObdCommand(
    val mode: String,    // "01", "03", "09", etc.
    val pid: String = "" // "0C", "0D", "05" (може бути порожнім для деяких команд)
) {
    companion object {
        // Mode 01: Live Data PIDs
        val ENGINE_RPM = ObdCommand("01", "0C")         // RPM * 4
        val VEHICLE_SPEED = ObdCommand("01", "0D")      // km/h
        val ENGINE_COOLANT_TEMP = ObdCommand("01", "05") // °C - 40
        val THROTTLE_POSITION = ObdCommand("01", "11")   // % * 100 / 255
        val ENGINE_LOAD = ObdCommand("01", "04")         // % * 100 / 255
        val FUEL_LEVEL = ObdCommand("01", "2F")          // % * 100 / 255
        
        // Mode 03: Read DTCs
        val READ_DTC = ObdCommand("03")
        
        // Mode 04: Clear DTCs
        val CLEAR_DTC = ObdCommand("04")
        
        // Mode 09: Vehicle Info
        val VIN = ObdCommand("09", "02")                 // Vehicle Identification Number
        val CALIBRATION_ID = ObdCommand("09", "04")
        val ECU_NAME = ObdCommand("09", "0A")
    }
    
    override fun toString() = "$mode $pid".trim()
}
```

---

## 🔍 PidParser.kt - PID Response Parser

### Призначення
Парсинг відповідей на PID запити (режим 01, 02).

### Формат відповіді:
```
Команда:  01 0C          (Read RPM)
Відповідь: 41 0C 1F C0   (41 = response to mode 01, 0C = PID, 1F C0 = data)
Розрахунок: RPM = ((1F * 256) + C0) / 4 = 2032 RPM
```

### Код:
```kotlin
// 1. File Purpose: Parser for OBD-II PID responses (Mode 01, 02)
// 2. Role: Converts raw hex response to human-readable values

class PidParser {
    /**
     * Парсинг RPM (обороти двигуна)
     * PID: 0C, Bytes: 2, Formula: ((A*256)+B)/4
     */
    fun parseRpm(response: String): Int? {
        val bytes = extractDataBytes(response, expectedPid = "0C", expectedBytes = 2)
            ?: return null
        
        val a = bytes[0]
        val b = bytes[1]
        return ((a * 256) + b) / 4
    }
    
    /**
     * Парсинг швидкості (км/год)
     * PID: 0D, Bytes: 1, Formula: A
     */
    fun parseSpeed(response: String): Int? {
        val bytes = extractDataBytes(response, expectedPid = "0D", expectedBytes = 1)
            ?: return null
        
        return bytes[0]
    }
    
    /**
     * Парсинг температури охолоджуючої рідини (°C)
     * PID: 05, Bytes: 1, Formula: A - 40
     */
    fun parseCoolantTemp(response: String): Int? {
        val bytes = extractDataBytes(response, expectedPid = "05", expectedBytes = 1)
            ?: return null
        
        return bytes[0] - 40
    }
    
    /**
     * Парсинг положення дросельної заслінки (%)
     * PID: 11, Bytes: 1, Formula: A * 100 / 255
     */
    fun parseThrottlePosition(response: String): Float? {
        val bytes = extractDataBytes(response, expectedPid = "11", expectedBytes = 1)
            ?: return null
        
        return bytes[0] * 100f / 255f
    }
    
    /**
     * Витягнути data bytes з відповіді
     * Приклад: "41 0C 1F C0" → PID=0C, data=[1F, C0]
     */
    private fun extractDataBytes(
        response: String,
        expectedPid: String,
        expectedBytes: Int
    ): IntArray? {
        // Очистити response від spaces і prompt
        val clean = response.replace(" ", "").replace(">", "").trim()
        
        // Перевірити формат: має починатися з "41" (response to mode 01)
        if (!clean.startsWith("41")) {
            Log.e("PidParser", "Invalid response format: $response")
            return null
        }
        
        // Витягнути PID (2 hex digits після "41")
        val pid = clean.substring(2, 4)
        if (pid != expectedPid.uppercase()) {
            Log.e("PidParser", "PID mismatch: expected $expectedPid, got $pid")
            return null
        }
        
        // Витягнути data bytes
        val dataHex = clean.substring(4)
        if (dataHex.length < expectedBytes * 2) {
            Log.e("PidParser", "Not enough data bytes")
            return null
        }
        
        // Конвертувати hex в integers
        return IntArray(expectedBytes) { i ->
            val hexByte = dataHex.substring(i * 2, i * 2 + 2)
            hexByte.toInt(16)
        }
    }
}
```

---

## 🔴 DtcParser.kt - DTC Code Parser

### Призначення
Парсинг DTC (Diagnostic Trouble Codes) з режиму 03 (stored DTCs) або 07 (pending DTCs).

### Формат DTC:
```
Команда:  03             (Read stored DTCs)
Відповідь: 43 02 01 33 02 04  
           ^^ ^^ ^^^^^ ^^^^^
           |  |   |      |
           |  |   |      └─ DTC 2: 02 04 → P0204
           |  |   └──────── DTC 1: 01 33 → P0133
           |  └──────────── Кількість DTCs (02 = 2 коди)
           └─────────────── 43 = response to mode 03
```

### DTC Structure:
```
Перші 2 біти визначають категорію:
00 = P (Powertrain)     - двигун, трансмісія
01 = C (Chassis)        - ABS, suspension
10 = B (Body)           - airbag, climate
11 = U (Network)        - CAN, communication

Приклад: P0133
P - Powertrain
0 - Generic (0), Manufacturer-specific (1-3)
133 - Специфічний код (O2 Sensor Circuit Slow Response)
```

### Код:
```kotlin
// 1. File Purpose: Parser for DTC codes (Mode 03, 07, 0A)
// 2. Role: Converts raw hex DTC data to standardized codes (P0133, etc.)

class DtcParser {
    /**
     * Парсинг DTC відповіді
     * @param response hex рядок з DTCs
     * @return список DTC codes
     */
    fun parseDtcResponse(response: String): List<String> {
        val clean = response.replace(" ", "").replace(">", "").trim()
        
        // Перевірити формат: "43" (response to mode 03)
        if (!clean.startsWith("43")) {
            Log.e("DtcParser", "Invalid DTC response: $response")
            return emptyList()
        }
        
        // Витягнути кількість DTCs (1 byte після "43")
        val countHex = clean.substring(2, 4)
        val count = countHex.toInt(16)
        
        if (count == 0) {
            return emptyList()  // Немає помилок
        }
        
        // Парсинг кожного DTC (2 bytes = 4 hex digits)
        val dtcData = clean.substring(4)  // Після "43 XX"
        val dtcs = mutableListOf<String>()
        
        var i = 0
        while (i < dtcData.length && i < count * 4) {
            val dtcHex = dtcData.substring(i, minOf(i + 4, dtcData.length))
            if (dtcHex.length == 4) {
                val dtcCode = decodeDtc(dtcHex)
                dtcs.add(dtcCode)
            }
            i += 4
        }
        
        return dtcs
    }
    
    /**
     * Декодувати 4 hex digits в DTC код
     * Приклад: "0133" → "P0133"
     */
    private fun decodeDtc(hex: String): String {
        val value = hex.toInt(16)
        
        // Перші 2 біти - категорія
        val category = when ((value shr 14) and 0x03) {
            0 -> 'P'  // Powertrain
            1 -> 'C'  // Chassis
            2 -> 'B'  // Body
            3 -> 'U'  // Network
            else -> 'P'
        }
        
        // Другі 2 біти - generic/manufacturer
        val firstDigit = (value shr 12) and 0x03
        
        // Останні 12 біт - код
        val code = value and 0x0FFF
        
        return String.format("%c%01X%03X", category, firstDigit, code)
    }
}
```

### Приклади DTC:
- **P0133** - O2 Sensor Circuit Slow Response (Bank 1, Sensor 1)
- **P0301** - Cylinder 1 Misfire Detected
- **P0420** - Catalyst System Efficiency Below Threshold (Bank 1)
- **U0100** - Lost Communication with ECM/PCM 'A'
- **C1234** - ABS Wheel Speed Sensor Circuit Malfunction

---

## 🧪 Testing

### Mock ObdInterface:
```kotlin
class MockObdInterface : ObdInterface {
    val sentCommands = mutableListOf<ObdCommand>()
    var responses = mutableMapOf<String, String>()
    
    override suspend fun sendCommand(command: ObdCommand): String? {
        sentCommands.add(command)
        return responses[command.toString()]
    }
    
    override suspend fun initialize() = true
    override fun isConnected() = true
    override suspend fun disconnect() {}
    override fun getProtocol() = "Mock Protocol"
}

// Використання в тестах
val mock = MockObdInterface()
mock.responses["01 0C"] = "41 0C 1F C0"  // RPM = 2032

val rpm = PidParser().parseRpm(mock.sendCommand(ObdCommand.ENGINE_RPM)!!)
assertEquals(2032, rpm)
```

---

## 🔗 Ресурси / Resources
- [ELM327 Datasheet](https://www.elmelectronics.com/wp-content/uploads/2017/01/ELM327DS.pdf)
- [OBD-II PIDs (Wikipedia)](https://en.wikipedia.org/wiki/OBD-II_PIDs)
- [SAE J1979 Standard](https://www.sae.org/standards/content/j1979_201202/)
- [ISO 15765-4 (CAN)](https://www.iso.org/standard/66574.html)

---

## 📖 Додаткові Матеріали
- Список всіх PIDs: `docs/OBD_PID_LIST.md` (TODO)
- DTC database: 50,000+ кодів у `core/data/dtc_database.db`
- Freeze Frame parsing: `docs/FREEZE_FRAME_FORMAT.md` (TODO)

---

**Maintained by:** Protocol Agent / RepoBuilder 🤖  
**Tech Stack:** Kotlin + Coroutines + ELM327  
**Last Updated:** 2025 🚦⚡  
**Standards:** ISO 15765-4, SAE J1979, ISO 9141-2 🛰️

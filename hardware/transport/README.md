# 🔌 hardware/transport - Physical Connection Layer | Шар Фізичних Підключень

## 📋 File Purpose | Призначення
Модуль для фізичних підключень до OBD-II адаптерів через Bluetooth, USB або Wi-Fi (TCP).

## 🎯 Role | Роль
Абстракція транспортного шару - уніфікований інтерфейс для різних типів підключень. Незалежний від протоколу (OBD-II).

---

## 📂 Структура / Structure

```
hardware/transport/
├── build.gradle.kts
└── src/main/kotlin/com/quantumforce_code/hardware/transport/
    ├── Port.kt                 # Abstract port interface
    ├── BluetoothPort.kt        # Bluetooth RFCOMM implementation
    ├── UsbSerialPort.kt        # USB OTG serial implementation
    ├── TcpPort.kt              # Wi-Fi/TCP implementation
    └── ConnectionManager.kt    # Port selection & management
```

---

## 🎨 Architecture | Архітектура

```
┌───────────────────────────────────────┐
│      protocols/obd/                   │  ← OBD-II commands
│      (ObdInterface, Elm327)           │
└───────────────────────────────────────┘
                 ↓ uses
┌───────────────────────────────────────┐
│      hardware/transport/              │  ← Abstract Port
│      (Port interface)                 │
└───────────────────────────────────────┘
         ↓              ↓            ↓
┌──────────────┐ ┌─────────────┐ ┌──────────────┐
│ BluetoothPort│ │ UsbSerialPort│ │   TcpPort    │
│   (RFCOMM)   │ │   (OTG)     │ │  (Wi-Fi)     │
└──────────────┘ └─────────────┘ └──────────────┘
         ↓              ↓            ↓
┌──────────────┐ ┌─────────────┐ ┌──────────────┐
│   BT Adapter │ │ USB Device  │ │ TCP Socket   │
│   (Hardware) │ │  (Hardware) │ │  (Network)   │
└──────────────┘ └─────────────┘ └──────────────┘
```

**Переваги абстракції:**
- ✅ Протокол не знає про деталі підключення
- ✅ Легко додати новий транспорт (наприклад, BLE)
- ✅ Тестабельність (mock Port interface)

---

## 🔌 Port.kt - Abstract Interface

### Призначення
Базовий інтерфейс для всіх транспортів. Визначає contract для читання/запису даних.

### Код:
```kotlin
// 1. File Purpose: Abstract interface for physical connection ports
// 2. Role: Defines contract for all transport implementations (Bluetooth, USB, TCP)

interface Port {
    /**
     * Відкрити з'єднання
     * @return true якщо успішно
     */
    suspend fun open(): Boolean
    
    /**
     * Закрити з'єднання
     */
    suspend fun close()
    
    /**
     * Перевірити чи з'єднання активне
     */
    fun isOpen(): Boolean
    
    /**
     * Записати дані (команду) в порт
     * @param data рядок для відправки (наприклад, "ATZ\r")
     * @return кількість записаних байтів
     */
    suspend fun write(data: String): Int
    
    /**
     * Прочитати дані з порту
     * @param timeoutMs таймаут у мілісекундах
     * @return отримані дані як рядок, або null при таймауті
     */
    suspend fun read(timeoutMs: Long = 1000): String?
    
    /**
     * Очистити буфери вводу/виводу
     */
    suspend fun flush()
    
    /**
     * Отримати тип порту (для логування/діагностики)
     */
    fun getType(): PortType
}

enum class PortType {
    BLUETOOTH,
    USB_SERIAL,
    TCP,
    UNKNOWN
}
```

### Приклад використання:
```kotlin
val port: Port = BluetoothPort(bluetoothDevice)

try {
    port.open()
    port.write("ATZ\r")  // Reset ELM327
    val response = port.read(2000)  // Wait 2 sec
    println("Response: $response")
} finally {
    port.close()
}
```

---

## 📶 BluetoothPort.kt - Bluetooth Implementation

### Призначення
Реалізація для Bluetooth Classic (RFCOMM) підключення до OBD-II адаптерів.

### Технічні деталі:
- **Протокол:** Bluetooth RFCOMM (Serial Port Profile - SPP)
- **UUID:** `00001101-0000-1000-8000-00805F9B34FB` (стандартний для SPP)
- **Baud rate:** Не потрібен (Bluetooth автоматично)

### Код:
```kotlin
// 1. File Purpose: Bluetooth RFCOMM port implementation for OBD-II adapters
// 2. Role: Handles Bluetooth Classic connections (Serial Port Profile)

class BluetoothPort(
    private val bluetoothDevice: BluetoothDevice
) : Port {
    
    companion object {
        private val SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        private const val BUFFER_SIZE = 1024
    }
    
    private var socket: BluetoothSocket? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null
    
    override suspend fun open(): Boolean = withContext(Dispatchers.IO) {
        try {
            socket = bluetoothDevice.createRfcommSocketToServiceRecord(SPP_UUID)
            socket?.connect()
            
            inputStream = socket?.inputStream
            outputStream = socket?.outputStream
            
            true
        } catch (e: IOException) {
            Log.e("BluetoothPort", "Failed to open: ${e.message}")
            close()
            false
        }
    }
    
    override suspend fun close() = withContext(Dispatchers.IO) {
        try {
            inputStream?.close()
            outputStream?.close()
            socket?.close()
        } catch (e: IOException) {
            Log.e("BluetoothPort", "Error closing: ${e.message}")
        } finally {
            socket = null
            inputStream = null
            outputStream = null
        }
    }
    
    override fun isOpen(): Boolean {
        return socket?.isConnected == true
    }
    
    override suspend fun write(data: String): Int = withContext(Dispatchers.IO) {
        val bytes = data.toByteArray(Charsets.US_ASCII)
        outputStream?.write(bytes)
        outputStream?.flush()
        bytes.size
    }
    
    override suspend fun read(timeoutMs: Long): String? = withContext(Dispatchers.IO) {
        val buffer = ByteArray(BUFFER_SIZE)
        val startTime = System.currentTimeMillis()
        val result = StringBuilder()
        
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            if (inputStream?.available() ?: 0 > 0) {
                val bytesRead = inputStream?.read(buffer) ?: 0
                if (bytesRead > 0) {
                    result.append(String(buffer, 0, bytesRead, Charsets.US_ASCII))
                    
                    // Якщо отримали '>', це кінець відповіді ELM327
                    if (result.contains('>')) {
                        break
                    }
                }
            } else {
                delay(50)  // Wait for data
            }
        }
        
        if (result.isNotEmpty()) result.toString() else null
    }
    
    override suspend fun flush() = withContext(Dispatchers.IO) {
        while (inputStream?.available() ?: 0 > 0) {
            inputStream?.skip(inputStream?.available()?.toLong() ?: 0)
        }
    }
    
    override fun getType() = PortType.BLUETOOTH
}
```

### Permissions потрібні:
```xml
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
```

---

## 🔌 UsbSerialPort.kt - USB OTG Implementation

### Призначення
Реалізація для USB OTG підключення до OBD-II адаптерів (ELM327 USB версії).

### Технічні деталі:
- **Library:** [usb-serial-for-android](https://github.com/mik3y/usb-serial-for-android)
- **Drivers:** FTDI, CP210x, CH34x, CDC ACM
- **Baud rate:** 38400 (стандарт для ELM327)

### Код:
```kotlin
// 1. File Purpose: USB OTG serial port implementation for OBD-II adapters
// 2. Role: Handles USB serial connections (ELM327 USB variants)

class UsbSerialPort(
    private val usbDevice: UsbDevice,
    private val usbManager: UsbManager
) : Port {
    
    companion object {
        private const val BAUD_RATE = 38400
        private const val DATA_BITS = 8
        private const val STOP_BITS = UsbSerialPort.STOPBITS_1
        private const val PARITY = UsbSerialPort.PARITY_NONE
        private const val READ_TIMEOUT = 1000
        private const val WRITE_TIMEOUT = 1000
    }
    
    private var connection: UsbDeviceConnection? = null
    private var serialPort: UsbSerialPort? = null
    
    override suspend fun open(): Boolean = withContext(Dispatchers.IO) {
        try {
            // Знайти драйвер для пристрою
            val driver = UsbSerialProber.getDefaultProber()
                .probeDevice(usbDevice)
            
            if (driver == null) {
                Log.e("UsbSerialPort", "No driver for device")
                return@withContext false
            }
            
            connection = usbManager.openDevice(usbDevice)
            serialPort = driver.ports[0]  // Перший порт
            
            serialPort?.open(connection)
            serialPort?.setParameters(BAUD_RATE, DATA_BITS, STOP_BITS, PARITY)
            
            true
        } catch (e: IOException) {
            Log.e("UsbSerialPort", "Failed to open: ${e.message}")
            close()
            false
        }
    }
    
    override suspend fun close() = withContext(Dispatchers.IO) {
        try {
            serialPort?.close()
            connection?.close()
        } catch (e: IOException) {
            Log.e("UsbSerialPort", "Error closing: ${e.message}")
        } finally {
            serialPort = null
            connection = null
        }
    }
    
    override fun isOpen(): Boolean {
        return serialPort?.isOpen == true
    }
    
    override suspend fun write(data: String): Int = withContext(Dispatchers.IO) {
        val bytes = data.toByteArray(Charsets.US_ASCII)
        serialPort?.write(bytes, WRITE_TIMEOUT)
        bytes.size
    }
    
    override suspend fun read(timeoutMs: Long): String? = withContext(Dispatchers.IO) {
        val buffer = ByteArray(1024)
        val result = StringBuilder()
        val startTime = System.currentTimeMillis()
        
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            val bytesRead = serialPort?.read(buffer, READ_TIMEOUT.toInt()) ?: 0
            if (bytesRead > 0) {
                result.append(String(buffer, 0, bytesRead, Charsets.US_ASCII))
                if (result.contains('>')) break
            }
        }
        
        if (result.isNotEmpty()) result.toString() else null
    }
    
    override suspend fun flush() = withContext(Dispatchers.IO) {
        serialPort?.purgeHwBuffers(true, true)
    }
    
    override fun getType() = PortType.USB_SERIAL
}
```

### Permissions:
```xml
<uses-feature android:name="android.hardware.usb.host" />
<uses-permission android:name="android.permission.USB_PERMISSION" />
```

---

## 🌐 TcpPort.kt - Wi-Fi/TCP Implementation

### Призначення
Реалізація для Wi-Fi OBD-II адаптерів (ELM327 Wi-Fi, OBDLink MX+).

### Технічні деталі:
- **Protocol:** TCP Socket
- **Default IP:** `192.168.0.10` (стандарт для Wi-Fi адаптерів)
- **Default Port:** `35000`

### Код:
```kotlin
// 1. File Purpose: TCP socket port implementation for Wi-Fi OBD-II adapters
// 2. Role: Handles Wi-Fi/TCP connections (ELM327 Wi-Fi variants)

class TcpPort(
    private val host: String = "192.168.0.10",
    private val port: Int = 35000
) : Port {
    
    private var socket: Socket? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null
    
    override suspend fun open(): Boolean = withContext(Dispatchers.IO) {
        try {
            socket = Socket()
            socket?.connect(InetSocketAddress(host, port), 5000)  // 5 sec timeout
            
            inputStream = socket?.getInputStream()
            outputStream = socket?.getOutputStream()
            
            true
        } catch (e: IOException) {
            Log.e("TcpPort", "Failed to connect to $host:$port - ${e.message}")
            close()
            false
        }
    }
    
    override suspend fun close() = withContext(Dispatchers.IO) {
        try {
            inputStream?.close()
            outputStream?.close()
            socket?.close()
        } catch (e: IOException) {
            Log.e("TcpPort", "Error closing: ${e.message}")
        } finally {
            socket = null
            inputStream = null
            outputStream = null
        }
    }
    
    override fun isOpen(): Boolean {
        return socket?.isConnected == true && socket?.isClosed == false
    }
    
    override suspend fun write(data: String): Int = withContext(Dispatchers.IO) {
        val bytes = data.toByteArray(Charsets.US_ASCII)
        outputStream?.write(bytes)
        outputStream?.flush()
        bytes.size
    }
    
    override suspend fun read(timeoutMs: Long): String? = withContext(Dispatchers.IO) {
        socket?.soTimeout = timeoutMs.toInt()
        
        val buffer = ByteArray(1024)
        val result = StringBuilder()
        
        try {
            while (true) {
                val bytesRead = inputStream?.read(buffer) ?: 0
                if (bytesRead > 0) {
                    result.append(String(buffer, 0, bytesRead, Charsets.US_ASCII))
                    if (result.contains('>')) break
                }
            }
        } catch (e: SocketTimeoutException) {
            // Timeout - це нормально
        }
        
        if (result.isNotEmpty()) result.toString() else null
    }
    
    override suspend fun flush() = withContext(Dispatchers.IO) {
        while (inputStream?.available() ?: 0 > 0) {
            inputStream?.skip(inputStream?.available()?.toLong() ?: 0)
        }
    }
    
    override fun getType() = PortType.TCP
}
```

---

## 🔧 ConnectionManager.kt - Port Management

### Призначення
Автоматичний вибір та управління портами. Спрощує роботу з різними транспортами.

### Код:
```kotlin
// 1. File Purpose: Manages port selection and lifecycle
// 2. Role: Simplifies port usage, auto-detection, connection pooling

class ConnectionManager(
    private val context: Context
) {
    private var currentPort: Port? = null
    
    /**
     * Автоматично знайти доступний адаптер
     */
    suspend fun detectAdapter(): Port? {
        // Спробувати Bluetooth
        findBluetoothAdapter()?.let { return it }
        
        // Спробувати USB
        findUsbAdapter()?.let { return it }
        
        // Спробувати Wi-Fi
        findWifiAdapter()?.let { return it }
        
        return null
    }
    
    private suspend fun findBluetoothAdapter(): Port? {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter() ?: return null
        val pairedDevices = bluetoothAdapter.bondedDevices
        
        // Шукаємо пристрої з назвами "OBD", "ELM327", etc.
        val obdDevice = pairedDevices.firstOrNull { device ->
            device.name.contains("OBD", ignoreCase = true) ||
            device.name.contains("ELM327", ignoreCase = true)
        }
        
        return obdDevice?.let { BluetoothPort(it) }
    }
    
    private suspend fun findUsbAdapter(): Port? {
        val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
        val availableDrivers = UsbSerialProber.getDefaultProber()
            .findAllDrivers(usbManager)
        
        return availableDrivers.firstOrNull()?.let { driver ->
            UsbSerialPort(driver.device, usbManager)
        }
    }
    
    private suspend fun findWifiAdapter(): Port? {
        // Спробувати підключитися до стандартної IP
        val port = TcpPort()
        return if (port.open()) {
            port.close()
            port
        } else {
            null
        }
    }
    
    /**
     * Підключитися до адаптера
     */
    suspend fun connect(port: Port): Boolean {
        disconnect()  // Закрити попереднє з'єднання
        currentPort = port
        return port.open()
    }
    
    /**
     * Відключитися
     */
    suspend fun disconnect() {
        currentPort?.close()
        currentPort = null
    }
    
    /**
     * Отримати поточний порт
     */
    fun getCurrentPort(): Port? = currentPort
}
```

### Приклад використання:
```kotlin
val manager = ConnectionManager(context)

// Автоматичне виявлення
val port = manager.detectAdapter()
if (port != null) {
    manager.connect(port)
    // Використовувати port для OBD команд
} else {
    // Адаптер не знайдено
}
```

---

## 🧪 Testing

### Mock Port для тестів:
```kotlin
class MockPort : Port {
    private var opened = false
    val writtenData = mutableListOf<String>()
    var responseQueue = mutableListOf<String>()
    
    override suspend fun open(): Boolean {
        opened = true
        return true
    }
    
    override suspend fun close() {
        opened = false
    }
    
    override fun isOpen() = opened
    
    override suspend fun write(data: String): Int {
        writtenData.add(data)
        return data.length
    }
    
    override suspend fun read(timeoutMs: Long): String? {
        return responseQueue.removeFirstOrNull()
    }
    
    override suspend fun flush() {}
    
    override fun getType() = PortType.UNKNOWN
}
```

---

## 🔗 Ресурси / Resources
- [usb-serial-for-android](https://github.com/mik3y/usb-serial-for-android)
- [Android Bluetooth Guide](https://developer.android.com/guide/topics/connectivity/bluetooth)
- [ELM327 Datasheet](https://www.elmelectronics.com/wp-content/uploads/2017/01/ELM327DS.pdf)

---

**Maintained by:** Transport Agent / RepoBuilder 🤖  
**Tech Stack:** Kotlin + Coroutines + Android APIs  
**Last Updated:** 2025 🚦⚡  
**Supported:** Bluetooth RFCOMM, USB OTG, Wi-Fi TCP 🔌

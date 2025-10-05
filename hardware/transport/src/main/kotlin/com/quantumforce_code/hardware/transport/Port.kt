// 1. File Purpose: Generic transport port abstraction
// 2. Role: Defines contract for hardware communication channels
// 3. Принципи: Interface Segregation, Dependency Inversion

package com.quantumforce_code.hardware.transport

/**
 * Базовий інтерфейс для всіх типів транспортних каналів.
 * 
 * Призначення: Визначає контракт для комунікації з апаратурою через різні канали.
 * Реалізації: BluetoothPort, UsbSerialPort, TcpPort
 * 
 * Принципи:
 * - Interface Segregation: інтерфейс містить тільки базові операції
 * - Dependency Inversion: верхні шари залежать від цього інтерфейсу, а не від конкретних реалізацій
 * 
 * Приклад використання:
 * ```
 * val port: Port = BluetoothPort(device, context)
 * val result = port.open()
 * if (result.isSuccess) {
 *     port.write("ATZ\r".toByteArray())
 *     val response = port.read(timeout = 1000)
 * }
 * ```
 */
interface Port {
    /**
     * Відкриває з'єднання з пристроєм.
     * 
     * Ця операція може бути тривалою (до кількох секунд) залежно від типу з'єднання.
     * Рекомендується викликати з coroutine з IO dispatcher.
     * 
     * @return Result.success(true) якщо з'єднання встановлено успішно
     *         Result.failure з помилкою якщо з'єднання неможливе
     */
    suspend fun open(): Result<Boolean>
    
    /**
     * Закриває з'єднання з пристроєм та звільняє ресурси.
     * 
     * Ця операція завжди має виконуватися при завершенні роботи,
     * навіть якщо відбулася помилка під час комунікації.
     * 
     * Рекомендується викликати в блоці try-finally або use().
     */
    suspend fun close()
    
    /**
     * Записує дані у порт.
     * 
     * Дані відправляються синхронно. Для великих обсягів даних може
     * знадобитися розбиття на частини.
     * 
     * @param data байтовий масив для відправлення
     * @return Result.success(true) якщо запис успішний
     *         Result.failure якщо помилка запису
     */
    suspend fun write(data: ByteArray): Result<Boolean>
    
    /**
     * Читає дані з порту з таймаутом.
     * 
     * Блокує виконання до отримання даних або закінчення таймауту.
     * Якщо дані не отримані за час таймауту, повертає порожній масив.
     * 
     * @param timeout час очікування в мілісекундах (за замовчуванням 1000ms)
     * @return Result.success з прочитаними даними (може бути порожнім)
     *         Result.failure якщо помилка читання
     */
    suspend fun read(timeout: Long = 1000): Result<ByteArray>
    
    /**
     * Очищає буфери вводу/виводу.
     * 
     * Видаляє всі дані, що очікують в буферах читання та запису.
     * Корисно перед відправкою нової команди.
     * 
     * @return Result.success якщо успішно
     */
    suspend fun flush(): Result<Unit>
    
    /**
     * Перевіряє, чи активне з'єднання.
     * 
     * @return true якщо порт відкритий і готовий до комунікації
     */
    val isConnected: Boolean
    
    /**
     * Отримує інформацію про порт.
     * 
     * @return метадані про порт (тип, адреса, ім'я)
     */
    val info: PortInfo
    
    /**
     * Метадані про транспортний порт.
     * 
     * @property type тип порту (Bluetooth, USB, TCP)
     * @property address адреса пристрою (MAC, IP, шлях до девайсу)
     * @property name зрозуміле ім'я пристрою
     * @property capabilities додаткові можливості порту
     */
    data class PortInfo(
        val type: PortType,
        val address: String,
        val name: String,
        val capabilities: Set<Capability> = emptySet()
    )
    
    /**
     * Типи транспортних портів.
     */
    enum class PortType {
        BLUETOOTH,      // Bluetooth SPP (Serial Port Profile)
        USB_SERIAL,     // USB з CH340/FTDI/CP2102 чіпами
        TCP,            // TCP/IP (WiFi адаптери)
        MOCK            // Моковий порт для тестування
    }
    
    /**
     * Можливості порту.
     */
    enum class Capability {
        HIGH_SPEED,         // Підтримка високої швидкості передачі
        BIDIRECTIONAL,      // Одночасне читання та запис
        FLOW_CONTROL,       // Апаратний flow control
        AUTO_RECONNECT      // Автоматичне перепід'єднання
    }
}


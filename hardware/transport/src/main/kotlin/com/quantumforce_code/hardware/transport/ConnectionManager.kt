// 1. File Purpose: Connection manager interface
// 2. Role: Coordinates lifecycle of active hardware ports
// 3. Принципи: Facade Pattern, Connection Pooling

package com.quantumforce_code.hardware.transport

import kotlinx.coroutines.flow.StateFlow

/**
 * Інтерфейс для управління з'єднаннями з OBD-II пристроями.
 * 
 * Призначення: Керує життєвим циклом з'єднань, автоматичним перепід'єднанням
 * та сканування доступних пристроїв.
 * 
 * Принципи:
 * - Facade Pattern: спрощує роботу з транспортним шаром
 * - Single Responsibility: відповідає тільки за управління з'єднаннями
 * 
 * Приклад використання:
 * ```
 * val manager = ConnectionManagerImpl(context)
 * val config = ConnectionConfig(
 *     type = Port.PortType.BLUETOOTH,
 *     address = "00:11:22:33:44:55",
 *     baudRate = 38400
 * )
 * 
 * val result = manager.connect(config)
 * result.onSuccess { port ->
 *     // Використовуємо порт для комунікації
 * }
 * ```
 */
interface ConnectionManager {
    /**
     * StateFlow зі станом з'єднання.
     * UI може підписатися на цей flow для відображення статусу.
     */
    val connectionState: StateFlow<ConnectionState>
    
    /**
     * Встановлює з'єднання з пристроєм.
     * 
     * Якщо вже є активне з'єднання, воно буде закрито перед створенням нового.
     * 
     * @param config конфігурація з'єднання
     * @return Result.success з Port якщо з'єднання успішне
     *         Result.failure з помилкою
     */
    suspend fun connect(config: ConnectionConfig): Result<Port>
    
    /**
     * Розриває поточне з'єднання.
     * 
     * Зупиняє автоматичне перепід'єднання якщо воно було активне.
     */
    suspend fun disconnect()
    
    /**
     * Отримує поточний активний порт.
     * 
     * @return Port або null якщо з'єднання відсутнє
     */
    fun getCurrentPort(): Port?
    
    /**
     * Отримує конфігурацію поточного з'єднання.
     * 
     * @return ConnectionConfig або null якщо з'єднання відсутнє
     */
    fun getCurrentConfig(): ConnectionConfig?
    
    /**
     * Перевіряє доступність пристрою без встановлення з'єднання.
     * 
     * Корисно перед спробою підключення для валідації.
     * 
     * @param config конфігурація для перевірки
     * @return true якщо пристрій доступний
     */
    suspend fun isDeviceAvailable(config: ConnectionConfig): Boolean
    
    /**
     * Сканує доступні пристрої всіх типів.
     * 
     * Виконує пошук Bluetooth пристроїв, підключених USB девайсів
     * та доступних WiFi адаптерів.
     * 
     * УВАГА: Може вимагати дозволів (Location, Bluetooth Scan)
     * 
     * @return список знайдених пристроїв
     */
    suspend fun scanDevices(): List<DeviceInfo>
    
    /**
     * Сканує пристрої конкретного типу.
     * 
     * @param type тип пристроїв для пошуку
     * @return список знайдених пристроїв
     */
    suspend fun scanDevices(type: Port.PortType): List<DeviceInfo>
    
    /**
     * Налаштовує автоматичне перепід'єднання при розриві з'єднання.
     * 
     * Якщо з'єднання втрачено, manager автоматично спробує перепід'єднатися.
     * 
     * @param enabled чи увімкнути автоматичне перепід'єднання
     * @param maxAttempts максимальна кількість спроб (за замовчуванням 3)
     * @param retryDelay затримка між спробами в мс (за замовчуванням 2000)
     */
    fun setAutoReconnect(
        enabled: Boolean,
        maxAttempts: Int = 3,
        retryDelay: Long = 2000
    )
    
    /**
     * Перевіряє здоров'я поточного з'єднання.
     * 
     * Відправляє тестову команду та перевіряє відповідь.
     * 
     * @return Result.success якщо з'єднання працює
     *         Result.failure якщо є проблеми
     */
    suspend fun checkConnection(): Result<Boolean>
    
    /**
     * Конфігурація з'єднання.
     * 
     * @property type тип транспортного каналу
     * @property address адреса пристрою
     * @property baudRate швидкість передачі даних (для serial)
     * @property timeout таймаут з'єднання в мілісекундах
     * @property name зрозуміле ім'я (optional)
     */
    data class ConnectionConfig(
        val type: Port.PortType,
        val address: String,
        val baudRate: Int = 38400,
        val timeout: Long = 5000,
        val name: String? = null
    ) {
        /**
         * Валідує конфігурацію.
         * 
         * @return true якщо всі параметри коректні
         */
        fun isValid(): Boolean {
            if (address.isEmpty()) return false
            if (baudRate !in 9600..115200) return false
            if (timeout < 1000) return false
            return true
        }
    }
    
    /**
     * Інформація про знайдений пристрій.
     * 
     * @property name ім'я пристрою (може бути порожнім)
     * @property address адреса пристрою
     * @property type тип транспорту
     * @property isAvailable чи доступний зараз для підключення
     * @property signalStrength сила сигналу (для Bluetooth/WiFi), 0-100
     * @property isPaired чи спарений пристрій (для Bluetooth)
     */
    data class DeviceInfo(
        val name: String,
        val address: String,
        val type: Port.PortType,
        val isAvailable: Boolean,
        val signalStrength: Int? = null,
        val isPaired: Boolean = false
    ) {
        /**
         * Конвертує DeviceInfo в ConnectionConfig.
         */
        fun toConfig(baudRate: Int = 38400, timeout: Long = 5000): ConnectionConfig {
            return ConnectionConfig(
                type = type,
                address = address,
                baudRate = baudRate,
                timeout = timeout,
                name = name.ifEmpty { null }
            )
        }
    }
    
    /**
     * Стани з'єднання.
     */
    sealed class ConnectionState {
        /** З'єднання відсутнє */
        object Disconnected : ConnectionState()
        
        /** Встановлення з'єднання */
        data class Connecting(val deviceName: String) : ConnectionState()
        
        /** З'єднання активне */
        data class Connected(val deviceName: String, val port: Port) : ConnectionState()
        
        /** Помилка з'єднання */
        data class Error(val message: String, val cause: Throwable? = null) : ConnectionState()
        
        /** Перепід'єднання після розриву */
        data class Reconnecting(val attempt: Int, val maxAttempts: Int) : ConnectionState()
    }
}

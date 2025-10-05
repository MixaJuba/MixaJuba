// 1. File Purpose: OBD interface abstraction
// 2. Role: Defines contract for OBD-II command execution and diagnostics
// 3. Принципи: Command Pattern, Strategy Pattern

package com.quantumforce_code.protocols.obd

/**
 * Інтерфейс для роботи з OBD-II пристроями та адаптерами.
 * 
 * Призначення: Визначає операції для діагностики через OBD-II протокол.
 * Реалізації: Elm327Adapter, KWP2000Adapter, UDSAdapter
 * 
 * Принципи:
 * - Command Pattern: команди інкапсульовані в ObdCommand об'єкти
 * - Strategy Pattern: різні реалізації для різних адаптерів
 * 
 * Приклад використання:
 * ```
 * val obdInterface: ObdInterface = Elm327Adapter(port)
 * obdInterface.initialize().onSuccess {
 *     val response = obdInterface.sendCommand(ObdCommand.Mode01.EngineRPM)
 *     response.onSuccess { data ->
 *         println("RPM: ${data.parsedValue}")
 *     }
 * }
 * ```
 */
interface ObdInterface {
    /**
     * Ініціалізує з'єднання з OBD-II адаптером.
     * 
     * Виконує:
     * - Скидання адаптера (ATZ)
     * - Налаштування параметрів (echo off, headers, тощо)
     * - Визначення протоколу автомобіля
     * - Перевірку зв'язку з ECU
     * 
     * ВАЖЛИВО: Має бути викликаний перед будь-якими іншими операціями.
     * 
     * @return Result.success(true) якщо ініціалізація успішна
     *         Result.failure з описом помилки
     */
    suspend fun initialize(): Result<Boolean>
    
    /**
     * Відправляє команду OBD-II та отримує відповідь.
     * 
     * Автоматично:
     * - Додає завершальні символи (\r)
     * - Очікує відповідь з таймаутом
     * - Парсить відповідь
     * - Обробляє помилки (NO DATA, ERROR тощо)
     * 
     * @param command команда для відправлення
     * @return Result.success з ObdResponse
     *         Result.failure якщо помилка комунікації
     */
    suspend fun sendCommand(command: ObdCommand): Result<ObdResponse>
    
    /**
     * Читає DTC (Diagnostic Trouble Codes) з автомобіля.
     * 
     * Виконує Mode 03 (Read Stored DTCs) та парсить коди.
     * 
     * @return Result.success зі списком кодів (може бути порожнім)
     *         Result.failure якщо помилка читання
     */
    suspend fun readDtcCodes(): Result<List<String>>
    
    /**
     * Читає Pending DTC коди (тимчасові, не підтверджені).
     * 
     * Виконує Mode 07 (Read Pending DTCs).
     * Ці коди можуть зникнути, якщо проблема не повториться.
     * 
     * @return Result.success зі списком pending кодів
     */
    suspend fun readPendingDtcCodes(): Result<List<String>>
    
    /**
     * Очищує DTC коди з пам'яті ECU.
     * 
     * Виконує Mode 04 (Clear DTCs and Stored Values).
     * ВАЖЛИВО: Також очищує Freeze Frame дані та результати тестів.
     * 
     * @return Result.success(true) якщо коди очищено
     *         Result.failure якщо операція неможлива
     */
    suspend fun clearDtcCodes(): Result<Boolean>
    
    /**
     * Читає конкретний PID (Parameter ID).
     * 
     * PIDs дозволяють читати реальні параметри двигуна:
     * - Обороти (RPM)
     * - Швидкість
     * - Температура
     * - Навантаження тощо
     * 
     * @param pid ідентифікатор параметра (наприклад, "0C" для RPM)
     * @return Result.success з PidData (значення + одиниці)
     *         Result.failure якщо PID не підтримується
     */
    suspend fun readPid(pid: String): Result<PidData>
    
    /**
     * Читає декілька PIDs одночасно (якщо адаптер підтримує).
     * 
     * Більш ефективно, ніж читати кожен PID окремо.
     * 
     * @param pids список PIDs для читання
     * @return Result.success з мапою PID -> PidData
     */
    suspend fun readMultiplePids(pids: List<String>): Result<Map<String, PidData>>
    
    /**
     * Визначає протокол автомобіля автоматично.
     * 
     * Пробує різні протоколи поки не знайде робочий:
     * - ISO 9141-2
     * - ISO 14230-4 (KWP2000)
     * - ISO 15765-4 (CAN)
     * 
     * @return Result.success з типом протоколу
     *         Result.failure якщо не вдалося визначити
     */
    suspend fun detectProtocol(): Result<ObdProtocol>
    
    /**
     * Встановлює конкретний протокол вручну.
     * 
     * Корисно, якщо автоматичне визначення не працює.
     * 
     * @param protocol протокол для використання
     * @return Result.success якщо протокол встановлено
     */
    suspend fun setProtocol(protocol: ObdProtocol): Result<Unit>
    
    /**
     * Перевіряє, чи адаптер підключений та працює.
     * 
     * @return true якщо з'єднання активне
     */
    fun isConnected(): Boolean
    
    /**
     * Отримує інформацію про OBD-II адаптер.
     * 
     * Включає:
     * - Тип адаптера (ELM327, OBDLink тощо)
     * - Версію firmware
     * - Підтримувані протоколи
     * - Поточний протокол
     * 
     * @return Result.success з AdapterInfo
     */
    suspend fun getAdapterInfo(): Result<AdapterInfo>
    
    /**
     * Читає VIN (Vehicle Identification Number) автомобіля.
     * 
     * Виконує Mode 09, PID 02.
     * Не всі автомобілі підтримують цю функцію через OBD-II.
     * 
     * @return Result.success з VIN номером (17 символів)
     *         Result.failure якщо не підтримується
     */
    suspend fun readVin(): Result<String>
    
    /**
     * Читає Freeze Frame дані для конкретного DTC.
     * 
     * Freeze Frame - знімок параметрів у момент виникнення помилки.
     * 
     * @param dtcCode код помилки (наприклад, "P0301")
     * @return Result.success з FreezeFrameData
     */
    suspend fun readFreezeFrame(dtcCode: String): Result<FreezeFrameData>
    
    /**
     * Закриває з'єднання з адаптером.
     * 
     * Завжди викликайте при завершенні роботи.
     */
    suspend fun close()
    
    /**
     * Дані параметра (PID).
     */
    data class PidData(
        val pid: String,
        val value: Any,
        val unit: String,
        val name: String,
        val timestamp: Long = System.currentTimeMillis()
    )
    
    /**
     * Інформація про адаптер.
     */
    data class AdapterInfo(
        val type: String,           // "ELM327", "OBDLink", тощо
        val version: String,        // "v1.5", "v2.1"
        val protocols: List<ObdProtocol>,
        val currentProtocol: ObdProtocol?
    )
    
    /**
     * Freeze Frame дані.
     */
    data class FreezeFrameData(
        val dtcCode: String,
        val engineSpeed: Int?,
        val vehicleSpeed: Int?,
        val coolantTemp: Int?,
        val loadValue: Float?,
        val fuelPressure: Float?,
        val throttlePosition: Float?
    )
}

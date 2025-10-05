// 1. File Purpose: OBD response model
// 2. Role: Encapsulates OBD command response data
// 3. Принципи: Immutability, Type Safety

package com.quantumforce_code.protocols.obd

/**
 * Модель відповіді від OBD-II пристрою.
 * 
 * Призначення: Інкапсулює відповідь на OBD команду з усіма метаданими.
 * Принципи: Value Object - незмінний об'єкт з даними відповіді
 * 
 * Використання:
 * ```
 * val response = obdInterface.sendCommand(ObdCommand.Mode01.EngineRPM)
 * if (response.isSuccess()) {
 *     val rpm = response.asInt()
 *     println("Engine RPM: $rpm")
 * }
 * ```
 */
data class ObdResponse(
    val rawData: String,
    val status: Status,
    val parsedValue: Any? = null,
    val unit: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val command: ObdCommand? = null
) {
    /**
     * Статуси відповіді OBD.
     */
    enum class Status {
        /** Команда виконана успішно, дані отримано */
        SUCCESS,
        
        /** Помилка виконання команди */
        ERROR,
        
        /** Немає даних від ECU (NO DATA) */
        NO_DATA,
        
        /** Таймаут очікування відповіді */
        TIMEOUT,
        
        /** Команда не підтримується ECU */
        UNSUPPORTED,
        
        /** Некоректний формат відповіді */
        INVALID_FORMAT,
        
        /** CAN ERROR - помилка CAN шини */
        CAN_ERROR,
        
        /** BUS BUSY - шина зайнята */
        BUS_BUSY,
        
        /** BUFFER FULL - буфер адаптера переповнений */
        BUFFER_FULL,
        
        /** STOPPED - команда перервана */
        STOPPED
    }
    
    /**
     * Перевіряє, чи виконана команда успішно.
     * 
     * @return true якщо status == SUCCESS
     */
    fun isSuccess(): Boolean = status == Status.SUCCESS
    
    /**
     * Перевіряє, чи є помилка.
     * 
     * @return true якщо status не SUCCESS
     */
    fun isError(): Boolean = !isSuccess()
    
    /**
     * Отримує значення як Int.
     * 
     * @return Int значення або null якщо конвертація неможлива
     */
    fun asInt(): Int? {
        return when (parsedValue) {
            is Int -> parsedValue
            is Long -> parsedValue.toInt()
            is Float -> parsedValue.toInt()
            is Double -> parsedValue.toInt()
            is String -> parsedValue.toIntOrNull()
            else -> null
        }
    }
    
    /**
     * Отримує значення як Float.
     * 
     * @return Float значення або null
     */
    fun asFloat(): Float? {
        return when (parsedValue) {
            is Float -> parsedValue
            is Double -> parsedValue.toFloat()
            is Int -> parsedValue.toFloat()
            is Long -> parsedValue.toFloat()
            is String -> parsedValue.toFloatOrNull()
            else -> null
        }
    }
    
    /**
     * Отримує значення як String.
     * 
     * @return String значення або toString() якщо не null
     */
    fun asString(): String? {
        return parsedValue?.toString()
    }
    
    /**
     * Отримує значення як Boolean.
     * 
     * Корисно для бінарних параметрів (ввімкнено/вимкнено).
     * 
     * @return Boolean або null
     */
    fun asBoolean(): Boolean? {
        return when (parsedValue) {
            is Boolean -> parsedValue
            is Int -> parsedValue != 0
            is String -> parsedValue.lowercase() in setOf("true", "1", "yes", "on")
            else -> null
        }
    }
    
    /**
     * Форматує відповідь для відображення.
     * 
     * @return відформатований рядок "value unit" або повідомлення про помилку
     */
    fun formatForDisplay(): String {
        return when {
            isSuccess() && parsedValue != null && unit != null -> 
                "$parsedValue $unit"
            isSuccess() && parsedValue != null -> 
                parsedValue.toString()
            status == Status.NO_DATA -> 
                "No Data"
            status == Status.UNSUPPORTED -> 
                "Not Supported"
            status == Status.TIMEOUT -> 
                "Timeout"
            else -> 
                "Error: ${status.name}"
        }
    }
    
    /**
     * Отримує повідомлення про помилку.
     * 
     * @return опис помилки або null якщо успіх
     */
    fun getErrorMessage(): String? {
        return when (status) {
            Status.SUCCESS -> null
            Status.ERROR -> "OBD Error"
            Status.NO_DATA -> "No data from ECU"
            Status.TIMEOUT -> "Request timeout"
            Status.UNSUPPORTED -> "Command not supported by vehicle"
            Status.INVALID_FORMAT -> "Invalid response format: $rawData"
            Status.CAN_ERROR -> "CAN bus error"
            Status.BUS_BUSY -> "Bus busy, try again"
            Status.BUFFER_FULL -> "Adapter buffer full"
            Status.STOPPED -> "Command stopped"
        }
    }
    
    /**
     * Перевіряє, чи можна повторити команду.
     * Деякі помилки тимчасові (BUS_BUSY, TIMEOUT).
     * 
     * @return true якщо варто повторити спробу
     */
    fun isRetryable(): Boolean {
        return status in listOf(
            Status.TIMEOUT,
            Status.BUS_BUSY,
            Status.BUFFER_FULL
        )
    }
    
    companion object {
        /**
         * Створює успішну відповідь.
         */
        fun success(
            rawData: String,
            parsedValue: Any,
            unit: String? = null,
            command: ObdCommand? = null
        ): ObdResponse {
            return ObdResponse(
                rawData = rawData,
                status = Status.SUCCESS,
                parsedValue = parsedValue,
                unit = unit,
                command = command
            )
        }
        
        /**
         * Створює відповідь з помилкою.
         */
        fun error(
            rawData: String,
            status: Status,
            command: ObdCommand? = null
        ): ObdResponse {
            return ObdResponse(
                rawData = rawData,
                status = status,
                command = command
            )
        }
        
        /**
         * Створює відповідь NO_DATA.
         */
        fun noData(command: ObdCommand? = null): ObdResponse {
            return ObdResponse(
                rawData = "NO DATA",
                status = Status.NO_DATA,
                command = command
            )
        }
    }
}

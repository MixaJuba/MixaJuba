// 1. File Purpose: DTC code entity
// 2. Role: Encapsulates diagnostic trouble code data
// 3. Принципи: Rich Domain Model, Value Object, Immutability

package com.quantumforce_code.core.domain

/**
 * Доменна модель коду помилки (Diagnostic Trouble Code).
 * 
 * Призначення: Представляє код помилки з усією інформацією для діагностики.
 * Принципи: Rich Domain Model - модель містить не лише дані, але й поведінку
 * 
 * @property code Код помилки (наприклад, P0301, C1234)
 * @property description Опис помилки людською мовою
 * @property severity Рівень критичності помилки
 * @property causes Список можливих причин виникнення
 * @property solutions Список рекомендованих рішень
 * @property affectedSystems Системи автомобіля, які задіяні
 * @property timestamp Час виявлення помилки (Unix timestamp)
 * @property freezeFrame Freeze Frame - знімок параметрів у момент помилки
 */
data class DtcCode(
    val code: String,
    val description: String,
    val severity: Severity,
    val causes: List<String>,
    val solutions: List<String>,
    val affectedSystems: List<String> = emptyList(),
    val timestamp: Long? = null,
    val freezeFrame: FreezeFrame? = null
) {
    /**
     * Рівень критичності коду помилки.
     */
    enum class Severity {
        LOW,        // Незначна, не впливає на роботу
        MEDIUM,     // Помірна, може вплинути на продуктивність
        HIGH,       // Серйозна, впливає на роботу
        CRITICAL    // Критична, вимагає негайного ремонту
    }
    
    /**
     * Freeze Frame - знімок параметрів двигуна в момент виникнення помилки.
     * Допомагає встановити умови, за яких виникла проблема.
     */
    data class FreezeFrame(
        val engineSpeed: Int?,      // Обороти двигуна (RPM)
        val vehicleSpeed: Int?,     // Швидкість авто (км/год)
        val coolantTemp: Int?,      // Температура охолоджувальної рідини (°C)
        val loadValue: Float?,      // Навантаження двигуна (%)
        val fuelPressure: Float?,   // Тиск палива (kPa)
        val throttlePosition: Float? // Положення дросельної заслінки (%)
    )
    
    /**
     * Визначає, чи вимагає код негайної уваги механіка.
     * 
     * @return true якщо критичний рівень
     */
    fun requiresImmediateAttention(): Boolean {
        return severity == Severity.CRITICAL
    }
    
    /**
     * Форматує код для відображення в UI.
     * 
     * @return Відформатований рядок з кодом та описом
     */
    fun formatForDisplay(): String {
        return "$code - $description"
    }
    
    /**
     * Перевіряє, чи є код активним (недавно виявленим).
     * Код вважається активним, якщо виявлений менше ніж годину тому.
     * 
     * @return true якщо код активний
     */
    fun isActive(): Boolean {
        if (timestamp == null) return false
        val oneHourAgo = System.currentTimeMillis() - (60 * 60 * 1000)
        return timestamp > oneHourAgo
    }
    
    /**
     * Отримує першу літеру коду, яка вказує на систему:
     * P - Powertrain (силова установка)
     * C - Chassis (шасі)
     * B - Body (кузов)
     * U - Network (мережа)
     */
    fun getSystemType(): SystemType {
        return when (code.firstOrNull()) {
            'P' -> SystemType.POWERTRAIN
            'C' -> SystemType.CHASSIS
            'B' -> SystemType.BODY
            'U' -> SystemType.NETWORK
            else -> SystemType.UNKNOWN
        }
    }
    
    enum class SystemType {
        POWERTRAIN,  // Двигун, трансмісія
        CHASSIS,     // Гальма, підвіска, керування
        BODY,        // Освітлення, сигналізація
        NETWORK,     // CAN шина, комунікація
        UNKNOWN
    }
}

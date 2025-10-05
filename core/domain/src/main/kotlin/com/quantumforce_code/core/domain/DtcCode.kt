// 1. File Purpose: DTC (Diagnostic Trouble Code) domain entity
// 2. Role: Encapsulates diagnostic trouble code data and metadata
// 3. Architecture: Core domain model, immutable data class
// 4. OBD-II Standard: Represents standardized vehicle fault codes (P0xxx, C0xxx, B0xxx, U0xxx)
// 5. Fields:
//    - code: 5-character alphanumeric (e.g., "P0420", "C0123")
//    - description: Human-readable fault description
//    - severity: Criticality level for prioritization
//    - causes: List of possible causes for this DTC
//    - solutions: List of recommended fixes
// 6. Usage: Displayed in UI, stored in database, parsed from OBD-II responses
// 7. Related: DtcParser.kt (creates instances), DtcRepository (stores)

package com.quantumforce_code.core.domain

/**
 * Представляє Diagnostic Trouble Code (DTC) згідно стандарту OBD-II.
 * 
 * DTC - це стандартизовані коди несправностей, які генерує ECU автомобіля
 * при виявленні проблем з системами двигуна, трансмісії, викидів, тощо.
 * 
 * **Формат Коду:**
 * - **1-ша літера**: Система
 *   - P = Powertrain (двигун, трансмісія)
 *   - C = Chassis (ABS, підвіска)
 *   - B = Body (подушки безпеки, центральний замок)
 *   - U = Network (шина CAN, комунікації)
 * - **2-га цифра**: Тип коду
 *   - 0 = Generic (SAE стандарт)
 *   - 1/2/3 = Manufacturer specific
 * - **3-5 цифри**: Специфічний код несправності
 * 
 * **Приклади:**
 * - P0420: Catalyst System Efficiency Below Threshold
 * - P0300: Random/Multiple Cylinder Misfire Detected
 * - C0035: Left Front Wheel Speed Sensor Circuit
 * 
 * @property code 5-символьний код (наприклад, "P0420")
 * @property description Опис несправності українською/англійською
 * @property severity Рівень критичності для UI пріоритизації
 * @property causes Список можливих причин цієї несправності
 * @property solutions Рекомендовані дії для усунення
 * 
 * @see Severity Enum критичності DTC
 * @see [OBD-II DTC Format](https://en.wikipedia.org/wiki/OBD-II_PIDs#Mode_03)
 */
data class DtcCode(
    val code: String,              // P0420, C0123, etc.
    val description: String,        // "Catalyst System Efficiency Below Threshold"
    val severity: Severity,         // LOW, MEDIUM, HIGH, CRITICAL
    val causes: List<String>,       // ["Bad catalytic converter", "Faulty O2 sensor", ...]
    val solutions: List<String>     // ["Replace catalyst", "Check O2 sensor wiring", ...]
) {
    /**
     * Перевіряє чи це generic (SAE) код чи manufacturer-specific.
     */
    val isGeneric: Boolean
        get() = code.length >= 2 && code[1] == '0'
    
    /**
     * Повертає категорію системи (Powertrain, Chassis, Body, Network).
     */
    val system: String
        get() = when (code.firstOrNull()) {
            'P' -> "Powertrain"
            'C' -> "Chassis"
            'B' -> "Body"
            'U' -> "Network"
            else -> "Unknown"
        }
}

/**
 * Рівень критичності DTC коду для UI індикації та пріоритизації.
 * 
 * **Використання:**
 * - LOW: Зеленого кольору в UI, можна продовжувати їздити
 * - MEDIUM: Жовтого кольору, потребує уваги найближчим часом
 * - HIGH: Помаранчевого кольору, негайно перевірити
 * - CRITICAL: Червоного кольору, зупинити автомобіль
 */
enum class Severity {
    /** Низька критичність - інформаційний код */
    LOW,
    
    /** Середня критичність - потребує уваги */
    MEDIUM,
    
    /** Висока критичність - негайна перевірка */
    HIGH,
    
    /** Критичний рівень - небезпечно продовжувати */
    CRITICAL
}

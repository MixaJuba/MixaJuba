// 1. File Purpose: Vehicle entity model
// 2. Role: Represents vehicle metadata in diagnostics domain
// 3. Принципи: Value Object, Immutability, Rich Domain Model

package com.quantumforce_code.core.domain

/**
 * Доменна модель автомобіля.
 * 
 * Призначення: Представляє автомобіль у бізнес-логіці додатку.
 * Принципи: Value Object - незмінний об'єкт, що представляє концепцію автомобіля
 * 
 * @property id Унікальний ідентифікатор
 * @property make Виробник (VW, BMW, Mercedes, Toyota тощо)
 * @property model Модель автомобіля (Golf, 320i, C-Class)
 * @property year Рік випуску
 * @property vin VIN номер (Vehicle Identification Number) - 17 символів
 * @property protocol Протокол діагностики, який підтримує автомобіль
 * @property ecuAddresses Список адрес ECU (Electronic Control Units) для діагностики
 * @property engineType Тип двигуна (бензин, дизель, гібрид, електро)
 * @property transmissionType Тип коробки передач
 */
data class Vehicle(
    val id: String,
    val make: String,
    val model: String,
    val year: Int,
    val vin: String,
    val protocol: Protocol = Protocol.OBD2_ISO15765,
    val ecuAddresses: List<String> = emptyList(),
    val engineType: EngineType = EngineType.GASOLINE,
    val transmissionType: TransmissionType = TransmissionType.AUTOMATIC
) {
    /**
     * Протоколи діагностики автомобілів.
     */
    enum class Protocol(val description: String) {
        OBD2_ISO9141("ISO 9141-2 (5 baud init)"),
        OBD2_ISO14230("ISO 14230-4 KWP (fast init)"),
        OBD2_ISO15765("ISO 15765-4 CAN (Controller Area Network)"),
        CAN_BUS("CAN Bus (High Speed)"),
        KWP2000("KWP2000 (Keyword Protocol 2000)"),
        UDS("UDS (Unified Diagnostic Services - ISO 14229)"),
        VAG_SPECIFIC("VAG Specific Protocol"),
        BMW_SPECIFIC("BMW Specific Protocol"),
        MERCEDES_SPECIFIC("Mercedes-Benz Specific Protocol"),
        MANUFACTURER_SPECIFIC("Other Manufacturer Specific");
        
        /**
         * Перевіряє, чи є протокол стандартним OBD-II.
         */
        fun isStandardObd(): Boolean {
            return this in listOf(
                OBD2_ISO9141,
                OBD2_ISO14230,
                OBD2_ISO15765
            )
        }
    }
    
    /**
     * Типи двигунів.
     */
    enum class EngineType {
        GASOLINE,       // Бензиновий
        DIESEL,         // Дизельний
        HYBRID,         // Гібридний
        ELECTRIC,       // Електричний
        NATURAL_GAS,    // Природний газ
        LPG,            // Скраплений газ
        UNKNOWN
    }
    
    /**
     * Типи коробок передач.
     */
    enum class TransmissionType {
        MANUAL,         // Механічна
        AUTOMATIC,      // Автоматична
        CVT,            // Варіатор
        DCT,            // Роботизована з подвійним зчепленням
        UNKNOWN
    }
    
    /**
     * Перевіряє, чи підтримує автомобіль розширену діагностику.
     * Розширена діагностика включає кодування, програмування, адаптації.
     * 
     * @return true якщо підтримує
     */
    fun supportsAdvancedDiagnostics(): Boolean {
        return protocol in listOf(
            Protocol.KWP2000,
            Protocol.UDS,
            Protocol.VAG_SPECIFIC,
            Protocol.BMW_SPECIFIC,
            Protocol.MERCEDES_SPECIFIC,
            Protocol.MANUFACTURER_SPECIFIC
        )
    }
    
    /**
     * Перевіряє валідність VIN номера.
     * VIN має містити 17 символів (літери та цифри, без I, O, Q).
     * 
     * @return true якщо VIN валідний
     */
    fun hasValidVin(): Boolean {
        if (vin.length != 17) return false
        
        val invalidChars = setOf('I', 'O', 'Q')
        return vin.all { it.isLetterOrDigit() && it.uppercaseChar() !in invalidChars }
    }
    
    /**
     * Отримує повне ім'я автомобіля для відображення.
     * 
     * @return відформатований рядок "Make Model (Year)"
     */
    fun getDisplayName(): String {
        return "$make $model ($year)"
    }
    
    /**
     * Перевіряє, чи є автомобіль сучасним (підтримує CAN).
     * CAN Bus використовується у автомобілях після 2008 року (в більшості країн).
     * 
     * @return true якщо автомобіль підтримує CAN
     */
    fun isModernVehicle(): Boolean {
        return protocol in listOf(
            Protocol.OBD2_ISO15765,
            Protocol.CAN_BUS,
            Protocol.UDS
        ) || year >= 2008
    }
    
    /**
     * Перевіряє, чи потребує автомобіль спеціалізованого обладнання.
     * 
     * @return true якщо потрібен спеціальний адаптер
     */
    fun requiresSpecializedAdapter(): Boolean {
        return protocol in listOf(
            Protocol.VAG_SPECIFIC,
            Protocol.BMW_SPECIFIC,
            Protocol.MERCEDES_SPECIFIC,
            Protocol.MANUFACTURER_SPECIFIC
        )
    }
}

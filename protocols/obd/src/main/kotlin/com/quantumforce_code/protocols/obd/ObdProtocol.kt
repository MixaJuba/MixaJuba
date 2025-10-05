// 1. File Purpose: OBD protocol definitions
// 2. Role: Enumerates supported OBD-II protocols
// 3. Принципи: Type Safety, Enumeration Pattern

package com.quantumforce_code.protocols.obd

/**
 * Enum класс для OBD-II протоколів.
 * 
 * Призначення: Визначає всі підтримувані протоколи діагностики.
 * Кожен протокол має унікальний ідентифікатор та швидкість передачі.
 * 
 * Використання:
 * ```
 * val protocol = ObdProtocol.ISO_15765_4_CAN
 * println(protocol.description)  // "ISO 15765-4 CAN (11 bit ID, 500 kbaud)"
 * println(protocol.baudRate)     // 500000
 * ```
 */
enum class ObdProtocol(
    val identifier: String,
    val description: String,
    val baudRate: Int,
    val isCanBased: Boolean = false
) {
    /**
     * SAE J1850 PWM (Pulse Width Modulation)
     * Використовується: Ford (старі моделі до 2003)
     */
    J1850_PWM(
        identifier = "1",
        description = "SAE J1850 PWM (41.6 kbaud)",
        baudRate = 41600
    ),
    
    /**
     * SAE J1850 VPW (Variable Pulse Width)
     * Використовується: GM, Chrysler (старі моделі)
     */
    J1850_VPW(
        identifier = "2",
        description = "SAE J1850 VPW (10.4 kbaud)",
        baudRate = 10400
    ),
    
    /**
     * ISO 9141-2 (5 baud init, 10.4 kbaud)
     * Використовується: Азіатські авто (старі моделі)
     */
    ISO_9141_2(
        identifier = "3",
        description = "ISO 9141-2 (5 baud init, 10.4 kbaud)",
        baudRate = 10400
    ),
    
    /**
     * ISO 14230-4 KWP (5 baud init, 10.4 kbaud)
     * Використовується: Європейські авто (старі моделі)
     */
    ISO_14230_4_KWP_5BAUD(
        identifier = "4",
        description = "ISO 14230-4 KWP (5 baud init, 10.4 kbaud)",
        baudRate = 10400
    ),
    
    /**
     * ISO 14230-4 KWP (fast init, 10.4 kbaud)
     * Використовується: Європейські авто
     */
    ISO_14230_4_KWP_FAST(
        identifier = "5",
        description = "ISO 14230-4 KWP (fast init, 10.4 kbaud)",
        baudRate = 10400
    ),
    
    /**
     * ISO 15765-4 CAN (11 bit ID, 500 kbaud)
     * Використовується: Більшість сучасних авто з 2008 року
     * НАЙПОШИРЕНІШИЙ протокол
     */
    ISO_15765_4_CAN_11BIT_500K(
        identifier = "6",
        description = "ISO 15765-4 CAN (11 bit ID, 500 kbaud)",
        baudRate = 500000,
        isCanBased = true
    ),
    
    /**
     * ISO 15765-4 CAN (29 bit ID, 500 kbaud)
     * Використовується: Деякі сучасні авто
     */
    ISO_15765_4_CAN_29BIT_500K(
        identifier = "7",
        description = "ISO 15765-4 CAN (29 bit ID, 500 kbaud)",
        baudRate = 500000,
        isCanBased = true
    ),
    
    /**
     * ISO 15765-4 CAN (11 bit ID, 250 kbaud)
     * Використовується: Деякі європейські авто
     */
    ISO_15765_4_CAN_11BIT_250K(
        identifier = "8",
        description = "ISO 15765-4 CAN (11 bit ID, 250 kbaud)",
        baudRate = 250000,
        isCanBased = true
    ),
    
    /**
     * ISO 15765-4 CAN (29 bit ID, 250 kbaud)
     */
    ISO_15765_4_CAN_29BIT_250K(
        identifier = "9",
        description = "ISO 15765-4 CAN (29 bit ID, 250 kbaud)",
        baudRate = 250000,
        isCanBased = true
    ),
    
    /**
     * SAE J1939 CAN (250 kbaud)
     * Використовується: Вантажівки, автобуси
     */
    J1939_CAN(
        identifier = "A",
        description = "SAE J1939 CAN (29 bit ID, 250 kbaud)",
        baudRate = 250000,
        isCanBased = true
    ),
    
    /**
     * Автоматичне визначення протоколу
     */
    AUTO(
        identifier = "0",
        description = "Automatic protocol detection",
        baudRate = 0
    );
    
    /**
     * Перевіряє, чи є протокол стандартним OBD-II.
     * 
     * @return true для стандартних протоколів (ISO 15765-4)
     */
    fun isStandardObd2(): Boolean {
        return this in listOf(
            ISO_15765_4_CAN_11BIT_500K,
            ISO_15765_4_CAN_29BIT_500K,
            ISO_15765_4_CAN_11BIT_250K,
            ISO_15765_4_CAN_29BIT_250K
        )
    }
    
    /**
     * Перевіряє, чи підтримує протокол extended addressing (29-bit).
     */
    fun supportsExtendedAddressing(): Boolean {
        return this in listOf(
            ISO_15765_4_CAN_29BIT_500K,
            ISO_15765_4_CAN_29BIT_250K,
            J1939_CAN
        )
    }
    
    companion object {
        /**
         * Знаходить протокол за ідентифікатором.
         * 
         * @param id ідентифікатор протоколу ("6", "7" тощо)
         * @return ObdProtocol або null якщо не знайдено
         */
        fun fromIdentifier(id: String): ObdProtocol? {
            return values().find { it.identifier == id }
        }
        
        /**
         * Отримує список CAN-based протоколів.
         */
        fun getCanProtocols(): List<ObdProtocol> {
            return values().filter { it.isCanBased }
        }
        
        /**
         * Рекомендований протокол для сучасних авто.
         */
        fun getRecommendedForModernVehicles(): ObdProtocol {
            return ISO_15765_4_CAN_11BIT_500K
        }
    }
}

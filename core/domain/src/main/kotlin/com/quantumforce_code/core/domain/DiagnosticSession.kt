// 1. File Purpose: Diagnostic session aggregate root
// 2. Role: Tracks lifecycle of a single diagnostics interaction with vehicle
// 3. Architecture: Aggregate in DDD terms - groups related domain objects
// 4. Lifecycle: Created → ACTIVE → (read DTCs, live data) → COMPLETED/FAILED
// 5. Persistence: Saved to Room database for history/reports
// 6. Business Logic:
//    - Captures when diagnostics started/ended
//    - Stores all DTC codes found during session
//    - Links to specific vehicle
//    - Enables session replay and comparison
// 7. Related: Vehicle.kt, DtcCode.kt, DiagnosticSessionRepository

package com.quantumforce_code.core.domain

/**
 * Представляє одну сесію діагностики автомобіля.
 * 
 * Сесія починається коли користувач підключається до автомобіля та
 * ініціює діагностику. Вона зберігає всю інформацію про цю взаємодію:
 * - Який автомобіль діагностувався
 * - Коли почалась і закінчилась сесія
 * - Які DTC коди були знайдені
 * - Статус завершення (успішно/помилка)
 * 
 * **Сценарії використання:**
 * 1. **Поточна діагностика**: ACTIVE сесія, користувач читає DTC
 * 2. **Історія**: COMPLETED сесії для аналізу трендів
 * 3. **Звіти**: Експорт сесії в PDF/CSV
 * 4. **Порівняння**: До/після ремонту
 * 
 * **Lifecycle:**
 * ```
 * 1. User connects to vehicle → Session created (status = ACTIVE)
 * 2. Read DTC codes → dtcCodes list populated
 * 3. User disconnects → endTime set, status = COMPLETED
 * 4. Session saved to database for history
 * ```
 * 
 * @property id Унікальний ідентифікатор сесії (UUID)
 * @property vehicle Автомобіль, який діагностувався
 * @property startTime Timestamp початку (System.currentTimeMillis)
 * @property endTime Timestamp завершення (nullable, якщо ACTIVE)
 * @property dtcCodes Список знайдених DTC кодів
 * @property status Поточний статус сесії
 * 
 * @see Vehicle Інформація про автомобіль
 * @see DtcCode Діагностичні коди в сесії
 * @see SessionStatus Можливі статуси
 */
data class DiagnosticSession(
    val id: String,                                 // UUID
    val vehicle: Vehicle,                           // Який автомобіль
    val startTime: Long,                            // Коли почалась
    val endTime: Long? = null,                      // Коли закінчилась (null якщо ACTIVE)
    val dtcCodes: List<DtcCode> = emptyList(),     // Знайдені коди
    val status: SessionStatus = SessionStatus.ACTIVE // Статус
) {
    /**
     * Обчислює тривалість сесії в мілісекундах.
     * Повертає null якщо сесія ще ACTIVE (endTime == null).
     */
    val duration: Long?
        get() = endTime?.let { it - startTime }
    
    /**
     * Перевіряє чи знайдено критичні DTC коди.
     */
    val hasCriticalCodes: Boolean
        get() = dtcCodes.any { it.severity == Severity.CRITICAL }
    
    /**
     * Підраховує кількість кодів по рівню критичності.
     */
    fun countBySeverity(severity: Severity): Int =
        dtcCodes.count { it.severity == severity }
}

/**
 * Статус діагностичної сесії.
 * 
 * **Переходи:**
 * - ACTIVE: Початковий стан, сесія в процесі
 * - COMPLETED: Успішно завершена, всі дані збережені
 * - FAILED: Помилка при діагностиці (connection lost, ECU не відповідає)
 */
enum class SessionStatus {
    /** Сесія в процесі, користувач підключений */
    ACTIVE,
    
    /** Сесія успішно завершена */
    COMPLETED,
    
    /** Сесія завершилась з помилкою */
    FAILED
}

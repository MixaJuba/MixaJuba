// 1. File Purpose: Diagnostic session aggregate
// 2. Role: Tracks lifecycle of a single diagnostics interaction
// 3. Принципи: Aggregate Root, State Pattern, Immutability

package com.quantumforce_code.core.domain

/**
 * Доменна модель сесії діагностики.
 * 
 * Призначення: Представляє процес діагностування автомобіля від початку до завершення.
 * Принципи: Aggregate Root - сесія об'єднує всі дані діагностики в одну єдність
 * 
 * @property id Унікальний ідентифікатор сесії
 * @property vehicleId Ідентифікатор автомобіля
 * @property startTime Час початку сесії (Unix timestamp)
 * @property endTime Час завершення сесії (null якщо активна)
 * @property status Поточний статус сесії
 * @property dtcCodes Список знайдених DTC кодів
 * @property liveData Словник Live даних (PID -> значення)
 * @property notes Нотатки користувача про сесію
 * @property connectionType Тип з'єднання, що використовувався
 */
data class DiagnosticSession(
    val id: String,
    val vehicleId: String,
    val startTime: Long,
    val endTime: Long? = null,
    val status: SessionStatus = SessionStatus.ACTIVE,
    val dtcCodes: List<DtcCode> = emptyList(),
    val liveData: Map<String, Any> = emptyMap(),
    val notes: String = "",
    val connectionType: ConnectionType = ConnectionType.BLUETOOTH
) {
    /**
     * Типи з'єднання з автомобілем.
     */
    enum class ConnectionType {
        BLUETOOTH,      // Bluetooth SPP
        WIFI,           // WiFi/IP
        USB,            // USB Serial
        UNKNOWN
    }
    
    /**
     * Завершує сесію з результатами діагностики.
     * 
     * @param dtcCodes список знайдених кодів помилок
     * @return нова сесія зі статусом COMPLETED
     */
    fun complete(dtcCodes: List<DtcCode>): DiagnosticSession {
        return copy(
            endTime = System.currentTimeMillis(),
            status = SessionStatus.COMPLETED,
            dtcCodes = dtcCodes
        )
    }
    
    /**
     * Позначає сесію як невдалу.
     * 
     * @param reason причина невдачі (зберігається в notes)
     * @return нова сесія зі статусом FAILED
     */
    fun fail(reason: String): DiagnosticSession {
        return copy(
            endTime = System.currentTimeMillis(),
            status = SessionStatus.FAILED,
            notes = if (notes.isEmpty()) reason else "$notes\nError: $reason"
        )
    }
    
    /**
     * Скасовує активну сесію.
     * 
     * @return нова сесію зі статусом CANCELLED
     */
    fun cancel(): DiagnosticSession {
        return copy(
            endTime = System.currentTimeMillis(),
            status = SessionStatus.CANCELLED
        )
    }
    
    /**
     * Додає Live дані до сесії.
     * 
     * @param pid ідентифікатор параметра (наприклад, "ENGINE_RPM")
     * @param value значення параметра
     * @return нова сесія з доданими даними
     */
    fun addLiveData(pid: String, value: Any): DiagnosticSession {
        val updatedData = liveData.toMutableMap()
        updatedData[pid] = value
        return copy(liveData = updatedData)
    }
    
    /**
     * Додає нотатку до сесії.
     * 
     * @param note текст нотатки
     * @return нова сесія з доданою нотаткою
     */
    fun addNote(note: String): DiagnosticSession {
        val updatedNotes = if (notes.isEmpty()) note else "$notes\n$note"
        return copy(notes = updatedNotes)
    }
    
    /**
     * Перевіряє, чи активна сесія (не завершена).
     * 
     * @return true якщо сесія в процесі
     */
    fun isActive(): Boolean {
        return status == SessionStatus.ACTIVE && endTime == null
    }
    
    /**
     * Перевіряє, чи завершена сесія успішно.
     * 
     * @return true якщо статус COMPLETED
     */
    fun isCompleted(): Boolean {
        return status == SessionStatus.COMPLETED
    }
    
    /**
     * Отримує тривалість сесії в мілісекундах.
     * 
     * @return тривалість або null якщо сесія ще активна
     */
    fun getDuration(): Long? {
        val end = endTime ?: return null
        return end - startTime
    }
    
    /**
     * Отримує тривалість сесії у форматі "MM:SS".
     * 
     * @return відформатований рядок або "Active" для активної сесії
     */
    fun getFormattedDuration(): String {
        val duration = getDuration() ?: return "Active"
        val seconds = (duration / 1000) % 60
        val minutes = (duration / (1000 * 60)) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
    
    /**
     * Перевіряє, чи знайдені критичні помилки.
     * 
     * @return true якщо є коди з рівнем CRITICAL
     */
    fun hasCriticalErrors(): Boolean {
        return dtcCodes.any { it.severity == DtcCode.Severity.CRITICAL }
    }
    
    /**
     * Отримує кількість DTC кодів за рівнем критичності.
     * 
     * @param severity рівень критичності
     * @return кількість кодів
     */
    fun countDtcCodesBySeverity(severity: DtcCode.Severity): Int {
        return dtcCodes.count { it.severity == severity }
    }
}

/**
 * Статуси діагностичної сесії.
 */
enum class SessionStatus {
    ACTIVE,         // Діагностика триває
    COMPLETED,      // Завершена успішно
    FAILED,         // Завершена з помилкою
    CANCELLED       // Скасована користувачем
}

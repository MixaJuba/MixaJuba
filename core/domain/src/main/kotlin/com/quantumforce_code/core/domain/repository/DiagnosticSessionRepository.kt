// 1. File Purpose: Repository interface for diagnostic sessions
// 2. Role: Defines contract for session data access and management
// 3. Принципи: Repository Pattern, Session Management

package com.quantumforce_code.core.domain.repository

import com.quantumforce_code.core.domain.DiagnosticSession

/**
 * Інтерфейс репозиторію для роботи з сесіями діагностики.
 * 
 * Призначення: Управляє життєвим циклом діагностичних сесій та їх історією.
 * Сесія представляє окремий процес діагностування автомобіля від початку до завершення.
 */
interface DiagnosticSessionRepository {
    /**
     * Створює нову сесію діагностики для автомобіля.
     * 
     * @param vehicleId ідентифікатор автомобіля
     * @return новостворена сесія зі статусом IN_PROGRESS
     */
    suspend fun createSession(vehicleId: String): DiagnosticSession
    
    /**
     * Оновлює існуючу сесію (наприклад, додає DTC коди або Live дані).
     * 
     * @param session сесія з оновленими даними
     * @return Result.success якщо успішно
     */
    suspend fun updateSession(session: DiagnosticSession): Result<Unit>
    
    /**
     * Завершує активну сесію з результатами.
     * 
     * @param sessionId ідентифікатор сесії
     * @param dtcCodes знайдені коди помилок
     * @return завершена сесія
     */
    suspend fun completeSession(
        sessionId: String,
        dtcCodes: List<com.quantumforce_code.core.domain.DtcCode>
    ): DiagnosticSession
    
    /**
     * Скасовує активну сесію.
     * 
     * @param sessionId ідентифікатор сесії
     * @return скасована сесія
     */
    suspend fun cancelSession(sessionId: String): DiagnosticSession
    
    /**
     * Отримує поточну активну сесію.
     * 
     * @return DiagnosticSession або null якщо немає активної сесії
     */
    suspend fun getActiveSession(): DiagnosticSession?
    
    /**
     * Отримує сесію за її ідентифікатором.
     * 
     * @param sessionId ідентифікатор сесії
     * @return DiagnosticSession або null якщо не знайдено
     */
    suspend fun getSessionById(sessionId: String): DiagnosticSession?
    
    /**
     * Отримує історію сесій для конкретного автомобіля.
     * 
     * @param vehicleId ідентифікатор автомобіля
     * @param limit максимальна кількість сесій для повернення
     * @return список сесій, відсортований за датою (нові спочатку)
     */
    suspend fun getSessionHistory(
        vehicleId: String,
        limit: Int = 50
    ): List<DiagnosticSession>
    
    /**
     * Отримує останню завершену сесію для автомобіля.
     * 
     * @param vehicleId ідентифікатор автомобіля
     * @return DiagnosticSession або null якщо немає завершених сесій
     */
    suspend fun getLastCompletedSession(vehicleId: String): DiagnosticSession?
    
    /**
     * Видаляє сесію з історії.
     * 
     * @param sessionId ідентифікатор сесії
     * @return Result.success якщо успішно
     */
    suspend fun deleteSession(sessionId: String): Result<Unit>
    
    /**
     * Видаляє всю історію сесій для автомобіля.
     * 
     * @param vehicleId ідентифікатор автомобіля
     * @return Result.success якщо успішно
     */
    suspend fun deleteAllSessions(vehicleId: String): Result<Unit>
    
    /**
     * Отримує статистику по сесіях автомобіля.
     * 
     * @param vehicleId ідентифікатор автомобіля
     * @return статистичні дані
     */
    suspend fun getSessionStatistics(vehicleId: String): SessionStatistics
    
    /**
     * Статистика діагностичних сесій.
     */
    data class SessionStatistics(
        val totalSessions: Int,
        val completedSessions: Int,
        val failedSessions: Int,
        val cancelledSessions: Int,
        val totalDtcCodesFound: Int,
        val lastSessionDate: Long?
    )
}

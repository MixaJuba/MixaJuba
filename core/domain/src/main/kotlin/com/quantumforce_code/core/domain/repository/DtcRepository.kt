// 1. File Purpose: Repository interface for DTC codes
// 2. Role: Defines contract for DTC data access
// 3. Принципи: Repository Pattern, Interface Segregation, Dependency Inversion

package com.quantumforce_code.core.domain.repository

import com.quantumforce_code.core.domain.DtcCode

/**
 * Інтерфейс репозиторію для роботи з DTC кодами.
 * 
 * Призначення: Визначає операції для доступу до кодів помилок.
 * Цей інтерфейс знаходиться в domain шарі, а реалізація - в data шарі.
 * Це забезпечує Dependency Inversion - domain не залежить від implementation деталей.
 * 
 * Принципи:
 * - Repository Pattern: абстракція доступу до даних
 * - Interface Segregation: інтерфейс містить тільки необхідні методи
 * - Dependency Inversion: залежність спрямована від data до domain
 */
interface DtcRepository {
    /**
     * Отримує всі DTC коди для конкретного автомобіля.
     * 
     * @param vehicleId ідентифікатор автомобіля
     * @param includeCleared чи включати очищені коди в результат
     * @return список кодів помилок або порожній список
     * 
     * Приклад використання:
     * ```
     * val codes = dtcRepository.getDtcCodes("vehicle-123", includeCleared = false)
     * ```
     */
    suspend fun getDtcCodes(
        vehicleId: String,
        includeCleared: Boolean = false
    ): List<DtcCode>
    
    /**
     * Отримує DTC код за його кодом (наприклад, "P0301").
     * 
     * @param code код помилки
     * @return DtcCode або null якщо не знайдено
     */
    suspend fun getDtcByCode(code: String): DtcCode?
    
    /**
     * Зберігає DTC код у сховище.
     * 
     * @param dtcCode код для збереження
     * @return Result.success якщо успішно, Result.failure з помилкою
     */
    suspend fun saveDtcCode(dtcCode: DtcCode): Result<Unit>
    
    /**
     * Зберігає список DTC кодів.
     * 
     * @param dtcCodes список кодів для збереження
     * @return Result.success з кількістю збережених кодів
     */
    suspend fun saveDtcCodes(dtcCodes: List<DtcCode>): Result<Int>
    
    /**
     * Видаляє всі DTC коди для конкретного автомобіля.
     * Ця операція еквівалентна "Clear DTC" в OBD-II.
     * 
     * @param vehicleId ідентифікатор автомобіля
     * @return Result.success якщо успішно
     */
    suspend fun clearDtcCodes(vehicleId: String): Result<Unit>
    
    /**
     * Позначає DTC код як очищений (не видаляє з історії).
     * 
     * @param code код для позначення
     * @return Result.success якщо успішно
     */
    suspend fun markDtcAsCleared(code: String): Result<Unit>
    
    /**
     * Шукає DTC коди за ключовим словом в коді або описі.
     * 
     * @param query пошуковий запит
     * @return список знайдених кодів
     * 
     * Приклад:
     * ```
     * val results = dtcRepository.searchDtcCodes("misfire") 
     * // Знайде всі коди з "misfire" в описі
     * ```
     */
    suspend fun searchDtcCodes(query: String): List<DtcCode>
    
    /**
     * Отримує кількість активних DTC кодів для автомобіля.
     * 
     * @param vehicleId ідентифікатор автомобіля
     * @return кількість активних кодів
     */
    suspend fun getActiveDtcCount(vehicleId: String): Int
    
    /**
     * Отримує критичні DTC коди для автомобіля.
     * 
     * @param vehicleId ідентифікатор автомобіля
     * @return список критичних кодів
     */
    suspend fun getCriticalDtcCodes(vehicleId: String): List<DtcCode>
}

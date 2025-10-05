// 1. File Purpose: Use case for clearing DTC codes
// 2. Role: Business logic for removing diagnostic trouble codes
// 3. Принципи: Single Responsibility, Command Pattern

package com.quantumforce_code.core.domain.usecase

import com.quantumforce_code.core.domain.UseCase
import com.quantumforce_code.core.domain.repository.DtcRepository

/**
 * Use Case для очищення DTC кодів автомобіля.
 * 
 * Призначення: Видаляє коди помилок з пам'яті ECU та локального сховища.
 * Використання: ViewModel викликає після підтвердження користувача.
 * 
 * ВАЖЛИВО: Ця операція незворотна і видаляє важливу діагностичну інформацію.
 * Завжди попереджайте користувача перед виконанням!
 * 
 * Приклад:
 * ```
 * val useCase = ClearDtcCodesUseCase(dtcRepository)
 * val result = useCase(ClearDtcCodesUseCase.Params("vehicle-123"))
 * result.onSuccess {
 *     // Коди очищено успішно
 * }
 * ```
 * 
 * @param dtcRepository репозиторій для доступу до DTC даних
 */
class ClearDtcCodesUseCase(
    private val dtcRepository: DtcRepository
) : UseCase<ClearDtcCodesUseCase.Params, Unit>() {
    
    /**
     * Параметри для виконання use case.
     * 
     * @property vehicleId ідентифікатор автомобіля
     * @property saveToHistory чи зберегти копію в історію перед видаленням
     */
    data class Params(
        val vehicleId: String,
        val saveToHistory: Boolean = true
    )
    
    /**
     * Виконує очищення DTC кодів.
     * 
     * Логіка:
     * 1. Якщо saveToHistory = true, зберегти коди перед видаленням
     * 2. Видалити всі активні DTC коди
     * 3. Відправити команду Clear DTC до адаптера OBD (через repository)
     * 
     * @param parameters параметри операції
     * @return Result.success якщо успішно, Result.failure з помилкою
     */
    override suspend fun execute(parameters: Params): Result<Unit> {
        return try {
            if (parameters.saveToHistory) {
                // Отримуємо поточні коди перед очищенням
                val currentCodes = dtcRepository.getDtcCodes(
                    vehicleId = parameters.vehicleId,
                    includeCleared = false
                )
                
                // Позначаємо їх як очищені (зберігаємо в історії)
                currentCodes.forEach { code ->
                    dtcRepository.markDtcAsCleared(code.code)
                }
            }
            
            // Видаляємо активні коди
            dtcRepository.clearDtcCodes(parameters.vehicleId)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

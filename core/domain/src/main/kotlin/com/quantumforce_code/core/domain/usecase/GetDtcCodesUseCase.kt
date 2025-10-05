// 1. File Purpose: Use case for retrieving DTC codes
// 2. Role: Business logic for fetching diagnostic trouble codes
// 3. Принципи: Single Responsibility, Command Pattern

package com.quantumforce_code.core.domain.usecase

import com.quantumforce_code.core.domain.DtcCode
import com.quantumforce_code.core.domain.UseCase
import com.quantumforce_code.core.domain.repository.DtcRepository

/**
 * Use Case для отримання DTC кодів автомобіля.
 * 
 * Призначення: Інкапсулює бізнес-логіку отримання кодів помилок.
 * Використання: ViewModel викликає цей use case для завантаження DTC кодів.
 * 
 * Приклад:
 * ```
 * val useCase = GetDtcCodesUseCase(dtcRepository)
 * val result = useCase(GetDtcCodesUseCase.Params("vehicle-123"))
 * result.onSuccess { codes ->
 *     // Обробити коди
 * }
 * ```
 * 
 * @param dtcRepository репозиторій для доступу до DTC даних
 */
class GetDtcCodesUseCase(
    private val dtcRepository: DtcRepository
) : UseCase<GetDtcCodesUseCase.Params, List<DtcCode>>() {
    
    /**
     * Параметри для виконання use case.
     * 
     * @property vehicleId ідентифікатор автомобіля
     * @property includeCleared чи включати очищені коди
     * @property filterBySeverity фільтр за рівнем критичності (optional)
     */
    data class Params(
        val vehicleId: String,
        val includeCleared: Boolean = false,
        val filterBySeverity: DtcCode.Severity? = null
    )
    
    /**
     * Виконує отримання DTC кодів з репозиторію.
     * 
     * Логіка:
     * 1. Отримати всі DTC коди з репозиторію
     * 2. Застосувати фільтри (якщо є)
     * 3. Відсортувати за критичністю (CRITICAL першими)
     * 
     * @param parameters параметри запиту
     * @return Result з списком кодів або помилкою
     */
    override suspend fun execute(parameters: Params): Result<List<DtcCode>> {
        return try {
            // Отримуємо коди з репозиторію
            val codes = dtcRepository.getDtcCodes(
                vehicleId = parameters.vehicleId,
                includeCleared = parameters.includeCleared
            )
            
            // Застосовуємо фільтр за severity якщо заданий
            val filteredCodes = parameters.filterBySeverity?.let { severity ->
                codes.filter { it.severity == severity }
            } ?: codes
            
            // Сортуємо за критичністю: CRITICAL > HIGH > MEDIUM > LOW
            val sortedCodes = filteredCodes.sortedByDescending { 
                it.severity.ordinal 
            }
            
            Result.success(sortedCodes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

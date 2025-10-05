// 1. File Purpose: Base UseCase abstraction for all business operations
// 2. Role: Provides contract for executing domain operations with Result handling
// 3. Architecture: Core of Clean Architecture - encapsulates single business rule
// 4. Pattern: Command pattern - each UseCase is one business operation
// 5. Type Parameters:
//    - P (in): Parameters needed for operation (can be Unit if none)
//    - R (out): Result type returned on success
// 6. Usage:
//    class ReadDtcCodesUseCase : UseCase<Unit, List<DtcCode>>() {
//        override suspend fun execute(parameters: Unit): Result<List<DtcCode>> { ... }
//    }
// 7. Related: All domain UseCases inherit from this

package com.quantumforce_code.core.domain

/**
 * Базовий абстрактний клас для всіх Use Cases в domain шарі.
 * 
 * Use Case представляє одну бізнес-операцію/правило в системі.
 * Кожен UseCase інкапсулює специфічну логіку, яка не залежить від
 * деталей реалізації (UI, database, network).
 * 
 * **Принципи:**
 * - Single Responsibility: Один UseCase = одна бізнес-операція
 * - Platform Independent: Чистий Kotlin, без Android framework залежностей
 * - Testable: Легко тестувати з моками
 * 
 * **Type Parameters:**
 * @param P Тип параметрів, що приймає UseCase (contravariant)
 * @param R Тип результату, що повертається (covariant)
 * 
 * **Приклади:**
 * ```kotlin
 * // UseCase без параметрів
 * class GetCurrentVehicleUseCase : UseCase<Unit, Vehicle>() {
 *     override suspend fun execute(parameters: Unit): Result<Vehicle> {
 *         return repository.getCurrentVehicle()
 *     }
 * }
 * 
 * // UseCase з параметрами
 * class ClearDtcCodeUseCase : UseCase<String, Boolean>() {
 *     override suspend fun execute(parameters: String): Result<Boolean> {
 *         return repository.clearDtcCode(parameters)
 *     }
 * }
 * ```
 * 
 * @see Result Kotlin Result type для error handling
 * @see [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
 */
abstract class UseCase<in P, out R> {
    /**
     * Виконує бізнес-операцію з наданими параметрами.
     * 
     * Цей метод suspend, тому може викликати інші suspend функції
     * (repository calls, network, database) без блокування thread.
     * 
     * @param parameters Параметри для виконання операції
     * @return Result<R> - Success з даними або Failure з помилкою
     */
    abstract suspend fun execute(parameters: P): Result<R>
}

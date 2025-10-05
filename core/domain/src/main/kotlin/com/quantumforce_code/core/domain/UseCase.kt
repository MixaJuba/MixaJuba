/**
 * Base UseCase Abstraction.
 *
 * Purpose: Provides a contract for executing domain operations in a clean architecture.
 * Functionality: Defines a suspend function for business logic execution with parameters and results.
 * Context: Used by ViewModels to perform domain logic; ensures separation of concerns.
 * 
 * Принципи: Single Responsibility, Command Pattern
 * 
 * @param P - тип параметрів для виконання операції
 * @param R - тип результату операції
 */

package com.quantumforce_code.core.domain

abstract class UseCase<in P, out R> {
    /**
     * Виконує бізнес-логіку use case.
     * 
     * @param parameters вхідні дані для операції
     * @return Result<R> - успішний результат або помилка
     */
    abstract suspend fun execute(parameters: P): Result<R>
    
    /**
     * Викликає use case з автоматичною обробкою винятків.
     * Оператор invoke дозволяє викликати UseCase як функцію.
     * 
     * Приклад: val result = myUseCase(params)
     */
    suspend operator fun invoke(parameters: P): Result<R> {
        return try {
            execute(parameters)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

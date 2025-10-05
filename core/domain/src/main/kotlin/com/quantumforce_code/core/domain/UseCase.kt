/**
 * Base UseCase Abstraction.
 *
 * Purpose: Provides a contract for executing domain operations in a clean architecture.
 * Functionality: Defines a suspend function for business logic execution with parameters and results.
 * Context: Used by ViewModels to perform domain logic; ensures separation of concerns.
 */

package com.quantumforce_code.core.domain

abstract class UseCase<in P, out R> {
    abstract suspend fun execute(parameters: P): Result<R>
}

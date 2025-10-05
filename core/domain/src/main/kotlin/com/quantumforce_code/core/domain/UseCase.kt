// 1. File Purpose: Base UseCase abstraction
// 2. Role: Provides contract for executing domain operations

package com.quantumforce_code.core.domain

abstract class UseCase<in P, out R> {
    abstract suspend fun execute(parameters: P): Result<R>
}

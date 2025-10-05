// 1. File Purpose: Logging security gate
// 2. Role: Regulates sensitive logging behavior

package com.quantumforce_code.security

object LoggerPolicy {
    fun shouldLogSensitiveData(): Boolean = false
}

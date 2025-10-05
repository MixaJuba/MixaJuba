// 1. File Purpose: DTC code entity
// 2. Role: Encapsulates diagnostic trouble code data

package com.quantumforce_code.core.domain

data class DtcCode(
    val code: String,
    val description: String,
    val severity: Severity,
    val causes: List<String>,
    val solutions: List<String>
)

enum class Severity {
    LOW, MEDIUM, HIGH, CRITICAL
}

// 1. File Purpose: Diagnostic session aggregate
// 2. Role: Tracks lifecycle of a single diagnostics interaction

package com.quantumforce_code.core.domain

data class DiagnosticSession(
    val id: String,
    val vehicle: Vehicle,
    val startTime: Long,
    val endTime: Long? = null,
    val dtcCodes: List<DtcCode> = emptyList(),
    val status: SessionStatus = SessionStatus.ACTIVE
)

enum class SessionStatus {
    ACTIVE, COMPLETED, FAILED
}

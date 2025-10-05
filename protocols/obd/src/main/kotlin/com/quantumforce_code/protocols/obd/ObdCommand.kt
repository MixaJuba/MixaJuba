// 1. File Purpose: OBD command model
// 2. Role: Encapsulates PID query/response framing

package com.quantumforce_code.protocols.obd

data class ObdCommand(
    val pid: String,
    val rawCommand: String,
    val expectedResponse: String? = null
)

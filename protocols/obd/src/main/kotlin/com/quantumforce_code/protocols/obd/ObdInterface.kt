// 1. File Purpose: OBD interface abstraction
// 2. Role: Defines contract for OBD command execution

package com.quantumforce_code.protocols.obd

interface ObdInterface {
    fun sendCommand(command: ObdCommand): String?
    fun isConnected(): Boolean
}

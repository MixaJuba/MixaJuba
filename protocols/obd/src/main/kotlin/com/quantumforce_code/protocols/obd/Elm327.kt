// 1. File Purpose: ELM327 adapter abstraction
// 2. Role: Handles initialization and command wrapping for ELM327-like devices

package com.quantumforce_code.protocols.obd

class Elm327 : ObdInterface {
    override fun sendCommand(command: ObdCommand): String? = null // Placeholder
    override fun isConnected(): Boolean = false
}

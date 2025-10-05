// 1. File Purpose: USB Serial transport implementation
// 2. Role: Manages USB ELM327-like adapters communication

package com.quantumforce_code.hardware.transport

class UsbSerialPort : Port {
    override fun open(): Boolean = false // Placeholder
    override fun close() {}
    override fun write(data: ByteArray): Boolean = false
    override fun read(): ByteArray? = null
    override val isConnected: Boolean = false
}

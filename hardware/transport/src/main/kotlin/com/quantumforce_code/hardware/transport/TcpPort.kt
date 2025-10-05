// 1. File Purpose: TCP/IP transport implementation
// 2. Role: Handles Wi-Fi / network-based OBD gateways

package com.quantumforce_code.hardware.transport

class TcpPort : Port {
    override fun open(): Boolean = false // Placeholder
    override fun close() {}
    override fun write(data: ByteArray): Boolean = false
    override fun read(): ByteArray? = null
    override val isConnected: Boolean = false
}

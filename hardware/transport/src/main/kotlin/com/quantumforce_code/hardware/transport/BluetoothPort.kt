// 1. File Purpose: Bluetooth transport implementation
// 2. Role: Manages BLE/RFCOMM diagnostic channel

package com.quantumforce_code.hardware.transport

class BluetoothPort : Port {
    override fun open(): Boolean = false // Placeholder
    override fun close() {}
    override fun write(data: ByteArray): Boolean = false
    override fun read(): ByteArray? = null
    override val isConnected: Boolean = false
}

// 1. File Purpose: Generic transport port abstraction
// 2. Role: Defines contract for hardware communication channels

package com.quantumforce_code.hardware.transport

interface Port {
    fun open(): Boolean
    fun close()
    fun write(data: ByteArray): Boolean
    fun read(): ByteArray?
    val isConnected: Boolean
}

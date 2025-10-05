// 1. File Purpose: Data version model
// 2. Role: Represents versioned datasets for updates

package com.quantumforce_code.updates

data class DataVersion(
    val version: String,
    val checksum: String,
    val url: String
)

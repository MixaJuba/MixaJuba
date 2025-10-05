// 1. File Purpose: Vehicle entity model
// 2. Role: Represents vehicle metadata in diagnostics domain

package com.quantumforce_code.core.domain

data class Vehicle(
    val id: String,
    val make: String,
    val model: String,
    val year: Int,
    val vin: String
)

// 1. File Purpose: UI state for DTC feature
// 2. Role: Data holder for Compose screens

package com.quantumforce_code.features.dtc

data class DtcUiState(
    val isLoading: Boolean = false,
    val dtcCodes: List<String> = emptyList(),
    val error: String? = null
)

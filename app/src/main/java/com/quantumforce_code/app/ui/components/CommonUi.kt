// 1. File Purpose: Common UI components
// 2. Role: Reusable Compose components for the app

package com.quantumforce_code.app.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun CommonButton(
    text: String,
    onClick: () -> Unit
) {
    Button(onClick = onClick) {
        Text(text)
    }
}
// 1. File Purpose: Main app composable
// 2. Role: Root composable that sets up navigation

package com.quantumforce_code.app

import androidx.compose.runtime.Composable
import com.quantumforce_code.app.navigation.NavGraph

@Composable
fun AutoDiagProApp() {
    NavGraph()
}
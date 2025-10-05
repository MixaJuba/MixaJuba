/**
 * Main App Composable.
 *
 * Purpose: Root composable that sets up the app's navigation and UI structure.
 * Functionality: Wraps the NavGraph to provide the main app layout and routing.
 * Context: Called from MainActivity; central point for UI composition and navigation flow.
 */
package com.quantumforce_code.app

import androidx.compose.runtime.Composable
import com.quantumforce_code.app.navigation.NavGraph

@Composable
fun AutoDiagProApp() {
    NavGraph()
}
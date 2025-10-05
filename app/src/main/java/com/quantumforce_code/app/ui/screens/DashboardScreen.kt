/**
 * Dashboard Screen for Main App Navigation.
 *
 * Purpose: Displays the main menu and quick access to app features.
 * Functionality: Provides buttons to navigate to DTC and Live Data screens.
 * Context: First screen users see; part of the UI layer in the app module.
 */
package com.quantumforce_code.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DashboardScreen(
    onNavigateToDtc: () -> Unit,
    onNavigateToLive: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "AutoDiagPro Dashboard",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(onClick = onNavigateToDtc) {
            Text("DTC Codes")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(onClick = onNavigateToLive) {
            Text("Live Data")
        }
    }
}

// 1. File Purpose: Dashboard screen for main app navigation
// 2. Role: Displays main menu and quick access to features

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

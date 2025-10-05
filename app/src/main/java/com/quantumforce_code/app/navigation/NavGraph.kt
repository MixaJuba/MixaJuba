// 1. File Purpose: Navigation graph setup
// 2. Role: Defines app navigation routes and screens

package com.quantumforce_code.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.quantumforce_code.app.ui.screens.DashboardScreen
import com.quantumforce_code.app.ui.screens.DtcScreen
import com.quantumforce_code.app.ui.screens.LiveScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") {
            DashboardScreen(
                onNavigateToDtc = { navController.navigate("dtc") },
                onNavigateToLive = { navController.navigate("live") }
            )
        }
        composable("dtc") {
            DtcScreen()
        }
        composable("live") {
            LiveScreen()
        }
    }
}
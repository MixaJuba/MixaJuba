// 1. File Purpose: Main activity for AutoDiagPro app
// 2. Role: Entry point, sets up Compose UI and Material3 theme
// 3. Architecture: Single-activity architecture with Jetpack Compose
// 4. Navigation: Delegates to NavGraph via AutoDiagProApp composable
// 5. Lifecycle: Manages UI state, handles configuration changes
// 6. Dependencies: Hilt-injected ViewModels and repositories available to children
// 7. Related: AutoDiagProApp.kt (root composable), NavGraph.kt (navigation)

package com.quantumforce_code.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.quantumforce_code.app.ui.theme.AutoDiagProTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AutoDiagProTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AutoDiagProApp()
                }
            }
        }
    }
}

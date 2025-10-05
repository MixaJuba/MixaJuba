/**
 * Main Activity for AutoDiagPro App.
 *
 * Purpose: Serves as the entry point for the Android application, initializing the UI framework.
 * Functionality: Sets up Jetpack Compose, applies the app theme, and displays the main navigation graph.
 * Context: Launched when the app starts; manages the app's lifecycle and UI rendering.
 */
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

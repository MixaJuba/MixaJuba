/**
 * Application Class for Hilt Setup.
 *
 * Purpose: Initializes the application-wide dependency injection using Hilt.
 * Functionality: Provides the base application context and configures Hilt for the entire app.
 * Context: Executed once when the app process starts; essential for DI in all components.
 */
package com.quantumforce_code.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application()

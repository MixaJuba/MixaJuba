// 1. File Purpose: Application class for Hilt setup
// 2. Role: Initializes dependency injection

package com.quantumforce_code.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application()

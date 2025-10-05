// 1. File Purpose: UI tests for app module
// 2. Role: Tests main app functionality

package com.quantumforce_code.app

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class AppUiTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun testDashboardNavigation() {
        composeTestRule.setContent {
            AutoDiagProApp()
        }
        
        composeTestRule.onNodeWithText("DTC Codes").performClick()
        composeTestRule.onNodeWithText("DTC Codes").assertExists()
    }
}
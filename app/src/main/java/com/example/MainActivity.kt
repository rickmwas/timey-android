package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.example.core.datastore.UserPreferences
import com.example.ui.TimyMainApp
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.TimyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val userPreferences = UserPreferences(applicationContext)

        setContent {
            val themeModeStr by userPreferences.themeMode.collectAsState(initial = "LIGHTS_OUT")
            val themeMode = remember(themeModeStr) {
                when (themeModeStr) {
                    "LIGHT" -> AppThemeMode.LIGHT
                    "DIM" -> AppThemeMode.DIM
                    else -> AppThemeMode.LIGHTS_OUT
                }
            }

            TimyTheme(themeMode = themeMode) {
                TimyMainApp(
                    userPreferences = userPreferences,
                    currentThemeMode = themeMode
                )
            }
        }
    }
}

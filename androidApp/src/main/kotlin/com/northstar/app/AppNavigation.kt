package com.northstar.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.northstar.app.presentation.placeholder.PlaceholderScreen
import com.northstar.app.ui.theme.NorthStarTheme

object AppRoutes {
    const val PLACEHOLDER = "placeholder"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NorthStarTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            NavHost(
                navController = navController,
                startDestination = AppRoutes.PLACEHOLDER,
            ) {
                composable(AppRoutes.PLACEHOLDER) {
                    PlaceholderScreen()
                }
            }
        }
    }
}

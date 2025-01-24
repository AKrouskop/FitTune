package com.example.fittune

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fittune.screens.*
import com.example.fittune.ui.theme.FitTuneTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fittune.viewmodels.ProgressViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var darkModeEnabled by remember { mutableStateOf(false) }
            val navController = rememberNavController()

            FitTuneTheme(darkMode = darkModeEnabled) {
                AppNavHost(
                    navController = navController,
                    onDarkModeToggle = { isEnabled -> darkModeEnabled = isEnabled } // Update state here
                )
            }
        }
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    onDarkModeToggle: (Boolean) -> Unit
) {
    val progressViewModel: ProgressViewModel = viewModel()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onStartWorkoutClick = { navController.navigate("workout") },
                onLogMealClick = { navController.navigate("logMeal") },
                onViewProgressClick = { navController.navigate("progress") },
                onNavigateTo = { screen -> navController.navigate(screen) },
                progressViewModel = progressViewModel
            )
        }
        composable("workout") {
            WorkoutScreen(
                onNavigateTo = { navController.navigate(it) },
                progressViewModel = progressViewModel
            )
        }
        composable("logMeal") {
            LogMealScreen(
                onNavigateTo = { navController.navigate(it) },
                progressViewModel = progressViewModel
            )
        }
        composable("progress") {
            ProgressScreen(
                onNavigateTo = { navController.navigate(it) },
                progressViewModel = progressViewModel
            )
        }
        composable("settings") {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onToggleDarkMode = onDarkModeToggle,
                progressViewModel = progressViewModel
            )
        }
    }
}

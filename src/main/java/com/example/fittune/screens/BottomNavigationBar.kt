package com.example.fittune.screens

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

@Composable
fun BottomNavigationBar(currentScreen: String, onNavigate: (String) -> Unit) {
    NavigationBar(
        containerColor = Color(0xFFF5F5F5) // Light gray background
    ) {
        NavigationBarItem(
            selected = currentScreen == "home",
            onClick = { onNavigate("home") },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF4CAF50))
        )
        NavigationBarItem(
            selected = currentScreen == "workout",
            onClick = { onNavigate("workout") },
            icon = { Icon(Icons.Filled.FitnessCenter, contentDescription = "Workout") },
            label = { Text("Workout") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF4CAF50))
        )
        NavigationBarItem(
            selected = currentScreen == "logMeal",
            onClick = { onNavigate("logMeal") },
            icon = { Icon(Icons.Filled.Fastfood, contentDescription = "Nutrition") },
            label = { Text("Nutrition") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF4CAF50))
        )
        NavigationBarItem(
            selected = currentScreen == "progress",
            onClick = { onNavigate("progress") },
            icon = { Icon(Icons.Filled.Person, contentDescription = "Progress") },
            label = { Text("You") },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF4CAF50))
        )
    }
}

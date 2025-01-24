package com.example.fittune.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fittune.viewmodels.ProgressViewModel
import androidx.compose.ui.text.input.KeyboardType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateTo: (String) -> Unit = {},
    onToggleDarkMode: (Boolean) -> Unit,
    progressViewModel: ProgressViewModel = viewModel()
) {
    var darkModeEnabled by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(false) }
    var showGoalDialog by remember { mutableStateOf(false) }
    var showAccountDialog by remember { mutableStateOf(false) }
    var currentGoal by remember { mutableStateOf("") }
    var showPasswordDialog by remember { mutableStateOf(false) }
    val profile by progressViewModel.profile.collectAsState()
    val accountInfo = mapOf(
        "Name" to profile.name,
        "Email" to profile.email,
        "Phone" to profile.phone,
        "Weight" to profile.weight,
        "Height" to profile.height
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Left,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF4CAF50))
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Dark Mode Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Dark Mode", fontSize = 18.sp)
                    Switch(
                        checked = darkModeEnabled,
                        onCheckedChange = { isChecked ->
                            darkModeEnabled = isChecked
                            onToggleDarkMode(isChecked)
                        }
                    )
                }

                // Daily Calorie Goal
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Daily Calories Goal: ${profile.dailyCaloriesGoal}", fontSize = 18.sp)
                    Button(onClick = {
                        currentGoal = "calories"
                        showGoalDialog = true
                    }) {
                        Text("Edit")
                    }
                }

                // Weekly Workouts Goal
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Weekly Workouts Goal: ${profile.weeklyWorkoutGoal}", fontSize = 18.sp)
                    Button(onClick = {
                        currentGoal = "workouts"
                        showGoalDialog = true
                    }) {
                        Text("Edit")
                    }
                }

                // Account Information Section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Account Information", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    accountInfo.forEach { (key, value) ->
                        Text("$key: $value", fontSize = 16.sp)
                    }
                    Button(onClick = { showAccountDialog = true }) {
                        Text("Edit Account")
                    }
                }

                // Change Password Button
                Button(
                    onClick = { showPasswordDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
                ) {
                    Text("Change Password", fontSize = 18.sp, color = Color.White)
                }

                // Notifications Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Enable Notifications", fontSize = 18.sp)
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Logout Button
                Button(
                    onClick = { /* Logout logic */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Logout", fontSize = 18.sp, color = Color.White)
                }
            }
        }
    )

    // Dialog for Editing Goals
    if (showGoalDialog) {
        GoalEditDialog(
            currentGoal = currentGoal,
            dailyCalories = profile.dailyCaloriesGoal,
            weeklyWorkouts = profile.weeklyWorkoutGoal,
            onSave = { goal, value ->
                if (goal == "calories") {
                    progressViewModel.updateProfile(
                        name = profile.name,
                        email = profile.email,
                        phone = profile.phone,
                        weight = profile.weight,
                        height = profile.height,
                        dailyCaloriesGoal = value.toInt(),
                        weeklyWorkoutGoal = profile.weeklyWorkoutGoal
                    )
                } else if (goal == "workouts") {
                    progressViewModel.updateProfile(
                        name = profile.name,
                        email = profile.email,
                        phone = profile.phone,
                        weight = profile.weight,
                        height = profile.height,
                        dailyCaloriesGoal = profile.dailyCaloriesGoal,
                        weeklyWorkoutGoal = value.toInt()
                    )
                }
                showGoalDialog = false
            },
            onDismiss = { showGoalDialog = false }
        )
    }

    // Dialog for Editing Account Information
    if (showAccountDialog) {
        AccountEditDialog(
            accountInfo = accountInfo,
            onSave = { updatedInfo ->
                progressViewModel.updateProfile(
                    name = updatedInfo["Name"] ?: profile.name,
                    email = updatedInfo["Email"] ?: profile.email,
                    phone = updatedInfo["Phone"] ?: profile.phone,
                    weight = updatedInfo["Weight"] ?: profile.weight,
                    height = updatedInfo["Height"] ?: profile.height,
                    dailyCaloriesGoal = profile.dailyCaloriesGoal,
                    weeklyWorkoutGoal = profile.weeklyWorkoutGoal
                )
                showAccountDialog = false
            },
            onDismiss = { showAccountDialog = false }
        )
    }
    if (showPasswordDialog) {
        ChangePasswordDialog(
            onSave = { oldPassword, newPassword, confirmPassword ->
                if (newPassword == confirmPassword) {
                    // Handle successful password change
                } else {
                    // Handle password mismatch case
                }
                showPasswordDialog = false
            },
            onDismiss = { showPasswordDialog = false }
        )
    }
}

@Composable
fun GoalEditDialog(
    currentGoal: String,
    dailyCalories: Int,
    weeklyWorkouts: Int,
    onSave: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var input by remember { mutableStateOf("") } // State for user input

    AlertDialog(
        onDismissRequest = onDismiss, // Close the dialog on dismiss request
        title = {
            Text("Edit ${if (currentGoal == "calories") "Calories Goal" else "Workouts Goal"}")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    label = { Text("Enter new goal") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number) // Restrict input to numbers
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (input.isNotEmpty()) {
                    onSave(currentGoal, input) // Call the onSave callback with user input
                }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) { // Close the dialog without saving
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AccountEditDialog(
    accountInfo: Map<String, String>,
    onSave: (Map<String, String>) -> Unit,
    onDismiss: () -> Unit
) {
    // Individual states for each field
    var name by remember { mutableStateOf(accountInfo["Name"] ?: "") }
    var email by remember { mutableStateOf(accountInfo["Email"] ?: "") }
    var phone by remember { mutableStateOf(accountInfo["Phone"] ?: "") }
    var weight by remember { mutableStateOf(accountInfo["Weight"] ?: "") }
    var height by remember { mutableStateOf(accountInfo["Height"] ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Personal Information") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Weight") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = height,
                    onValueChange = { height = it },
                    label = { Text("Height") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(
                    mapOf(
                        "Name" to name,
                        "Email" to email,
                        "Phone" to phone,
                        "Weight" to weight,
                        "Height" to height
                    )
                )
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ChangePasswordDialog(
    onSave: (String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change Password") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = oldPassword,
                    onValueChange = { oldPassword = it },
                    label = { Text("Old Password") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm New Password") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSave(oldPassword, newPassword, confirmPassword) }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

package com.example.fittune.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fittune.viewmodels.ProgressViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen(
    onNavigateTo: (String) -> Unit,
    progressViewModel: ProgressViewModel = viewModel()
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var showTimerDialog by remember { mutableStateOf(false) }
    var showCustomWorkoutDialog by remember { mutableStateOf(false) }
    var showEditCustomWorkoutDialog by remember { mutableStateOf(false) }
    var selectedWorkout by remember { mutableStateOf("") }
    var customWorkouts = remember { mutableStateListOf<Pair<String, List<String>>>() }
    val allExercises = listOf(
        "Squats", "Deadlifts", "Bench Press", // Strength
        "Running", "Cycling", "Rowing",      // Cardio
        "Yoga", "Pilates", "Stretching"      // Flexibility
    )
    val exercisesByTab = listOf(
        listOf("Squats", "Deadlifts", "Bench Press"),
        listOf("Running", "Cycling", "Rowing"),
        listOf("Yoga", "Pilates", "Stretching")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Workout",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                },
                actions = {
                    IconButton(onClick = { onNavigateTo("settings") }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings",
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
                // Tab Row
                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    edgePadding = 0.dp,
                    containerColor = Color.Transparent,
                    contentColor = Color.Black,
                    indicator = { tabPositions ->
                        val currentTabPosition = tabPositions[selectedTabIndex]
                        Box(
                            Modifier
                                .wrapContentSize(Alignment.BottomStart)
                                .offset(x = currentTabPosition.left)
                                .width(currentTabPosition.width)
                                .height(4.dp)
                                .background(color = Color(0xFFFFC107), shape = RoundedCornerShape(2.dp))
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = { Text("Strength") },
                        selectedContentColor = Color(0xFFFFC107),
                        unselectedContentColor = Color.Black
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text("Cardio") },
                        selectedContentColor = Color(0xFFFFC107),
                        unselectedContentColor = Color.Black
                    )
                    Tab(
                        selected = selectedTabIndex == 2,
                        onClick = { selectedTabIndex = 2 },
                        text = { Text("Flexibility") },
                        selectedContentColor = Color(0xFFFFC107),
                        unselectedContentColor = Color.Black
                    )
                    Tab(
                        selected = selectedTabIndex == 3,
                        onClick = { selectedTabIndex = 3 },
                        text = { Text("Custom Workouts") },
                        selectedContentColor = Color(0xFFFFC107),
                        unselectedContentColor = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Workout Summary Section
                when (selectedTabIndex) {
                    0 -> StrengthWorkouts()
                    1 -> CardioWorkouts()
                    2 -> FlexibilityWorkouts()
                    3 -> CustomWorkouts(
                        selectedWorkout = selectedWorkout,
                        onSelectWorkout = { selectedWorkout = it },
                        onAddWorkout = { showCustomWorkoutDialog = true },
                        customWorkouts = customWorkouts,
                        onEditWorkout = { workout ->
                            selectedWorkout = workout
                            showEditCustomWorkoutDialog = true
                        },
                        onStartWorkout = { showTimerDialog = true } // Add this parameter
                    )
                }

                // Start Workout Button for non-custom tabs
                if (selectedTabIndex != 3) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showTimerDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
                    ) {
                        Text("Start Workout", fontSize = 18.sp, color = Color.White)
                    }
                }
            }
        },
        bottomBar = {
            BottomNavigationBar(currentScreen = "Meals", onNavigate = onNavigateTo)
        }
    )

    if (showTimerDialog) {
        WorkoutTimerDialog(
            exercises = if (selectedTabIndex == 3) {
                customWorkouts.find { it.first == selectedWorkout }?.second ?: emptyList()
            } else {
                exercisesByTab[selectedTabIndex]
            },
            onDismiss = { showTimerDialog = false },
            onWorkoutComplete = { elapsedTime ->
                showTimerDialog = false

                // Calculate calories burned (example: 10 calories per minute)
                val caloriesBurned = elapsedTime / 60 * 10

                // Update the workout progress using logWorkout
                progressViewModel.logWorkout(caloriesBurned = caloriesBurned)
            }
        )
    }

    if (showCustomWorkoutDialog) {
        CustomWorkoutDialog(
            availableExercises = allExercises,
            onSave = { selectedExercises ->
                customWorkouts.add("Custom Workout ${customWorkouts.size + 1}" to selectedExercises)
                showCustomWorkoutDialog = false
            },
            onDismiss = { showCustomWorkoutDialog = false }
        )
    }

    if (showEditCustomWorkoutDialog) {
        CustomWorkoutDialog(
            availableExercises = allExercises,
            onSave = { updatedExercises ->
                customWorkouts.replaceAll {
                    if (it.first == selectedWorkout) it.first to updatedExercises else it
                }
                showEditCustomWorkoutDialog = false
            },
            onDismiss = { showEditCustomWorkoutDialog = false }
        )
    }
}

@Composable
fun CustomWorkouts(
    selectedWorkout: String,
    onSelectWorkout: (String) -> Unit,
    onAddWorkout: () -> Unit,
    customWorkouts: MutableList<Pair<String, List<String>>>,
    onEditWorkout: (String) -> Unit,
    onStartWorkout: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        LazyColumn {
            if (customWorkouts.isEmpty()) {
                item {
                    Text(
                        "No custom workouts available.",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                items(customWorkouts.size) { index ->
                    val workout = customWorkouts[index]
                    val workoutName = workout.first
                    val exercises = workout.second

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = workoutName,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Exercises: ${exercises.joinToString(", ")}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { onEditWorkout(workoutName) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                            ) {
                                Text("Edit")
                            }

                            Button(
                                onClick = {
                                    if (index in customWorkouts.indices) {
                                        customWorkouts.removeAt(index)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                            ) {
                                Text("Delete")
                            }

                            Button(
                                onClick = {
                                    onSelectWorkout(workoutName)
                                    onStartWorkout() // Call the new parameter function
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
                            ) {
                                Text("Start")
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = onAddWorkout,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text("Add Custom Workout", fontSize = 18.sp, color = Color.White)
        }
    }
}

@Composable
fun StrengthWorkouts() {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item { WorkoutSummaryItem("Squats", "3x10", "450 cal burned") }
        item { WorkoutSummaryItem("Deadlifts", "3x8", "500 cal burned") }
        item { WorkoutSummaryItem("Bench Press", "3x8", "400 cal burned") }
    }
}

@Composable
fun CardioWorkouts() {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item { WorkoutSummaryItem("Running", "30 min", "300 cal burned") }
        item { WorkoutSummaryItem("Cycling", "45 min", "450 cal burned") }
        item { WorkoutSummaryItem("Rowing", "20 min", "250 cal burned") }
    }
}

@Composable
fun FlexibilityWorkouts() {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item { WorkoutSummaryItem("Yoga", "30 min", "200 cal burned") }
        item { WorkoutSummaryItem("Pilates", "40 min", "250 cal burned") }
        item { WorkoutSummaryItem("Stretching", "20 min", "150 cal burned") }
    }
}

@Composable
fun WorkoutSummaryItem(name: String, setsReps: String, calories: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp), // Add vertical padding for better spacing
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = name,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(2f) // Allocate more space for the name
        )
        Text(
            text = setsReps,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f) // Center-align sets/reps
        )
        Text(
            text = calories,
            fontSize = 16.sp,
            color = Color(0xFF4CAF50),
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f) // Align calories to the right
        )
    }
}

@Composable
fun WorkoutTimerDialog(
    exercises: List<String>,
    onDismiss: () -> Unit,
    onWorkoutComplete: (Int) -> Unit
) {
    var currentExerciseIndex by remember { mutableStateOf(0) }
    var timerRunning by remember { mutableStateOf(true) }
    var elapsedTime by remember { mutableStateOf(0) } // Seconds elapsed

    // Timer Logic
    LaunchedEffect(timerRunning) {
        while (timerRunning) {
            kotlinx.coroutines.delay(1000L)
            elapsedTime++
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Workout Timer") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Current Exercise: ${exercises[currentExerciseIndex]}")
                Text("Time Elapsed: ${elapsedTime / 60} min ${elapsedTime % 60} sec")

                if (currentExerciseIndex < exercises.size - 1) {
                    Button(onClick = { currentExerciseIndex++ }) {
                        Text("Next Exercise")
                    }
                } else {
                    Button(onClick = {
                        timerRunning = false
                        onWorkoutComplete(elapsedTime)
                    }) {
                        Text("Finish Workout")
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            Button(onClick = {
                timerRunning = false
                onDismiss()
            }) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun CustomWorkoutDialog(
    availableExercises: List<String>,
    onSave: (List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    val selectedExercises = remember { mutableStateListOf<String>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Custom Workout") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LazyColumn(modifier = Modifier.height(200.dp)) {
                    items(availableExercises.size) { index ->
                        val exercise = availableExercises[index]
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Checkbox(
                                checked = selectedExercises.contains(exercise),
                                onCheckedChange = { isChecked ->
                                    if (isChecked) {
                                        if (selectedExercises.size < 3) selectedExercises.add(exercise)
                                    } else {
                                        selectedExercises.removeAt(selectedExercises.indexOf(exercise))
                                    }
                                }
                            )
                            Text(exercise, fontSize = 16.sp)
                        }
                    }
                }
                Text("Selected: ${selectedExercises.joinToString(", ")}")
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(selectedExercises)
                onDismiss()
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


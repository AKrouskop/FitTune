package com.example.fittune.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fittune.viewmodels.ProgressViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(onNavigateTo: (String) -> Unit, progressViewModel: ProgressViewModel = viewModel()) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var isWorkoutSelected by remember { mutableStateOf(true) } // State for toggling between Workout and Nutrition

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "You",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                },
                actions = {
                    IconButton(onClick = { onNavigateTo("settings") }) { // Navigate to Settings
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
                // Tabs at the top
                TabRow(
                    selectedTabIndex = selectedTabIndex,
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
                        text = { Text("Progress") },
                        selectedContentColor = Color(0xFFFFC107),
                        unselectedContentColor = Color.Black
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text("Profile") },
                        selectedContentColor = Color(0xFFFFC107),
                        unselectedContentColor = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Show content for Progress or Profile based on selectedTabIndex
                if (selectedTabIndex == 0) {
                    ProgressTabContent(
                        isWorkoutSelected = isWorkoutSelected,
                        onToggleSelected = { selected -> isWorkoutSelected = selected },
                        progressViewModel = progressViewModel
                    )
                } else {
                    ProfileTabContent(progressViewModel = progressViewModel)
                }
            }
        },
        bottomBar = {
            BottomNavigationBar(currentScreen = "You", onNavigate = onNavigateTo)
        }
    )
}

@Composable
fun ProgressTabContent(
    isWorkoutSelected: Boolean,
    onToggleSelected: (Boolean) -> Unit,
    progressViewModel: ProgressViewModel
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Workout and Nutrition toggle buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { onToggleSelected(true) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isWorkoutSelected) Color(0xFFFFC107) else Color.White
                ),
                modifier = Modifier.weight(1f).padding(8.dp)
            ) {
                Text("Workout", color = if (isWorkoutSelected) Color.Black else Color.Gray, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = { onToggleSelected(false) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!isWorkoutSelected) Color(0xFFFFC107) else Color.White
                ),
                modifier = Modifier.weight(1f).padding(8.dp)
            ) {
                Text("Nutrition", color = if (!isWorkoutSelected) Color.Black else Color.Gray, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isWorkoutSelected) {
            WorkoutProgressContent(progressViewModel = progressViewModel)
        } else {
            NutritionProgressContent(progressViewModel = progressViewModel)
        }
    }
}

@Composable
fun WorkoutProgressContent(progressViewModel: ProgressViewModel) {
    val workoutProgress by progressViewModel.workoutProgress.collectAsState()

    Text(text = "This week", fontWeight = FontWeight.Bold, fontSize = 18.sp)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Workouts: ${workoutProgress.workoutsCompleted}/${workoutProgress.weeklyGoal}", fontSize = 16.sp)
        Text("Calories Burned: ${workoutProgress.caloriesBurned}/${workoutProgress.calorieGoal}", fontSize = 16.sp)
    }
    ProgressBar(label = "Workout Goal", progress = workoutProgress.workoutCompletionPercentage)
    ProgressBar(label = "Calories Burned Goal", progress = workoutProgress.calorieCompletionPercentage)
}

@Composable
fun NutritionProgressContent(progressViewModel: ProgressViewModel) {
    val nutritionProgress by progressViewModel.nutritionProgress.collectAsState()

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(text = "Today's Nutrition", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        ProgressBar(label = "Calories Remaining", progress = nutritionProgress.caloriePercentage)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Consumed: ${nutritionProgress.consumedCalories} kcal", fontSize = 16.sp)
            Text("Remaining: ${nutritionProgress.remainingCalories} kcal", fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text("Protein: ${nutritionProgress.protein}g", fontSize = 16.sp)
            Text("Carbs: ${nutritionProgress.carbs}g", fontSize = 16.sp)
            Text("Fats: ${nutritionProgress.fats}g", fontSize = 16.sp)
        }
    }
}

@Composable
fun ProfileTabContent(progressViewModel: ProgressViewModel) {
    val profile by progressViewModel.profile.collectAsState()

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(text = "Profile", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text("Name: ${profile.name}", fontSize = 16.sp)
            Text("Email: ${profile.email}", fontSize = 16.sp)
        }
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text("Daily Calories Goal: ${profile.dailyCaloriesGoal}", fontSize = 16.sp)
            Text("Weekly Workouts: ${profile.weeklyWorkoutGoal}", fontSize = 16.sp)
        }
    }
}
@Composable
fun ProgressBar(label: String, progress: Float) {
    Column {
        Text(text = label, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        LinearProgressIndicator(
            progress = progress.coerceIn(0f, 1f), // Ensure progress is between 0 and 1
            color = Color(0xFFFFC107),
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        )
    }
}

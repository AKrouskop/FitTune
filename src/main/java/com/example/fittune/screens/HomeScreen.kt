package com.example.fittune.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fittune.viewmodels.ProgressViewModel
import androidx.compose.runtime.collectAsState

@OptIn(ExperimentalMaterial3Api::class) // Suppress experimental API warnings
@Composable
fun HomeScreen(
    onStartWorkoutClick: () -> Unit,
    onLogMealClick: () -> Unit,
    onViewProgressClick: () -> Unit,
    onNavigateTo: (String) -> Unit,
    progressViewModel: ProgressViewModel = viewModel()
) {

    val workoutProgress = progressViewModel.workoutProgress.collectAsState().value
    val nutritionProgress = progressViewModel.nutritionProgress.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "FitTune",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
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
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onStartWorkoutClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
                ) {
                    Text("Start Workout", fontSize = 18.sp, color = Color.White)
                }

                Button(
                    onClick = onLogMealClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
                ) {
                    Text("Log Meal", fontSize = 18.sp, color = Color.White)
                }

                Button(
                    onClick = onViewProgressClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
                ) {
                    Text("View Progress", fontSize = 18.sp, color = Color.White)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Key Statistics",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatisticCard(
                        label = "Total Calories Burned",
                        value = "${workoutProgress.caloriesBurned} Cals",
                        modifier = Modifier.fillMaxWidth()
                    )
                    StatisticCard(
                        label = "Total Meals Logged",
                        value = "${nutritionProgress.consumedCalories / 500} Meals", // Assuming 500 cal per meal
                        modifier = Modifier.fillMaxWidth()
                    )
                    StatisticCard(
                        label = "Total Workouts Completed",
                        value = "${workoutProgress.workoutsCompleted} Workouts",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        bottomBar = {
            BottomNavigationBar(currentScreen = "home", onNavigate = onNavigateTo)
        }
    )
}

@Composable
fun StatisticCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = modifier.padding(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

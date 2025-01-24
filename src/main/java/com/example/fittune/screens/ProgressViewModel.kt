package com.example.fittune.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class WorkoutProgress(
    val workoutsCompleted: Int,
    val weeklyGoal: Int,
    val caloriesBurned: Int,
    val calorieGoal: Int,
) {
    val workoutCompletionPercentage: Float
        get() = if (weeklyGoal > 0) workoutsCompleted.toFloat() / weeklyGoal else 0f
    val calorieCompletionPercentage: Float
        get() = if (calorieGoal > 0) caloriesBurned.toFloat() / calorieGoal else 0f
}

data class NutritionProgress(
    val consumedCalories: Int,
    val remainingCalories: Int,
    val caloriePercentage: Float,
    val protein: Int,
    val carbs: Int,
    val fats: Int,
)

data class Profile(
    val name: String = "",
    val email: String = "",
    val phone: String = "123-456-7890",
    val weight: String = "180 lbs",
    val height: String = "70 in",
    val dailyCaloriesGoal: Int = 2000,
    val weeklyWorkoutGoal: Int = 4
)

class ProgressViewModel : ViewModel() {
    private val _profile = MutableStateFlow(
        Profile(
            name = "John Doe",
            email = "johndoe@example.com",
            dailyCaloriesGoal = 2000,
            weeklyWorkoutGoal = 4
        )
    )
    val profile: StateFlow<Profile> = _profile

    private val _workoutProgress = MutableStateFlow(
        WorkoutProgress(
            workoutsCompleted = 0,
            weeklyGoal = _profile.value.weeklyWorkoutGoal,
            caloriesBurned = 0,
            calorieGoal = _profile.value.dailyCaloriesGoal
        )
    )
    val workoutProgress: StateFlow<WorkoutProgress> = _workoutProgress

    private val _nutritionProgress = MutableStateFlow(
        NutritionProgress(
            consumedCalories = 0,
            remainingCalories = _profile.value.dailyCaloriesGoal,
            caloriePercentage = 0f,
            protein = 0,
            carbs = 0,
            fats = 0
        )
    )
    val nutritionProgress: StateFlow<NutritionProgress> = _nutritionProgress

    // Function to log a workout
    fun logWorkout(caloriesBurned: Int) {
        _workoutProgress.value = _workoutProgress.value.copy(
            workoutsCompleted = _workoutProgress.value.workoutsCompleted + 1,
            caloriesBurned = _workoutProgress.value.caloriesBurned + caloriesBurned
        )

        // Explicitly update StateFlow
        _workoutProgress.value = _workoutProgress.value
    }

    // Function to log a meal
    fun logMeal(consumedCalories: Int, protein: Int, carbs: Int, fats: Int) {
        val currentNutritionProgress = _nutritionProgress.value
        val currentProfile = _profile.value

        val newConsumedCalories = currentNutritionProgress.consumedCalories + consumedCalories
        val newRemainingCalories = (currentProfile.dailyCaloriesGoal - newConsumedCalories).coerceAtLeast(0)

        _nutritionProgress.value = currentNutritionProgress.copy(
            consumedCalories = newConsumedCalories,
            remainingCalories = newRemainingCalories,
            caloriePercentage = if (currentProfile.dailyCaloriesGoal > 0) {
                newConsumedCalories.toFloat() / currentProfile.dailyCaloriesGoal
            } else 0f,
            protein = currentNutritionProgress.protein + protein,
            carbs = currentNutritionProgress.carbs + carbs,
            fats = currentNutritionProgress.fats + fats
        )

        // Explicitly update StateFlow to ensure Compose observes changes
        _nutritionProgress.value = _nutritionProgress.value
    }

    // Update the profile and sync goals in progress states
    fun updateProfile(
        name: String,
        email: String,
        phone: String,
        weight: String,
        height: String,
        dailyCaloriesGoal: Int,
        weeklyWorkoutGoal: Int
    ) {
        _profile.value = _profile.value.copy(
            name = name,
            email = email,
            phone = phone,
            weight = weight,
            height = height,
            dailyCaloriesGoal = dailyCaloriesGoal,
            weeklyWorkoutGoal = weeklyWorkoutGoal
        )
        syncGoals()
    }

    // Function to sync workout and nutrition progress goals with profile
    private fun syncGoals() {
        _workoutProgress.value = _workoutProgress.value.copy(
            weeklyGoal = _profile.value.weeklyWorkoutGoal,
            calorieGoal = _profile.value.dailyCaloriesGoal
        )
        _nutritionProgress.value = _nutritionProgress.value.copy(
            remainingCalories = _profile.value.dailyCaloriesGoal,
            caloriePercentage = _nutritionProgress.value.consumedCalories.toFloat() / _profile.value.dailyCaloriesGoal
        )
    }

    // Function to reset progress for a new week or day
    fun resetProgress() {
        _workoutProgress.value = _workoutProgress.value.copy(
            workoutsCompleted = 0,
            caloriesBurned = 0
        )
        _nutritionProgress.value = _nutritionProgress.value.copy(
            consumedCalories = 0,
            remainingCalories = _profile.value.dailyCaloriesGoal,
            caloriePercentage = 0f,
            protein = 0,
            carbs = 0,
            fats = 0
        )
    }
}

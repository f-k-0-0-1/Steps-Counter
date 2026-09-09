package com.example.myapplication2.model

data class StepUiState(

    val steps: Int = 0,

    val goal: Int = 10_000,

    val distanceKm: Double = 0.0,

    val calories: Int = 0,

    val progress: Float = 0f,

    val hasPermission: Boolean = false,

    val sensorAvailable: Boolean = false,

    val isTracking: Boolean = false,

    val errorMessage: String? = null
)
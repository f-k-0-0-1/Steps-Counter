package com.example.myapplication2.viewmodel

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope

import com.example.myapplication2.data.StepDataStore
import com.example.myapplication2.data.StepRepository
import com.example.myapplication2.model.StepUiState

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import kotlin.math.roundToInt


class StepsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val context =
        application.applicationContext


    /*
     * DataStore
     *
     * Stores persistent step information such as:
     *
     * - Today's steps
     * - Daily baseline
     * - Baseline date
     * - Daily goal
     */
    private val dataStore =
        StepDataStore(context)


    /*
     * Repository
     *
     * Acts as the data layer between the
     * ViewModel and DataStore.
     */
    private val repository =
        StepRepository(dataStore)


    /*
     * UI state
     *
     * Compose observes this StateFlow.
     */
    private val _uiState =
        MutableStateFlow(
            StepUiState()
        )


    val uiState:
            StateFlow<StepUiState> =
        _uiState.asStateFlow()


    /*
     * Called when the ViewModel is created.
     */
    init {

        loadInitialData()

        observeSteps()
    }


    /*
     * Load initial information.
     *
     * This includes:
     *
     * - Daily goal
     * - Step sensor availability
     */
    private fun loadInitialData() {

        viewModelScope.launch {

            /*
             * Get saved daily goal.
             */
            val goal =
                repository.getGoal()


            /*
             * Check whether the device
             * has a hardware step counter.
             */
            val sensorManager =
                context.getSystemService(
                    Context.SENSOR_SERVICE
                ) as SensorManager


            val sensorAvailable =
                sensorManager.getDefaultSensor(
                    Sensor.TYPE_STEP_COUNTER
                ) != null


            /*
             * Update UI state.
             */
            _uiState.value =
                _uiState.value.copy(

                    goal = goal,

                    sensorAvailable =
                        sensorAvailable
                )
        }
    }


    /*
     * Observe today's steps from DataStore.
     *
     * The foreground service writes the
     * current step count into DataStore.
     *
     * This Flow automatically emits the
     * new value when it changes.
     */
    private fun observeSteps() {

        viewModelScope.launch {

            repository
                .observeTodaySteps()
                .collect { steps ->

                    updateUi(
                        steps
                    )
                }
        }
    }


    /*
     * Update all UI-related values whenever
     * today's step count changes.
     */
    private fun updateUi(
        steps: Int
    ) {

        /*
         * Get the current daily goal.
         */
        val goal =
            _uiState.value.goal


        /*
         * Calculate progress toward the goal.
         *
         * Example:
         *
         * 5,000 / 10,000 = 0.5
         *
         * 0.5 is used by the
         * CircularProgressIndicator.
         */
        val progress =
            if (goal > 0) {

                (
                        steps.toFloat() /
                                goal.toFloat()
                        ).coerceIn(
                        0f,
                        1f
                    )

            } else {

                0f
            }


        /*
         * Calculate approximate distance.
         */
        val distanceKm =
            calculateDistance(
                steps
            )


        /*
         * Calculate approximate calories.
         */
        val calories =
            calculateCalories(
                steps
            )


        /*
         * Update Compose state.
         */
        _uiState.value =
            _uiState.value.copy(

                steps = steps,

                progress = progress,

                distanceKm =
                    distanceKm,

                calories =
                    calories
            )
    }


    /*
     * Called by MainActivity after checking
     * ACTIVITY_RECOGNITION permission.
     *
     * This does NOT start the sensor.
     *
     * The sensor is controlled by
     * StepTrackingService.
     */
    fun setPermissionGranted(
        granted: Boolean
    ) {

        _uiState.value =
            _uiState.value.copy(
                hasPermission = granted
            )
    }


    /*
     * Calculate approximate walking distance.
     *
     * Current assumption:
     *
     * 1 step ≈ 0.75 meters
     *
     * This is only an estimate.
     *
     * Later we can calculate stride length
     * based on the user's height.
     */
    private fun calculateDistance(
        steps: Int
    ): Double {

        val meters =
            steps * 0.75

        return meters / 1000.0
    }


    /*
     * Calculate approximate calories.
     *
     * Current simple estimate:
     *
     * 1 step ≈ 0.04 kcal
     *
     * This is NOT a medically accurate
     * calorie calculation.
     *
     * We will improve this later using
     * user profile information.
     */
    private fun calculateCalories(
        steps: Int
    ): Int {

        return (
                steps * 0.04
                ).roundToInt()
    }
}
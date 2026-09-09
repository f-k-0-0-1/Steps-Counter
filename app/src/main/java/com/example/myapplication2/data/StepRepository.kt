package com.example.myapplication2.data

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class StepRepository(
    private val dataStore: StepDataStore
) {

    suspend fun calculateTodaySteps(
        sensorSteps: Long
    ): Int {

        val today =
            LocalDate.now().toString()

        val savedDate =
            dataStore.getBaselineDate()

        val savedBaseline =
            dataStore.getBaseline()

        /*
         * First sensor reading.
         */
        if (
            savedBaseline == null ||
            savedDate == null
        ) {

            dataStore.saveBaseline(
                baseline = sensorSteps,
                date = today
            )

            return 0
        }

        /*
         * New calendar day.
         */
        if (savedDate != today) {

            dataStore.saveBaseline(
                baseline = sensorSteps,
                date = today
            )

            return 0
        }

        /*
         * Calculate today's steps.
         */
        val steps =
            sensorSteps - savedBaseline

        /*
         * Sensor may have reset after
         * a device reboot.
         */
        if (steps < 0) {

            dataStore.saveBaseline(
                baseline = sensorSteps,
                date = today
            )

            return 0
        }

        val todaySteps =
            steps.toInt()

        dataStore.saveTodaySteps(
            todaySteps
        )

        return todaySteps
    }

    fun observeTodaySteps(): Flow<Int> {

        return dataStore.observeTodaySteps()
    }

    suspend fun getGoal(): Int {

        return dataStore.getGoal()
    }

    suspend fun saveGoal(
        goal: Int
    ) {

        dataStore.saveGoal(goal)
    }
}
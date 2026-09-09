package com.example.myapplication2.data

import android.content.Context

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = "steps_preferences"
)

class StepDataStore(
    private val context: Context
) {

    companion object {

        private val BASELINE_KEY =
            longPreferencesKey(
                "baseline_steps"
            )

        private val DATE_KEY =
            stringPreferencesKey(
                "baseline_date"
            )

        private val GOAL_KEY =
            intPreferencesKey(
                "daily_goal"
            )

        private val TODAY_STEPS_KEY =
            intPreferencesKey(
                "today_steps"
            )
    }

    suspend fun getBaseline(): Long? {

        val preferences =
            context.dataStore.data.first()

        return preferences[
            BASELINE_KEY
        ]
    }

    suspend fun getBaselineDate(): String? {

        val preferences =
            context.dataStore.data.first()

        return preferences[
            DATE_KEY
        ]
    }

    suspend fun saveBaseline(
        baseline: Long,
        date: String
    ) {

        context.dataStore.edit { preferences ->

            preferences[
                BASELINE_KEY
            ] = baseline

            preferences[
                DATE_KEY
            ] = date

            preferences[
                TODAY_STEPS_KEY
            ] = 0
        }
    }

    suspend fun getTodaySteps(): Int {

        val preferences =
            context.dataStore.data.first()

        return preferences[
            TODAY_STEPS_KEY
        ] ?: 0
    }

    suspend fun saveTodaySteps(
        steps: Int
    ) {

        context.dataStore.edit { preferences ->

            preferences[
                TODAY_STEPS_KEY
            ] = steps
        }
    }

    fun observeTodaySteps(): Flow<Int> {

        return context.dataStore.data.map {
                preferences ->

            preferences[
                TODAY_STEPS_KEY
            ] ?: 0
        }
    }

    suspend fun getGoal(): Int {

        val preferences =
            context.dataStore.data.first()

        return preferences[
            GOAL_KEY
        ] ?: 10_000
    }

    suspend fun saveGoal(
        goal: Int
    ) {

        context.dataStore.edit { preferences ->

            preferences[
                GOAL_KEY
            ] = goal
        }
    }
}
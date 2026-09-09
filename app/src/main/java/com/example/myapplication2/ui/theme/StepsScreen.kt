package com.example.myapplication2.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication2.model.StepUiState
import java.util.Locale

@Composable
fun StepsScreen(
    state: StepUiState,
    onRequestPermission: () -> Unit
) {

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Spacer(
                modifier = Modifier.height(30.dp)
            )

            Text(
                text = "Today's Steps",
                style =
                    MaterialTheme.typography.headlineSmall,
                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(30.dp)
            )

            StepProgress(
                steps = state.steps,
                progress = state.progress
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Text(
                text =
                    "${state.steps} / ${state.goal}",
                style =
                    MaterialTheme.typography.titleMedium
            )

            Spacer(
                modifier = Modifier.height(30.dp)
            )

            StatsRow(
                distanceKm =
                    state.distanceKm,
                calories =
                    state.calories
            )

            Spacer(
                modifier = Modifier.height(30.dp)
            )

            if (!state.hasPermission) {

                Card(
                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier =
                            Modifier.padding(20.dp),
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Text(
                            text =
                                "Physical activity permission is required to count your steps."
                        )

                        Spacer(
                            modifier =
                                Modifier.height(16.dp)
                        )

                        Button(
                            onClick =
                                onRequestPermission
                        ) {

                            Text(
                                text =
                                    "Allow Permission"
                            )
                        }
                    }
                }
            }

            if (!state.sensorAvailable) {

                Spacer(
                    modifier =
                        Modifier.height(16.dp)
                )

                Text(
                    text =
                        "A step counter sensor is not available on this device.",
                    color =
                        MaterialTheme.colorScheme.error
                )
            }

            if (state.isTracking) {

                Spacer(
                    modifier =
                        Modifier.height(16.dp)
                )

                Text(
                    text =
                        "Step tracking is active.",
                    color =
                        MaterialTheme.colorScheme.primary
                )
            }

            state.errorMessage?.let { message ->

                Spacer(
                    modifier =
                        Modifier.height(16.dp)
                )

                Text(
                    text = message,
                    color =
                        MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun StepProgress(
    steps: Int,
    progress: Float
) {

    Box(
        modifier = Modifier.size(220.dp),
        contentAlignment =
            Alignment.Center
    ) {

        CircularProgressIndicator(
            progress = {
                progress
            },
            modifier =
                Modifier.fillMaxSize(),
            strokeWidth = 18.dp
        )

        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector =
                    Icons.Default.DirectionsWalk,
                contentDescription =
                    "Walking",
                modifier =
                    Modifier.size(35.dp)
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text = steps.toString(),
                style =
                    MaterialTheme.typography.headlineMedium,
                fontWeight =
                    FontWeight.Bold
            )

            Text(
                text = "steps"
            )
        }
    }
}

@Composable
private fun StatsRow(
    distanceKm: Double,
    calories: Int
) {

    Row(
        modifier =
            Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {

        StatCard(
            modifier =
                Modifier.weight(1f),
            title = "Distance",
            value =
                String.format(
                    Locale.US,
                    "%.2f km",
                    distanceKm
                ),
            icon = {
                Icon(
                    imageVector =
                        Icons.Default.Route,
                    contentDescription =
                        "Distance"
                )
            }
        )

        StatCard(
            modifier =
                Modifier.weight(1f),
            title = "Calories",
            value =
                "$calories kcal",
            icon = {
                Icon(
                    imageVector =
                        Icons.Default.LocalFireDepartment,
                    contentDescription =
                        "Calories"
                )
            }
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier,
    title: String,
    value: String,
    icon: @Composable () -> Unit
) {

    Card(
        modifier = modifier
    ) {

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            icon()

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text = title
            )

            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )

            Text(
                text = value,
                fontWeight =
                    FontWeight.Bold
            )
        }
    }
}
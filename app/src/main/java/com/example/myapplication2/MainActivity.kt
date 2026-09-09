package com.example.myapplication2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.core.content.ContextCompat

import com.example.myapplication2.service.StepTrackingService
import com.example.myapplication2.ui.StepsScreen
import com.example.myapplication2.ui.theme.StepsCounterTheme
import com.example.myapplication2.viewmodel.StepsViewModel


class MainActivity : ComponentActivity() {

    private val viewModel:
            StepsViewModel by viewModels()


    /*
     * Activity Recognition permission launcher.
     */
    private val activityRecognitionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            /*
             * Update the Compose UI state.
             */
            viewModel.setPermissionGranted(
                granted
            )

            /*
             * If permission was granted,
             * continue with notification permission.
             */
            if (granted) {

                requestNotificationPermission()
            }
        }


    /*
     * Notification permission launcher.
     */
    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            /*
             * Notification permission does not
             * affect Activity Recognition.
             *
             * Start the step tracking service
             * regardless of whether notification
             * permission was granted.
             */
            startStepTrackingService()
        }


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )


        setContent {

            StepsCounterTheme {

                val state by
                viewModel.uiState
                    .collectAsState()


                /*
                 * Check permissions when the
                 * Activity is created.
                 */
                LaunchedEffect(Unit) {

                    checkPermissionsAndStart()
                }


                StepsScreen(
                    state = state,

                    onRequestPermission = {

                        checkPermissionsAndStart(
                            requestIfMissing = true
                        )
                    }
                )
            }
        }
    }


    /*
     * Check required permissions and start
     * the foreground service when possible.
     */
    private fun checkPermissionsAndStart(
        requestIfMissing: Boolean = false
    ) {

        /*
         * Android versions below Android 10
         * don't require ACTIVITY_RECOGNITION.
         */
        if (Build.VERSION.SDK_INT < 29) {

            viewModel.setPermissionGranted(
                true
            )

            requestNotificationPermission()

            return
        }


        /*
         * Check Activity Recognition permission.
         */
        val activityRecognitionGranted =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED


        /*
         * IMPORTANT:
         *
         * Keep the Compose UI synchronized
         * with Android's actual permission state.
         */
        viewModel.setPermissionGranted(
            activityRecognitionGranted
        )


        if (!activityRecognitionGranted) {

            /*
             * Don't automatically open the
             * permission dialog when the app
             * first launches.
             */
            if (requestIfMissing) {

                activityRecognitionLauncher.launch(
                    Manifest.permission.ACTIVITY_RECOGNITION
                )
            }

            return
        }


        /*
         * Activity Recognition is already granted.
         */
        requestNotificationPermission()
    }


    /*
     * Request notification permission on
     * Android 13 and higher.
     */
    private fun requestNotificationPermission() {

        if (Build.VERSION.SDK_INT >= 33) {

            val notificationGranted =
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED


            if (!notificationGranted) {

                notificationPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )

                return
            }
        }


        /*
         * Notification permission is either:
         *
         * - already granted
         * - not required on this Android version
         *
         * Start the foreground service.
         */
        startStepTrackingService()
    }


    /*
     * Start the foreground step-tracking service.
     */
    private fun startStepTrackingService() {

        val serviceIntent =
            Intent(
                this,
                StepTrackingService::class.java
            ).apply {

                action =
                    StepTrackingService.ACTION_START
            }


        ContextCompat.startForegroundService(
            this,
            serviceIntent
        )
    }
}
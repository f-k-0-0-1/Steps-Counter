package com.example.myapplication2.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.content.pm.ServiceInfo

import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat

import com.example.myapplication2.R
import com.example.myapplication2.data.StepDataStore
import com.example.myapplication2.data.StepRepository
import com.example.myapplication2.data.StepSensorManager

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class StepTrackingService : Service() {

    companion object {

        private const val CHANNEL_ID =
            "step_tracking_channel"

        private const val NOTIFICATION_ID =
            1001

        const val ACTION_START =
            "com.example.myapplication2.action.START"

        const val ACTION_STOP =
            "com.example.myapplication2.action.STOP"
    }

    private val serviceScope =
        CoroutineScope(
            SupervisorJob() + Dispatchers.IO
        )

    private lateinit var sensorManager:
            StepSensorManager

    private lateinit var repository:
            StepRepository

    override fun onCreate() {

        super.onCreate()

        val dataStore =
            StepDataStore(applicationContext)

        repository =
            StepRepository(dataStore)

        sensorManager =
            StepSensorManager(applicationContext)

        createNotificationChannel()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        when (intent?.action) {

            ACTION_STOP -> {

                stopTrackingService()

                return START_NOT_STICKY
            }

            else -> {

                startTrackingService()
            }
        }

        return START_STICKY
    }

    private fun startTrackingService() {

        val notification =
            createNotification(
                steps = 0
            )

        if (Build.VERSION.SDK_INT >= 34) {

            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH
            )

        } else {

            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                notification,
                0
            )
        }

        startSensorListening()
    }

    private fun startSensorListening() {

        if (!sensorManager.isSensorAvailable) {
            return
        }

        sensorManager.startListening { sensorSteps ->

            serviceScope.launch {

                val todaySteps =
                    repository.calculateTodaySteps(
                        sensorSteps
                    )

                updateNotification(
                    todaySteps
                )
            }
        }
    }

    private fun updateNotification(
        steps: Int
    ) {

        val notification =
            createNotification(
                steps
            )

        val notificationManager =
            getSystemService(
                NotificationManager::class.java
            )

        notificationManager.notify(
            NOTIFICATION_ID,
            notification
        )
    }

    private fun createNotification(
        steps: Int
    ): Notification {

        return NotificationCompat.Builder(
            this,
            CHANNEL_ID
        )
            .setContentTitle(
                "Steps Counter"
            )
            .setContentText(
                "$steps steps today • Tracking active"
            )
            .setSmallIcon(
                R.drawable.ic_launcher_foreground
            )
            .setOngoing(true)
            .setCategory(
                NotificationCompat.CATEGORY_SERVICE
            )
            .setPriority(
                NotificationCompat.PRIORITY_LOW
            )
            .build()
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    "Step Tracking",
                    NotificationManager.IMPORTANCE_LOW
                )

            channel.description =
                "Shows that step tracking is active."

            val notificationManager =
                getSystemService(
                    NotificationManager::class.java
                )

            notificationManager.createNotificationChannel(
                channel
            )
        }
    }

    private fun stopTrackingService() {

        sensorManager.stopListening()

        ServiceCompat.stopForeground(
            this,
            ServiceCompat.STOP_FOREGROUND_REMOVE
        )

        stopSelf()
    }

    override fun onDestroy() {

        sensorManager.stopListening()

        serviceScope.cancel()

        super.onDestroy()
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? {

        return null
    }
}
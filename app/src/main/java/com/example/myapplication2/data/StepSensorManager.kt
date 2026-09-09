package com.example.myapplication2.data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class StepSensorManager(
    context: Context
) : SensorEventListener {

    private val sensorManager =
        context.getSystemService(
            Context.SENSOR_SERVICE
        ) as SensorManager

    private val stepSensor =
        sensorManager.getDefaultSensor(
            Sensor.TYPE_STEP_COUNTER
        )

    private var onStepChanged:
            ((Long) -> Unit)? = null

    val isSensorAvailable: Boolean
        get() = stepSensor != null

    fun startListening(
        callback: (Long) -> Unit
    ) {

        onStepChanged = callback

        if (stepSensor != null) {

            sensorManager.registerListener(
                this,
                stepSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    fun stopListening() {

        sensorManager.unregisterListener(
            this
        )

        onStepChanged = null
    }

    override fun onSensorChanged(
        event: SensorEvent?
    ) {

        if (event == null) {
            return
        }

        if (
            event.sensor.type ==
            Sensor.TYPE_STEP_COUNTER
        ) {

            val steps =
                event.values[0].toLong()

            onStepChanged?.invoke(
                steps
            )
        }
    }

    override fun onAccuracyChanged(
        sensor: Sensor?,
        accuracy: Int
    ) {
    }
}
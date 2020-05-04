package com.example.rotationtracker.sensor

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class Registerer(application: Application) {

    private var sensorManager: SensorManager =
        application.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private var rotationSens: Sensor? = null
    private var isSensRot = false

    fun registerListeners(listener: SensorEventListener) {
        sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)?.let {
            rotationSens = it
            sensorManager.registerListener(listener, rotationSens, SensorManager.SENSOR_DELAY_FASTEST)
            isSensRot = true
        }
    }

    fun unregisterListeners(listener: SensorEventListener) {
        if (isSensRot) {
            sensorManager.unregisterListener(listener, rotationSens)
            isSensRot = false
        }
    }
}
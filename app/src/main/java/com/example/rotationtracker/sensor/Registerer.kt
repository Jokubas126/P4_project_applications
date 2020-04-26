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
    private var accelSens: Sensor? = null
    private var magnetSens: Sensor? = null
    private var isSensRot = false
    private var isSensAccel = false
    private var isSensMagnet = false

    fun registerListeners(listener: SensorEventListener) {
        sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)?.let {
            rotationSens = it
            sensorManager.registerListener(listener, rotationSens, SensorManager.SENSOR_DELAY_FASTEST)
            isSensRot = true
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let {
            accelSens = it
            sensorManager.registerListener(listener, accelSens, SensorManager.SENSOR_DELAY_FASTEST)
            isSensAccel = true
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.let {
            magnetSens = it
            sensorManager.registerListener(listener, magnetSens, SensorManager.SENSOR_DELAY_FASTEST)
            isSensMagnet = true
        }
    }

    fun unregisterListeners(listener: SensorEventListener) {
        if (isSensRot) {
            sensorManager.unregisterListener(listener, rotationSens)
            isSensRot = false
        }
        if (isSensAccel) {
            sensorManager.unregisterListener(listener, accelSens)
            isSensAccel = false
        }
        if (isSensMagnet) {
            sensorManager.unregisterListener(listener, magnetSens)
            isSensMagnet = false
        }
    }

}
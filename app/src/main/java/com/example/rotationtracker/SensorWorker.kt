package com.example.rotationtracker

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class SensorWorker(application: Application, private val listener: ValueChangedListener) :
    SensorEventListener {

    private var sensorManager: SensorManager =
        application.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private var rotationSens: Sensor? = null
    private var sensRot = false

    //arrays of floats to store the information about the rotation sensor
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(9)

    private var startTime: Long? = null

    private var isCheckAllowed = false

    private val timeList = mutableListOf<Long>()
    private val sensorValueList = mutableListOf<FloatArray>()

    private val dataFormatter = DataFormatter()

    interface ValueChangedListener {
        fun onValueChanged(values: FloatArray)
        fun onSave(timeList: List<Long>, directionList: List<FloatArray>)
    }

    fun registerListeners() {
        sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR).let {
            rotationSens = it
            sensorManager.registerListener(this, rotationSens, SensorManager.SENSOR_DELAY_FASTEST)
            sensRot = true
        }

        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                isCheckAllowed = true
            }
        }, 0, 10)
    }

    override fun onSensorChanged(event: SensorEvent) {
        CoroutineScope(Dispatchers.Default).launch {
            if (isCheckAllowed) {
                isCheckAllowed = false
                checkSensorData(event,
                    startTime?.let { System.currentTimeMillis() - it }
                        ?: run {
                            startTime = System.currentTimeMillis()
                            startTime
                        })
            }
        }
    }

    private fun checkSensorData(event: SensorEvent, timePassed: Long?) {
        CoroutineScope(Dispatchers.Default).launch {
            val angles = FloatArray(2)
            if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                for (i in angles.indices) {
                    angles[i] = Math.toDegrees(
                        SensorManager.getOrientation(
                            rotationMatrix,
                            orientationAngles
                        )[i].toDouble()
                    ).toFloat()
                }
            }
            addValue(dataFormatter.formatRawAngles(angles), timePassed!!)
        }
    }

    fun onCalibrate() {
        CoroutineScope(Dispatchers.Default).launch {
            dataFormatter.xAxisCalibrationBias = Math.toDegrees(
                SensorManager.getOrientation(rotationMatrix, orientationAngles)[0].toDouble()
            ).toFloat()
        }
    }

    // ----------- Adding -----------------//

    private fun addValue(angles: FloatArray, timePassed: Long) {
        CoroutineScope(Dispatchers.Default).launch {
            timeList.add(timePassed)
            sensorValueList.add(angles)
            withContext(Dispatchers.Main) {
                listener.onValueChanged(angles)
            }
        }
    }

    fun onFinish() {
        unregisterListeners()
        timeList.removeAt(0)
        sensorValueList.removeAt(0)
        listener.onSave(timeList, sensorValueList)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun unregisterListeners() {
        if (sensRot) {
            sensorManager.unregisterListener(this, rotationSens)
            sensRot = false;
        }
    }
}
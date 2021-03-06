package com.example.rotationtracker.sensor

import android.app.Application
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.coroutines.CoroutineContext

class SensorWorker(application: Application, private val listener: ValueChangedListener) : CoroutineScope,
    SensorEventListener {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default

    //arrays of floats to store the information about the rotation sensor
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(9)

    private var startTime: Long? = null

    private var isCheckAllowed = false

    private val timeList = mutableListOf<Long>()
    private val sensorValueList = mutableListOf<FloatArray>()

    private val dataFormatter = SensorDataFormatter()

    private val registerer = Registerer(application)

    interface ValueChangedListener {
        fun onValueChanged(values: FloatArray)
        fun onSave(timeList: List<Long>, directionList: List<FloatArray>)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent) {
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

    private fun checkSensorData(event: SensorEvent, timePassed: Long?) {
        launch {
            synchronized(this){
                if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR){
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                    val angles = FloatArray(2)
                    for (i in angles.indices) {
                        angles[i] = Math.toDegrees(
                            SensorManager.getOrientation(
                                rotationMatrix,
                                orientationAngles
                            )[i].toDouble()
                        ).toFloat()
                    }
                    addValue(dataFormatter.formatRawAngles(angles), timePassed!!)
                }
            }
        }
    }

    fun onCalibrate() {
        dataFormatter.xAxisCalibrationBias = sensorValueList.last()[0]
    }

    // ----------- Adding -----------------//

    private fun addValue(angles: FloatArray, timePassed: Long) {
        launch {
            timeList.add(timePassed)
            sensorValueList.add(angles)
            withContext(Dispatchers.Main) {
                listener.onValueChanged(angles)
            }
        }
    }

    fun onStart() {
        registerer.registerListeners(this)

        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                isCheckAllowed = true
            }
        }, 0, 10)
    }

    fun onFinish() {
        registerer.unregisterListeners(this)
        timeList.removeAt(0)
        sensorValueList.removeAt(0)
        listener.onSave(timeList, sensorValueList)
    }
}
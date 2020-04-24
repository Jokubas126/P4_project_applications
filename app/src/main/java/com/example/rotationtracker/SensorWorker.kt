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

    private var calibrationBias: Float? = null

    private var start: Long? = null

    private var isCheckAllowed = false

    private val timeList = mutableListOf<Long>()
    private val sensorValueList = mutableListOf<String>()

    interface ValueChangedListener {
        fun onValueChanged(value: String)
        fun onSave(timeList: List<Long>, valueList: List<String>)
    }

    fun registerListeners() {
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null
            && sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) != null
        ) {
            rotationSens = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
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
                    start?.let { System.currentTimeMillis() - it }
                        ?: run {
                            start = System.currentTimeMillis()
                            start
                        })
            }
        }
    }

    private fun checkSensorData(event: SensorEvent, timePassed: Long?) {
        CoroutineScope(Dispatchers.Default).launch {
            var angle = 0.0f
            if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                angle = Math.toDegrees(
                    SensorManager.getOrientation(
                        rotationMatrix,
                        orientationAngles
                    )[0].toDouble()
                ).toFloat()
            }
            addValue(formatRawAngle(angle), timePassed!!)
        }
    }

    private fun formatRawAngle(angle: Float): Float {
        return getAngleWithCalibrationBias(shiftAngle(angle))
    }

    private fun getAngleWithCalibrationBias(angle: Float): Float {
        return calibrationBias?.let {
            val changedAngle = angle - it
            when {
                changedAngle > 360 -> changedAngle - 360
                changedAngle < 0 -> changedAngle + 360
                else -> changedAngle
            }
        } ?: run { angle }
    }

    private fun shiftAngle(angle: Float): Float {
        //converts angle from -180->0->180 to 0->180->360
        return (angle + 360) % 360
    }

    private fun addValue(angle: Float, timePassed: Long) {
        CoroutineScope(Dispatchers.Default).launch {
            timeList.add(timePassed)
            sensorValueList.add("%.2f".format(angle))
            withContext(Dispatchers.Main){
                listener.onValueChanged("%.2f".format(angle))
            }
        }
    }

    fun onCalibrateClicked() {
        CoroutineScope(Dispatchers.Default).launch {
            val angle = Math.toDegrees(
                SensorManager.getOrientation(rotationMatrix, orientationAngles)[0].toDouble()
            ).toFloat()
            calibrationBias = shiftAngle(angle)

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

/*private fun mapAngleToSingular(angle: Float): Float {
        // map from -180 to 180 on -1 to 1
        var singular = 0.0f
        if (angle >= -180 && angle < -90)
            singular = -((angle + 180) / 90)
        else if (angle >= -90 && angle <= 90)
            singular = angle / 90
        else if (angle > 90 && angle <= 180)
            singular = -((angle - 180) / 90)
        return singular
    }*/
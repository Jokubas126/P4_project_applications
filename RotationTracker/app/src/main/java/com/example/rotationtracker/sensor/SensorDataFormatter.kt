package com.example.rotationtracker.sensor

class SensorDataFormatter {

    fun formatRawAngles(angles: FloatArray): FloatArray {
        // format x axis
        angles[0] = getXAxisWithCalibrationBias(angles[0])

        // format y axis with reversed angles
        angles[1] = restrictYAxis(-angles[1])
        return angles
    }

    // -------------- Calibration ----------------//

    var xAxisCalibrationBias: Float? = null

    private fun getXAxisWithCalibrationBias(angle: Float): Float {
        return xAxisCalibrationBias?.let {
            val changedAngle = angle - it
            when {
                changedAngle > 180 -> changedAngle - 360
                changedAngle < -180 -> changedAngle + 360
                else -> changedAngle
            }
        } ?: run { angle }
    }


    // ---------------- Mapping ------------//

    private fun restrictYAxis(angle: Float): Float {
        return when {
            angle > 90 -> 90.0f
            angle < -40 -> -40.0f
            else -> angle
        }
    }
}
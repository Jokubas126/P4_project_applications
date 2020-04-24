package com.example.rotationtracker

class DataFormatter {

    var xAxisCalibrationBias: Float? = null
        set(value) {
            field = value?.let { mapXAxis(it) }
        }

    fun formatRawAngles(angles: FloatArray): FloatArray {
        // format x axis
        angles[0] = getXAxisWithCalibrationBias(mapXAxis(angles[0]))

        // format y axis with reversed angles
        angles[1] = restrictYAxis(-angles[1])
        return angles
    }

    // -------------- Calibration ----------------//

    private fun getXAxisWithCalibrationBias(angle: Float): Float {
        return xAxisCalibrationBias?.let {
            val changedAngle = angle - it
            when {
                changedAngle > 360 -> changedAngle - 360
                changedAngle < 0 -> changedAngle + 360
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

    private fun mapXAxis(angle: Float): Float {
        //converts angle from -180->0->180 to 0->180->360
        return (angle + 360) % 360
    }

}
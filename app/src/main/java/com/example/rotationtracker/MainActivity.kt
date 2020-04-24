package com.example.rotationtracker

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity(), SensorWorker.ValueChangedListener {

    private var sensorWorker: SensorWorker? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        start_btn.setOnClickListener {
            work()
            calibrate_btn.visibility = View.VISIBLE
            finish_btn.visibility = View.VISIBLE
            it.visibility = View.GONE
            status_text_view.text = "STARTED"
        }

        calibrate_btn.setOnClickListener { sensorWorker?.onCalibrate() }

        finish_btn.setOnClickListener {
            sensorWorker?.onFinish()
            status_text_view.text = "STOPPED"
            start_btn.visibility = View.VISIBLE
            calibrate_btn.visibility = View.GONE
            finish_btn.visibility = View.GONE
        }
    }

    private fun work() {
        sensorWorker = SensorWorker(application, this)
        sensorWorker?.registerListeners()
    }

    override fun onValueChanged(values: FloatArray) {
        x_text_view.text = "%.1f".format(values[0])
        y_text_view.text = "%.1f".format(values[1])
    }

    override fun onSave(timeList: List<Long>, directionList: List<FloatArray>) {
        FileFormatter().writeToFile(this, timeList, directionList)
    }

    override fun onPause() {
        super.onPause()
        sensorWorker?.unregisterListeners()
    }
}

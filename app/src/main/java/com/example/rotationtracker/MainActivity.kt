package com.example.rotationtracker

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.StringBuilder

class MainActivity : AppCompatActivity(), SensorWorker.ValueChangedListener {

    private var sensorWorker: SensorWorker? = null

    private val stringBuilder = StringBuilder()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        start_btn.setOnClickListener {
            work()
            status_text_view.text = "STARTED"
        }
        calibrate_btn.setOnClickListener { sensorWorker?.onCalibrateClicked() }
        finish_btn.setOnClickListener {
            sensorWorker?.onFinish()
            status_text_view.text = "STOPPED"
        }
    }

    private fun work() {
        sensorWorker = SensorWorker(application, this)
        sensorWorker?.registerListeners()
    }

    override fun onValueChanged(value: String) {
        status_text_view.text = value
    }

    override fun onSave(timeList: List<Long>, valueList: List<String>) {
        timeList.forEachIndexed { index, _ ->
            stringBuilder.append("time ").append(timeList[index]).append("\n")

            stringBuilder.append("direction ").append(valueList[index]).append("\n")
        }
        writeToTextFile()
    }

    private fun writeToTextFile() {
        val file = File(getExternalFilesDir(""), "Recording.csv")
        try {
            val fileOutPutStream = FileOutputStream(file)
            fileOutPutStream.write(stringBuilder.toString().toByteArray())
            fileOutPutStream.close()
            Toast.makeText(this, "Saved in: " + file.absolutePath, Toast.LENGTH_LONG)
                .show()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
        sensorWorker?.unregisterListeners()
    }
}

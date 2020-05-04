package com.example.rotationtracker

import android.content.Context
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.StringBuilder

class FileFormatter {

    private fun dataListsToString(timeList: List<Long>, directionList: List<FloatArray>): String {
        val stringBuilder = StringBuilder()
        timeList.forEachIndexed { index, _ ->
            stringBuilder.append("time ").append(timeList[index]).append("\n")

            stringBuilder.append("direction ")
            for (value in directionList[index])
                stringBuilder.append("%.2f".format(value)).append(" ")

            stringBuilder.append("\n")
        }
        return stringBuilder.toString()
    }

    fun writeToFile(context: Context, timeList: List<Long>, directionList: List<FloatArray>) {
        val string = dataListsToString(timeList, directionList)
        val file = File(context.getExternalFilesDir(""), "head_direction_recording.csv")
        try {
            val fileOutPutStream = FileOutputStream(file)
            fileOutPutStream.write(string.toByteArray())
            fileOutPutStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        Toast.makeText(context, "Saved in: " + file.absolutePath, Toast.LENGTH_LONG).show()
    }
}
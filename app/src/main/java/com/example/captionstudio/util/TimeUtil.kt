package com.example.captionstudio.util

import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

object TimeUtil {
    fun convertMillisToTimeString(milliSeconds: Long): String {
        val millis = milliSeconds
        val twoDigitFormat = DecimalFormat("00")
        val threeDigitFormat = DecimalFormat("000")

        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val mins = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        val secs = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
        val ms = milliSeconds % 1000

        return java.lang.String.format(
            "%s:%s:%s.%s",
            twoDigitFormat.format(hours), twoDigitFormat.format(mins), twoDigitFormat.format(secs),
            threeDigitFormat.format(ms)
        )
    }

    fun convertSecondsToTimeString(seconds: Int): String {
        val mins = seconds / 60
        val secs = seconds % 60

        return String.format("%02d:%02d", mins, secs)
    }
}
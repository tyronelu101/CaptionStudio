package com.example.captionstudio.util

import java.text.DecimalFormat
import java.util.Locale
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

        return String.format(
            Locale.US,
            "%s:%s:%s.%s",
            twoDigitFormat.format(hours), twoDigitFormat.format(mins), twoDigitFormat.format(secs),
            threeDigitFormat.format(ms)
        )
    }

    fun convertSecondsToTimeString(seconds: Int): String {
        val mins = seconds / 60
        val secs = seconds % 60

        return String.format(Locale.US, "%02d:%02d", mins, secs)
    }

    fun convertMillisecondsToSrtTime(ms: Long): String {
        val hours = ms / 3_600_000
        val minutes = (ms % 3_600_000) / 60_000
        val seconds = (ms % 60_000) / 1000
        val millis = ms % 1000

        return String.format(Locale.US, "%02d:%02d:%02d,%03d", hours, minutes, seconds, millis)
    }
}
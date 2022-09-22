package com.decomposepermissions.utils

import kotlinx.datetime.Clock.System
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TIME_PATTERN = "HH:mm:ss"

data class LogData(val log: String) {

    companion object {

        fun build(title: String, log: String) = LogData(
            log = "${getTimeString()} | $title $log"
        )

        private fun getTimeString(): String {
            val formatter = SimpleDateFormat(TIME_PATTERN, Locale.getDefault())
            return formatter.format(Date(System.now().toEpochMilliseconds()))
        }
    }
}

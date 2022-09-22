package com.decomposepermissions.home.ui

import androidx.compose.runtime.MutableState

interface HomeComponent {

    val logsState: MutableState<List<LogData>>

    fun onRequestPermissionClick()

    fun onRequestMultiplePermission()

    fun onClearAppData()
}

package com.decomposepermissions.home.ui

import com.decomposepermissions.utils.LogData

interface HomeComponent {

    val logsState: List<LogData>

    fun onRequestPermissionClick()

    fun onRequestMultiplePermission()

    fun onRequestPermissionFromChild()

    fun onClearAppData()

    sealed interface Output {
        object MultiPaneRequested : Output
    }
}

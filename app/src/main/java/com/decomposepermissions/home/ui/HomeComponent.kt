package com.decomposepermissions.home.ui

import androidx.compose.runtime.MutableState
import com.decomposepermissions.utils.LogData

interface HomeComponent {

    val logsState: MutableState<List<LogData>>

    fun onRequestPermissionClick()

    fun onRequestMultiplePermission()

    fun onRequestPermissionFromChild()

    fun onClearAppData()

    sealed interface Output {
        object MultiPaneRequested : Output
    }
}

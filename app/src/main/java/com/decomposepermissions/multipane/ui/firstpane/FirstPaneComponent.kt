package com.decomposepermissions.multipane.ui.firstpane

import androidx.compose.runtime.MutableState
import com.decomposepermissions.utils.LogData

interface FirstPaneComponent {
    val logsState: MutableState<List<LogData>>
}

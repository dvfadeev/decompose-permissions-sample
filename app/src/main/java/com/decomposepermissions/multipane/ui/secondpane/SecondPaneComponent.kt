package com.decomposepermissions.multipane.ui.secondpane

import androidx.compose.runtime.MutableState
import com.decomposepermissions.utils.LogData

interface SecondPaneComponent {
    val logsState: MutableState<List<LogData>>
}

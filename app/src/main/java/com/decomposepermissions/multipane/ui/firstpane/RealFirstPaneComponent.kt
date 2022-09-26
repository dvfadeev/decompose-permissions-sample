package com.decomposepermissions.multipane.ui.firstpane

import android.Manifest
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import com.decomposepermissions.permissions.PermissionManager
import com.decomposepermissions.utils.LogData
import com.decomposepermissions.utils.PERMISSION_ACCESS_LOCATION
import com.decomposepermissions.utils.componentCoroutineScope
import com.decomposepermissions.utils.toMessage
import kotlinx.coroutines.launch

class RealFirstPaneComponent(
    componentContext: ComponentContext,
    private val permissionManager: PermissionManager
) : ComponentContext by componentContext, FirstPaneComponent {

    private val coroutineScope = componentCoroutineScope()

    override val logsState: MutableState<List<LogData>> = mutableStateOf(listOf())

    init {
        requestPermission()
    }

    private fun requestPermission() {
        coroutineScope.launch {
            val message = permissionManager.requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION).toMessage()
            showLog(message)
        }
    }

    private fun showLog(log: String) {
        logsState.value = logsState.value.toMutableList().apply {
            add(
                LogData.build(PERMISSION_ACCESS_LOCATION, log)
            )
        }
    }
}

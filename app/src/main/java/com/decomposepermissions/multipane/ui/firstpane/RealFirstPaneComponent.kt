package com.decomposepermissions.multipane.ui.firstpane

import android.Manifest
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import com.decomposepermissions.permissions.PermissionManager
import com.decomposepermissions.utils.LogData
import com.decomposepermissions.utils.PERMISSION_ACCESS_LOCATION
import com.decomposepermissions.utils.STATUS_AUTO_DENIED
import com.decomposepermissions.utils.STATUS_DENIED
import com.decomposepermissions.utils.STATUS_GRANTED

class RealFirstPaneComponent(
    componentContext: ComponentContext,
    permissionManager: PermissionManager
) : ComponentContext by componentContext, FirstPaneComponent {

    override val logsState: MutableState<List<LogData>> = mutableStateOf(listOf())

    init {
        permissionManager.requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION).onGranted {
            showLog(STATUS_GRANTED)
        }.onDenied {
            showLog(STATUS_DENIED)
        }.onAutoDenied {
            showLog(STATUS_AUTO_DENIED)
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

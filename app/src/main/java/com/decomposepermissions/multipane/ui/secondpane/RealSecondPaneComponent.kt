package com.decomposepermissions.multipane.ui.secondpane

import android.Manifest
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import com.decomposepermissions.permissions.PermissionManager
import com.decomposepermissions.utils.LogData
import com.decomposepermissions.utils.PERMISSION_READ_SMS
import com.decomposepermissions.utils.STATUS_AUTO_DENIED
import com.decomposepermissions.utils.STATUS_DENIED
import com.decomposepermissions.utils.STATUS_GRANTED

class RealSecondPaneComponent(
    componentContext: ComponentContext,
    permissionManager: PermissionManager
) : ComponentContext by componentContext, SecondPaneComponent {

    override val logsState: MutableState<List<LogData>> = mutableStateOf(listOf())

    init {
        permissionManager.requestPermission(Manifest.permission.SEND_SMS).onGranted {
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
                LogData.build(PERMISSION_READ_SMS, log)
            )
        }
    }
}

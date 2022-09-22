package com.decomposepermissions.home.ui

import android.Manifest
import android.app.ActivityManager
import android.content.Context.ACTIVITY_SERVICE
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import com.decomposepermissions.permissions.PermissionManager
import com.decomposepermissions.utils.ActivityProvider
import com.decomposepermissions.utils.LogData
import com.decomposepermissions.utils.PERMISSION_CAMERA
import com.decomposepermissions.utils.PERMISSION_READ_CONTACTS
import com.decomposepermissions.utils.PERMISSION_READ_STORAGE
import com.decomposepermissions.utils.STATUS_AUTO_DENIED
import com.decomposepermissions.utils.STATUS_DENIED
import com.decomposepermissions.utils.STATUS_GRANTED

class RealHomeComponent(
    componentContext: ComponentContext,
    private val activityProvider: ActivityProvider,
    private val permissionManager: PermissionManager,
    private val onOutput: (HomeComponent.Output) -> Unit
) : ComponentContext by componentContext, HomeComponent {

    override val logsState: MutableState<List<LogData>> = mutableStateOf(listOf())

    override fun onRequestPermissionClick() {
        requestPermission(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            PERMISSION_READ_STORAGE
        )
    }

    override fun onRequestMultiplePermission() {
        requestPermission(
            Manifest.permission.CAMERA,
            PERMISSION_CAMERA
        )
        requestPermission(
            Manifest.permission.READ_CONTACTS,
            PERMISSION_READ_CONTACTS
        )
    }

    override fun onRequestPermissionFromChild() {
        onOutput.invoke(HomeComponent.Output.MultiPaneRequested)
    }

    override fun onClearAppData() {
        activityProvider.activity?.let {
            (it.getSystemService(ACTIVITY_SERVICE) as ActivityManager).clearApplicationUserData()
        }
    }

    private fun requestPermission(permission: String, logTitle: String) {
        permissionManager.requestPermission(permission)
            .onGranted {
                showLog(logTitle, STATUS_GRANTED)
            }.onDenied {
                showLog(logTitle, STATUS_DENIED)
            }.onAutoDenied {
                showLog(logTitle, STATUS_AUTO_DENIED)
            }
    }

    private fun showLog(title: String, log: String) {
        logsState.value = logsState.value.toMutableList().apply {
            add(
                LogData.build(title, log)
            )
        }
    }
}

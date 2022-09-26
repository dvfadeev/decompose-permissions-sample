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
import com.decomposepermissions.utils.componentCoroutineScope
import com.decomposepermissions.utils.toMessage
import kotlinx.coroutines.launch

const val PERMISSION_MULTIPLY = "MultiplyPermission:"

class RealHomeComponent(
    componentContext: ComponentContext,
    private val activityProvider: ActivityProvider,
    private val permissionManager: PermissionManager,
    private val onOutput: (HomeComponent.Output) -> Unit
) : ComponentContext by componentContext, HomeComponent {

    private val coroutineScope = componentCoroutineScope()

    override val logsState: MutableState<List<LogData>> = mutableStateOf(listOf())

    override fun onRequestPermissionClick() {
        coroutineScope.launch {
            val permission = Manifest.permission.READ_EXTERNAL_STORAGE
            val message = permissionManager.requestPermission(permission).toMessage()
            showLog(permission, message)
        }
    }

    override fun onRequestMultiplePermission() {
        coroutineScope.launch {
            val message = permissionManager.requestPermissions(
                listOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_CONTACTS
                )
            ).toMessage()
            showLog(PERMISSION_MULTIPLY, message)
        }
    }

    override fun onRequestPermissionFromChild() {
        onOutput.invoke(HomeComponent.Output.MultiPaneRequested)
    }

    override fun onClearAppData() {
        activityProvider.activity?.let {
            (it.getSystemService(ACTIVITY_SERVICE) as ActivityManager).clearApplicationUserData()
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

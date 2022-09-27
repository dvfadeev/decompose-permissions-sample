package com.decomposepermissions.permissions

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.decomposepermissions.permissions.PermissionManager.SinglePermissionResult
import com.decomposepermissions.permissions.PermissionManager.SinglePermissionResult.Denied
import com.decomposepermissions.permissions.PermissionManager.SinglePermissionResult.Granted
import com.decomposepermissions.utils.ActivityProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

internal class SinglePermissionRequestExecutor(
    private val activityProvider: ActivityProvider
) {
    private var activityResultLauncher: ActivityResultLauncher<String>? = null
    private val permissionsResultFlow: MutableSharedFlow<Boolean> = MutableSharedFlow()

    init {
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate).launch {
            activityProvider.activityStateFlow.collect {
                if (it != null) {
                    registerLauncher(it)
                }
            }
        }
    }

    suspend fun process(permission: String): SinglePermissionResult {
        activityResultLauncher?.launch(permission)
        return if (permissionsResultFlow.first()) {
            Granted
        } else {
            val rational = activityProvider.awaitActivity().shouldShowRequestPermissionRationale(permission)
            Denied(isPermanently = !rational)
        }
    }

    private fun registerLauncher(activity: ComponentActivity) {
        activityResultLauncher = activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            activity.lifecycleScope.launch {
                permissionsResultFlow.emit(it)
            }
        }
    }
}

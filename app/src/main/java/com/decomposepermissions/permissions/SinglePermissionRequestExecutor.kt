package com.decomposepermissions.permissions

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.decomposepermissions.permissions.PermissionManager.SinglePermissionResult
import com.decomposepermissions.permissions.PermissionManager.SinglePermissionResult.Denied
import com.decomposepermissions.permissions.PermissionManager.SinglePermissionResult.Granted
import com.decomposepermissions.utils.ActivityProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

internal class SinglePermissionRequestExecutor(
    private val activityProvider: ActivityProvider
) {
    private var activityResultLauncher = MutableStateFlow<ActivityResultLauncher<String>?>(null)
    private val permissionsResultFlow = MutableSharedFlow<Boolean?>()

    init {
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate).launch {
            activityProvider.activityStateFlow.collect {
                if (it != null) {
                    registerLauncher(it)
                } else {
                    unregisterLauncher()
                }
            }
        }
    }

    suspend fun process(permission: String): SinglePermissionResult {
        activityResultLauncher.filterNotNull().first().launch(permission)
        val granted = permissionsResultFlow.first() ?: throw CancellationException()
        return if (granted) {
            Granted
        } else {
            val rational = activityProvider.awaitActivity().shouldShowRequestPermissionRationale(permission)
            Denied(isPermanently = !rational)
        }
    }

    private fun registerLauncher(activity: ComponentActivity) {
        activityResultLauncher.value = activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            activity.lifecycleScope.launch {
                permissionsResultFlow.emit(it)
            }
        }
    }

    private suspend fun unregisterLauncher() {
        activityResultLauncher.value?.unregister()
        activityResultLauncher.value = null
        permissionsResultFlow.emit(null)
    }
}

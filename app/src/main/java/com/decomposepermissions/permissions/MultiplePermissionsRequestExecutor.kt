package com.decomposepermissions.permissions

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.decomposepermissions.permissions.PermissionManager.MultiplePermissionResult
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

internal class MultiplePermissionsRequestExecutor(
    private val activityProvider: ActivityProvider
) {
    private var activityResultLauncher: ActivityResultLauncher<Array<String>>? = null
    private val permissionsResultFlow: MutableSharedFlow<Map<String, Boolean>> = MutableSharedFlow()

    init {
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate).launch {
            activityProvider.activityStateFlow.collect {
                if (it != null) {
                    registerLauncher(it)
                }
            }
        }
    }

    suspend fun process(permissions: List<String>): MultiplePermissionResult {
        val permissionResults = mutableMapOf<String, SinglePermissionResult>()
        activityResultLauncher?.launch(permissions.toTypedArray())
        permissionsResultFlow.first().forEach {
            val result = if (it.value) {
                Granted
            } else {
                val rational = activityProvider.awaitActivity().shouldShowRequestPermissionRationale(it.key)
                Denied(isPermanently = !rational)
            }
            permissionResults[it.key] = result
        }
        return MultiplePermissionResult(
            value = permissionResults
        )
    }

    private fun registerLauncher(activity: ComponentActivity) {
        activityResultLauncher =
            activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                activity.lifecycleScope.launch {
                    permissionsResultFlow.emit(it)
                }
            }
    }
}

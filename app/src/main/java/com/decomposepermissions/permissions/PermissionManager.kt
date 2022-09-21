package com.decomposepermissions.permissions

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PermissionManager {

    private var activity: ComponentActivity? = null

    private var activityResultLauncher: ActivityResultLauncher<Array<String>>? = null

    private val permissionQueueState = MutableStateFlow(PermissionQueue())

    private val permissionsResultFlow: MutableSharedFlow<Map<String, Boolean>> = MutableSharedFlow()

    private var permissionCollectJob: Job? = null

    private val prefs: SharedPreferences?
        get() = activity?.getPreferences(Context.MODE_PRIVATE)

    private val scope: LifecycleCoroutineScope?
        get() = activity?.lifecycleScope

    fun attachActivity(activity: ComponentActivity) {
        this.activity = activity
        activityResultLauncher =
            activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                scope?.launch {
                    permissionsResultFlow.emit(it)
                }
            }
    }

    fun detachActivity() {
        this.activity = null
    }

    fun requestPermission(
        permission: String
    ): Result {
        scope?.launch {
            permissionQueueState.emit(
                permissionQueueState.value.copy().apply {
                    insertPermission(permission)
                }
            )
        }

        val isGranted = activity?.let {
            ContextCompat.checkSelfPermission(
                it,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        } ?: false
        val isShowRationale = activity?.shouldShowRequestPermissionRationale(permission) ?: false

        return Result(
            permission,
            isGranted = isGranted,
            isShowRationale = isShowRationale
        )
    }

    inner class Result(
        private val permission: String,
        private var isGranted: Boolean,
        private var isShowRationale: Boolean
    ) {
        private var successAction: (() -> Unit)? = null
        private var deniedAction: (() -> Unit)? = null
        private var autoDeniedAction: (() -> Unit)? = null

        private var permissionQueueJob: Job? = scope?.launch {
            permissionQueueState.collect { queue ->
                if (queue.peek() == permission) {
                    activityResultLauncher?.launch(arrayOf(permission))
                    processPermissionsResult()
                }
            }
        }

        private var isFirstRun: Boolean
            get() = prefs?.getBoolean(permission, true) ?: true
            set(value) {
                prefs?.edit()?.apply {
                    putBoolean(permission, value)
                    apply()
                }
            }

        fun onGranted(action: () -> Unit): Result {
            successAction = action
            if (isGranted) {
                action()
            }
            return this
        }

        fun onDenied(action: () -> Unit): Result {
            deniedAction = action
            return this
        }

        fun onAutoDenied(action: () -> Unit): Result {
            autoDeniedAction = action
            return this
        }

        private fun processPermissionsResult() {
            permissionCollectJob?.cancel()
            permissionCollectJob = scope?.launch {
                if (isGranted) {
                    removeFromQueue()
                    return@launch
                }
                permissionsResultFlow.collect {
                    if (it.isEmpty()) {
                        return@collect
                    }
                    if (it[permission] == null) {
                        return@collect
                    }

                    removeFromQueue()
                    if (it[permission] == true) {
                        successAction?.invoke()
                    } else {
                        if (isFirstRun) {
                            isFirstRun = false
                        }
                        val isNeverAskAgain = !isFirstRun && !isGranted && !isShowRationale
                        if (isNeverAskAgain) {
                            autoDeniedAction?.invoke()
                        } else {
                            deniedAction?.invoke()
                        }
                    }
                }
            }
        }

        private suspend fun removeFromQueue() {
            permissionQueueJob?.cancel()
            permissionQueueState.emit(
                permissionQueueState.value.copy().apply {
                    removePermission(permission)
                }
            )
        }
    }

    data class PermissionQueue(
        private var permissions: List<String> = listOf()
    ) {

        fun insertPermission(permission: String) {
            permissions = permissions.toMutableList().apply {
                add(permission)
            }
        }

        fun removePermission(permission: String) {
            permissions = permissions.toMutableList().apply {
                remove(permission)
            }
        }

        fun peek() = permissions.firstOrNull()
    }
}

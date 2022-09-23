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

/**
 * A manager that allows you to request permissions and process their result.
 * When multiple permissions are requested, forms a queue and processes them sequentially.
 *
 * Must be tied to current activity.
 */
class PermissionManager {

    private var activity: ComponentActivity? = null

    private var activityResultLauncher: ActivityResultLauncher<String>? = null

    private val permissionQueueState = MutableStateFlow(PermissionQueue())

    private val permissionsResultFlow: MutableSharedFlow<Boolean> = MutableSharedFlow()

    private var permissionCollectJob: Job? = null

    private val prefs: SharedPreferences?
        get() = activity?.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    private val scope: LifecycleCoroutineScope?
        get() = activity?.lifecycleScope

    /**
     * [PermissionManager] tied to [ComponentActivity].
     *
     * This method must be called during activity creation.
     */
    fun attachActivity(activity: ComponentActivity) {
        this.activity = activity
        activityResultLauncher =
            activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                scope?.launch {
                    permissionsResultFlow.emit(it)
                }
            }
    }

    /**
     * [PermissionManager] tied to [ComponentActivity].
     *
     * This method must be called during activity destruction.
     */
    fun detachActivity() {
        this.activity = null
    }

    /**
     * Request permission by name from Manifest.permission.
     */
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
                    activityResultLauncher?.launch(permission)
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

        /**
         * Perform given action when permission is granted.
         *
         * Returns the original Result unchanged.
         */
        fun onGranted(action: () -> Unit): Result {
            successAction = action
            if (isGranted) {
                action()
            }
            return this
        }

        /**
         * Perform given action when permission is denied.
         *
         * Returns the original Result unchanged.
         */
        fun onDenied(action: () -> Unit): Result {
            deniedAction = action
            return this
        }

        /**
         * Perform given action when permission is denied automatically.
         * In cases where the user has selected "Never ask again".
         *
         * Returns the original Result unchanged.
         */
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
                    removeFromQueue()
                    if (it) {
                        successAction?.invoke()
                    } else {
                        val isNeverAskAgain = !isFirstRun && !isGranted && !isShowRationale
                        if (isNeverAskAgain) {
                            autoDeniedAction?.invoke()
                        } else {
                            deniedAction?.invoke()
                        }
                        if (isFirstRun) {
                            isFirstRun = false
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

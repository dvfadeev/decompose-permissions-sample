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
import kotlinx.coroutines.launch

class PermissionManager {

    private var activity: ComponentActivity? = null

    private var activityResultLauncher: ActivityResultLauncher<Array<String>>? = null

    private val permissionFlow: MutableSharedFlow<Map<String, Boolean>> = MutableSharedFlow()

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
                    permissionFlow.emit(it)
                }
            }
    }

    fun detachActivity() {
        this.activity = null
    }

    fun requestPermission(
        permission: String
    ): Result {
        val isGranted = activity?.let {
            ContextCompat.checkSelfPermission(
                it,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        } ?: false
        val isShowRationale = activity?.shouldShowRequestPermissionRationale(permission) ?: false

        activityResultLauncher?.launch(arrayOf(permission))

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

        init {
            processPermissionCollect()
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

        private fun processPermissionCollect() {
            permissionCollectJob?.cancel()
            permissionCollectJob = scope?.launch {
                if (isGranted) {
                    return@launch
                }
                permissionFlow.collect {
                    val isFirstRun = prefs?.getBoolean(permission, true) ?: true
                    if (isFirstRun) {
                        prefs?.edit()?.apply {
                            putBoolean(permission, false)
                            apply()
                        }
                    }
                    if (it[permission] == true) {
                        successAction?.invoke()
                    } else {
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
    }
}

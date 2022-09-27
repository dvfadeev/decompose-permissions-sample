package com.decomposepermissions.permissions

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.decomposepermissions.permissions.PermissionManager.SinglePermissionResult.Denied
import com.decomposepermissions.permissions.PermissionManager.SinglePermissionResult.Granted
import com.decomposepermissions.utils.ActivityProvider
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * A manager that allows you to request permissions and process their result.
 *
 * When a permission request is called multiple times, a queue is formed and processed sequentially.
 *
 * Must be tied to [ActivityProvider].
 */
class PermissionManager(
    private val activityProvider: ActivityProvider,
    private val applicationContext: Context
) {
    private val singlePermissionExecutor = SinglePermissionRequestExecutor(activityProvider)
    private val multiplePermissionsExecutor = MultiplePermissionsRequestExecutor(activityProvider)
    private val mutex = Mutex()

    /**
     * Request single permission
     * Should be called from coroutine
     */
    suspend fun requestPermission(permission: String): SinglePermissionResult {
        if (checkPermissionGranted(permission)) {
            return Granted
        }

        return mutex.withLock {
            singlePermissionExecutor.process(permission)
        }
    }

    /**
     * Request multiply permission
     * Should be called from coroutine
     */
    suspend fun requestPermissions(permissions: List<String>): MultiplePermissionResult {
        val (grantedPermissions, notGrantedPermissions) = permissions.partition {
            checkPermissionGranted(it)
        }
        val grantedPermissionsResult = MultiplePermissionResult.buildGranted(grantedPermissions)

        if (notGrantedPermissions.isEmpty()) {
            return grantedPermissionsResult
        }

        return mutex.withLock {
            grantedPermissionsResult + (multiplePermissionsExecutor.process(notGrantedPermissions))
        }
    }

    /**
     * Check if the current permission has been granted
     */
    fun checkPermissionGranted(permission: String) = ContextCompat.checkSelfPermission(
        applicationContext,
        permission
    ) == PackageManager.PERMISSION_GRANTED

    /**
     * Check if should show rationale dialog.
     * In an educational UI, explain to the user why your app requires this
     * permission for a specific feature to behave as expected.
     */
    fun checkShouldShowRationale(permission: String) =
        activityProvider.activity?.shouldShowRequestPermissionRationale(permission) ?: false

    sealed class SinglePermissionResult {

        /**
         * Permission has been granted by user
         */
        object Granted : SinglePermissionResult()

        /**
         * Permission has been denied by user
         * If [isPermanently] == true permission was denied automatically (user chose "Never ask again")
         */
        class Denied(val isPermanently: Boolean) : SinglePermissionResult()
    }

    class MultiplePermissionResult(
        val value: Map<String, SinglePermissionResult>
    ) {

        companion object {
            fun buildGranted(permissions: List<String>) = MultiplePermissionResult(
                value = permissions.associateWith {
                    Granted
                }
            )
        }

        val isEmpty: Boolean
            get() = value.isEmpty()

        val isAllGranted: Boolean
            get() = if (value.isNotEmpty()) {
                value.filter { it.value is Granted }.size == value.size
            } else false

        val isAllDenied: Boolean
            get() = if (value.isNotEmpty()) {
                value.filter { it.value is Denied }.size == value.size
            } else false

        operator fun plus(second: MultiplePermissionResult) = MultiplePermissionResult(
            value = this.value + second.value
        )
    }
}

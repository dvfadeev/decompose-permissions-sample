package com.decomposepermissions.permissions

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.decomposepermissions.permissions.PermissionManager.PermissionResult.Denied
import com.decomposepermissions.permissions.PermissionManager.PermissionResult.Granted
import com.decomposepermissions.utils.ActivityProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

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
    private val operationQueue = OperationQueue()

    /**
     * Request single permission
     * Should be called from coroutine
     */
    suspend fun requestPermission(permission: String): PermissionResult {
        val activity = activityProvider.awaitActivity()
        val scope = activity.lifecycleScope
        if (checkPermissionGranted(permission)) {
            return Granted
        }
        return operationQueue.processOperation(scope) {
            (singlePermissionExecutor.process(permission))
        } as PermissionResult
    }

    /**
     * Request multiply permission
     * Should be called from coroutine
     */
    suspend fun requestPermissions(permissions: List<String>): MultiplePermissionResult {
        val scope = activityProvider.awaitActivity().lifecycleScope
        val (grantedPermissions, notGrantedPermissions) = permissions.partition {
            checkPermissionGranted(it)
        }
        val grantedPermissionsResult = MultiplePermissionResult.buildGranted(grantedPermissions)

        if (notGrantedPermissions.isEmpty()) {
            return grantedPermissionsResult
        }

        return operationQueue.processOperation(scope) {
            (multiplePermissionsExecutor.process(notGrantedPermissions))
        } as MultiplePermissionResult + grantedPermissionsResult
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

    private class OperationQueue {

        private var queue: MutableStateFlow<List<suspend () -> Result>> = MutableStateFlow(listOf())

        private val queueValue
            get() = queue.value

        suspend fun processOperation(
            scope: CoroutineScope,
            operation: suspend () -> Result
        ): Result {
            put(operation)

            var queueCoroutine: Job? = null
            val currentOperation = suspendCancellableCoroutine { continuation ->
                queueCoroutine = scope.launch {
                    queue.collect {
                        if (it.firstOrNull() == operation && continuation.isActive) {
                            continuation.resume(operation)
                        }
                    }
                }
            }

            val result = currentOperation.invoke()
            remove(operation)
            queueCoroutine?.cancel()
            return result
        }

        private suspend fun put(operation: suspend () -> Result) {
            queue.emit(queueValue.toMutableList().apply {
                add(operation)
            })
        }

        private suspend fun remove(operation: suspend () -> Result) {
            queue.emit(queueValue.toMutableList().apply {
                remove(operation)
            })
        }
    }

    sealed class Result

    sealed class PermissionResult : Result() {

        /**
         * Permission has been granted by user
         */
        object Granted : PermissionResult()

        /**
         * Permission has been denied by user
         * If [isPermanently] == true permission was denied automatically (user chose "Never ask again")
         */
        class Denied(val isPermanently: Boolean) : PermissionResult()
    }

    class MultiplePermissionResult(
        val value: Map<String, PermissionResult>
    ) : Result() {

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

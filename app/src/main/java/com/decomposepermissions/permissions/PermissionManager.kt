package com.decomposepermissions.permissions

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.decomposepermissions.permissions.PermissionManager.PermissionResult.Denied
import com.decomposepermissions.permissions.PermissionManager.PermissionResult.Granted
import com.decomposepermissions.utils.ActivityProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * A manager that allows you to request permissions and process their result.
 * When multiple permissions are requested, forms a queue and processes them sequentially.
 *
 * Must be tied to current activity.
 */
class PermissionManager(
    private val activityProvider: ActivityProvider,
    private val applicationContext: Context
) {

    private val permissionExecutor = PermissionRequestExecutor()
    private val multiplePermissionsExecutor = MultiplePermissionsRequestExecutor()
    private val operationQueue = OperationQueue()

    init {
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate).launch {
            activityProvider.activityStateFlow.collect {
                if (it != null) {
                    permissionExecutor.attachActivity(it)
                    multiplePermissionsExecutor.attachActivity(it)
                }
            }
        }
    }

    suspend fun requestPermission(permission: String): Result {
        val activity = activityProvider.awaitActivity()
        val scope = activity.lifecycleScope
        if (isPermissionGranted(permission)) {
            return Granted
        }
        return operationQueue.processOperation(scope) {
            (permissionExecutor.process(permission))
        }
    }

    suspend fun requestPermissions(permissions: List<String>): Result {
        val scope = activityProvider.awaitActivity().lifecycleScope
        return operationQueue.processOperation(scope) {
            (multiplePermissionsExecutor.process(permissions))
        }
    }

    fun isPermissionGranted(permission: String) = ContextCompat.checkSelfPermission(
        applicationContext,
        permission
    ) == PackageManager.PERMISSION_GRANTED

    fun shouldShowRationale(permission: String) =
        activityProvider.activity?.shouldShowRequestPermissionRationale(permission) ?: false

    private class PermissionRequestExecutor {

        private var activityResultLauncher: ActivityResultLauncher<String>? = null

        private val permissionsResultFlow: MutableSharedFlow<Boolean> = MutableSharedFlow()

        private lateinit var scope: LifecycleCoroutineScope

        private var activity: ComponentActivity? = null

        fun attachActivity(activity: ComponentActivity) {
            scope = activity.lifecycleScope
            activityResultLauncher = activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                scope.launch {
                    permissionsResultFlow.emit(it)
                }

            }
            this.activity = activity
        }

        suspend fun process(permission: String): PermissionResult {
            activityResultLauncher?.launch(permission)
            return if (permissionsResultFlow.first()) {
                Granted
            } else {
                val rational = activity?.shouldShowRequestPermissionRationale(permission) == false
                Denied(rational)
            }
        }
    }

    private class MultiplePermissionsRequestExecutor {

        private var activityResultLauncher: ActivityResultLauncher<Array<String>>? = null

        private val permissionsResultFlow: MutableSharedFlow<Map<String, Boolean>> = MutableSharedFlow()

        private lateinit var scope: LifecycleCoroutineScope

        private var activity: ComponentActivity? = null

        fun attachActivity(activity: ComponentActivity) {
            scope = activity.lifecycleScope
            activityResultLauncher =
                activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                    scope.launch {
                        permissionsResultFlow.emit(it)
                    }

                }
            this.activity = activity
        }

        suspend fun process(permissions: List<String>): MultiplePermissionResult {
            val permissionResults = mutableMapOf<String, PermissionResult>()

            activityResultLauncher?.launch(permissions.toTypedArray())

            permissionsResultFlow.first().forEach {
                val result = if (it.value) {
                    Granted
                } else {
                    val rational = activity?.shouldShowRequestPermissionRationale(it.key) == false
                    Denied(rational)
                }
                permissionResults[it.key] = result
            }
            return MultiplePermissionResult(
                value = permissionResults
            )
        }
    }

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

        object Granted : PermissionResult()

        class Denied(val isPermanently: Boolean) : PermissionResult()
    }

    class MultiplePermissionResult(
        val value: Map<String, PermissionResult>
    ) : Result() {

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
    }
}

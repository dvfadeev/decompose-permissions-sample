package com.decomposepermissions.permissions

import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.decomposepermissions.permissions.PermissionManager.Result.Denied
import com.decomposepermissions.permissions.PermissionManager.Result.Granted
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
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
class PermissionManager {

    private val executor = PermissionRequestExecutor()

    private val operationQueue = OperationQueue()

    private var activity: ComponentActivity? = null

    private var activityResultLauncher: ActivityResultLauncher<String>? = null

    private val permissionsResultFlow: MutableSharedFlow<Boolean> = MutableSharedFlow()

    private val scope: LifecycleCoroutineScope?
        get() = activity?.lifecycleScope

    /**
     * [PermissionManager] tied to [ComponentActivity].
     *
     * This method must be called during activity creation.
     */
    fun attachActivity(activity: ComponentActivity) {
        executor.attachActivity(activity)
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

    suspend fun requestPermission(permission: String): Result = coroutineScope {
        val isGranted = activity?.let {
            ContextCompat.checkSelfPermission(
                it,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        } ?: false

        if (isGranted) {
            return@coroutineScope Granted
        }

        return@coroutineScope operationQueue.processOperation(scope!!) {
            (executor.process(permission))
        }
    }

    class PermissionRequestExecutor {

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

        suspend fun process(permission: String): Result {
            activityResultLauncher?.launch(permission)
            return if (permissionsResultFlow.first()) {
                Granted
            } else {
                val rational = activity?.shouldShowRequestPermissionRationale(permission) == false
                Denied(rational)
            }
        }
    }

    class OperationQueue {

        private var queue: MutableStateFlow<List<suspend () -> Result>> = MutableStateFlow(listOf())

        private val queueValue
            get() = queue.value

        suspend fun processOperation(scope: CoroutineScope, operation: suspend () -> Result): Result {
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

    sealed class Result {

        object Granted : Result()

        class Denied(val isPermanently: Boolean) : Result()
    }
}

package com.decomposepermissions.utils

import androidx.activity.ComponentActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent

class ActivityProvider : KoinComponent {

    private val activityStateFlow = MutableStateFlow<ComponentActivity?>(null)

    val activity: ComponentActivity? get() = activityStateFlow.value

    fun attachActivity(activity: ComponentActivity) {
        activityStateFlow.value = activity
    }

    fun detachActivity() {
        activityStateFlow.value = null
    }

    suspend fun awaitActivity(): ComponentActivity {
        return activityStateFlow.filterNotNull().first()
    }
}

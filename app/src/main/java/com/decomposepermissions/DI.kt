package com.decomposepermissions

import com.decomposepermissions.permissions.PermissionManager
import com.decomposepermissions.root.rootModule
import com.decomposepermissions.utils.ActivityProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module

val allModules = listOf(
    module {
        single { CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate) }
        single { ActivityProvider() }
        single { PermissionManager() }
    },
    rootModule
)

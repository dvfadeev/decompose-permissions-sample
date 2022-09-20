package com.decomposepermissions

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.essenty.lifecycle.asEssentyLifecycle
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.decomposepermissions.permissions.PermissionManager
import com.decomposepermissions.root.createRootComponent
import com.decomposepermissions.root.ui.RootComponent
import com.decomposepermissions.root.ui.RootUi

class MainActivity : ComponentActivity() {

    private lateinit var rootComponent: RootComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activityProvider = application.koin.get<ActivityProvider>()
        val permissionManager = application.koin.get<PermissionManager>()
        activityProvider.attachActivity(this)
        permissionManager.attachActivity(this)
        lifecycle.asEssentyLifecycle().doOnDestroy {
            activityProvider.detachActivity()
            permissionManager.detachActivity()
        }

        val componentFactory = application.koin.get<ComponentFactory>()

        rootComponent = componentFactory.createRootComponent(defaultComponentContext())

        setContent {
            RootUi(rootComponent)
        }
    }
}

package com.decomposepermissions

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.essenty.lifecycle.asEssentyLifecycle
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.decomposepermissions.root.createRootComponent
import com.decomposepermissions.root.ui.RootComponent
import com.decomposepermissions.root.ui.RootUi
import com.decomposepermissions.utils.ActivityProvider
import com.decomposepermissions.utils.ComponentFactory
import com.decomposepermissions.utils.koin

class MainActivity : ComponentActivity() {

    private lateinit var rootComponent: RootComponent

    @OptIn(ExperimentalDecomposeApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activityProvider = application.koin.get<ActivityProvider>()
        activityProvider.attachActivity(this)
        lifecycle.asEssentyLifecycle().doOnDestroy {
            activityProvider.detachActivity()
        }

        val componentFactory = application.koin.get<ComponentFactory>()

        rootComponent = componentFactory.createRootComponent(defaultComponentContext())

        setContent {
            RootUi(rootComponent)
        }
    }
}

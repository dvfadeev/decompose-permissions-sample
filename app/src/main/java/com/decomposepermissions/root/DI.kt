package com.decomposepermissions.root

import com.arkivanov.decompose.ComponentContext
import com.decomposepermissions.utils.ComponentFactory
import com.decomposepermissions.root.ui.RealRootComponent
import com.decomposepermissions.root.ui.RootComponent
import org.koin.core.component.get
import org.koin.dsl.module

val rootModule = module {
}

fun ComponentFactory.createRootComponent(componentContext: ComponentContext): RootComponent {
    return RealRootComponent(componentContext, get())
}

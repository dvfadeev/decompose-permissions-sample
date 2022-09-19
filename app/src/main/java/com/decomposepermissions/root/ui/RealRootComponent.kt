package com.decomposepermissions.root.ui

import com.arkivanov.decompose.ComponentContext
import com.decomposepermissions.ComponentFactory

class RealRootComponent(
    componentContext: ComponentContext,
    private val componentFactory: ComponentFactory
) : ComponentContext by componentContext, RootComponent {

}

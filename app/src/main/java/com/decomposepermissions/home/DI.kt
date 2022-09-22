package com.decomposepermissions.home

import com.arkivanov.decompose.ComponentContext
import com.decomposepermissions.utils.ComponentFactory
import com.decomposepermissions.home.ui.HomeComponent
import com.decomposepermissions.home.ui.RealHomeComponent
import org.koin.core.component.get

fun ComponentFactory.createHomeComponent(componentContext: ComponentContext): HomeComponent {
    return RealHomeComponent(componentContext, get(), get())
}

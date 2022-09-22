package com.decomposepermissions.home

import com.arkivanov.decompose.ComponentContext
import com.decomposepermissions.home.ui.HomeComponent
import com.decomposepermissions.home.ui.RealHomeComponent
import com.decomposepermissions.utils.ComponentFactory
import org.koin.core.component.get

fun ComponentFactory.createHomeComponent(
    componentContext: ComponentContext,
    onOutput: (HomeComponent.Output) -> Unit
): HomeComponent {
    return RealHomeComponent(componentContext, get(), get(), onOutput)
}

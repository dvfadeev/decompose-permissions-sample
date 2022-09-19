package com.decomposepermissions.home

import com.arkivanov.decompose.ComponentContext
import com.decomposepermissions.home.ui.HomeComponent
import com.decomposepermissions.home.ui.RealHomeComponent

fun createHomeComponent(componentContext: ComponentContext): HomeComponent {
    return RealHomeComponent(componentContext)
}

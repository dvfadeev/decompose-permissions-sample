package com.decomposepermissions.multipane

import com.arkivanov.decompose.ComponentContext
import com.decomposepermissions.multipane.ui.MultiPaneComponent
import com.decomposepermissions.multipane.ui.RealMultiPaneComponent
import com.decomposepermissions.multipane.ui.firstpane.FirstPaneComponent
import com.decomposepermissions.multipane.ui.firstpane.RealFirstPaneComponent
import com.decomposepermissions.multipane.ui.secondpane.RealSecondPaneComponent
import com.decomposepermissions.multipane.ui.secondpane.SecondPaneComponent
import com.decomposepermissions.utils.ComponentFactory
import org.koin.core.component.get

fun ComponentFactory.createMultiPaneComponent(componentContext: ComponentContext): MultiPaneComponent {
    return RealMultiPaneComponent(componentContext, get())
}

fun ComponentFactory.createFirstPaneComponent(componentContext: ComponentContext): FirstPaneComponent {
    return RealFirstPaneComponent(componentContext, get())
}

fun ComponentFactory.createSecondPaneComponent(componentContext: ComponentContext): SecondPaneComponent {
    return RealSecondPaneComponent(componentContext, get())
}

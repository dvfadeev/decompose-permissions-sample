package com.decomposepermissions.root.ui

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.decomposepermissions.home.ui.HomeComponent
import com.decomposepermissions.multipane.ui.MultiPaneComponent

interface RootComponent {

    val childStack: Value<ChildStack<*, Child>>

    sealed interface Child {
        class Home(val component: HomeComponent) : Child
        class MultiPane(val component: MultiPaneComponent) : Child
    }
}

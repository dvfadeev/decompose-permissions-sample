package com.decomposepermissions.multipane.ui

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.decomposepermissions.multipane.ui.firstpane.FirstPaneComponent
import com.decomposepermissions.multipane.ui.secondpane.SecondPaneComponent

interface MultiPaneComponent {

    val firstPaneChildStack: Value<ChildStack<*, FirstPaneChild>>
    val secondPaneChildStack: Value<ChildStack<*, SecondPaneChild>>

    sealed interface FirstPaneChild {
        class FirstPane(val component: FirstPaneComponent) : FirstPaneChild
    }

    sealed interface SecondPaneChild {
        class SecondPane(val component: SecondPaneComponent) : SecondPaneChild
    }
}

package com.decomposepermissions.multipane.ui

import android.os.Parcelable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.decomposepermissions.multipane.createFirstPaneComponent
import com.decomposepermissions.multipane.createSecondPaneComponent
import com.decomposepermissions.multipane.ui.MultiPaneComponent.FirstPaneChild
import com.decomposepermissions.multipane.ui.MultiPaneComponent.SecondPaneChild
import com.decomposepermissions.utils.ComponentFactory
import kotlinx.parcelize.Parcelize

class RealMultiPaneComponent(
    componentContext: ComponentContext,
    private val componentFactory: ComponentFactory
) : ComponentContext by componentContext, MultiPaneComponent {

    private val firstPaneNavigation = StackNavigation<FirstPaneConfig>()
    private val secondPaneNavigation = StackNavigation<SecondPaneConfig>()

    private val firstPaneStack =
        childStack(
            source = firstPaneNavigation,
            initialConfiguration = FirstPaneConfig.FirstPane,
            key = "FirstPane",
            childFactory = ::createFirstPaneChild
        )

    private val secondPaneStack =
        childStack(
            source = secondPaneNavigation,
            initialConfiguration = SecondPaneConfig.SecondPane,
            key = "SecondPane",
            childFactory = ::createSecondPaneChild
        )

    override val firstPaneChildStack: Value<ChildStack<*, FirstPaneChild>> get() = firstPaneStack
    override val secondPaneChildStack: Value<ChildStack<*, SecondPaneChild>> get() = secondPaneStack

    private fun createFirstPaneChild(config: FirstPaneConfig, componentContext: ComponentContext): FirstPaneChild =
        when (config) {
            is FirstPaneConfig.FirstPane -> FirstPaneChild.FirstPane(
                componentFactory.createFirstPaneComponent(
                    componentContext
                )
            )
        }

    private fun createSecondPaneChild(config: SecondPaneConfig, componentContext: ComponentContext): SecondPaneChild =
        when (config) {
            is SecondPaneConfig.SecondPane -> SecondPaneChild.SecondPane(
                componentFactory.createSecondPaneComponent(
                    componentContext
                )
            )
        }

    private sealed class FirstPaneConfig : Parcelable {
        @Parcelize
        object FirstPane : FirstPaneConfig()
    }

    private sealed class SecondPaneConfig : Parcelable {
        @Parcelize
        object SecondPane : SecondPaneConfig()
    }
}

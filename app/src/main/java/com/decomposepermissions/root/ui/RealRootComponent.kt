package com.decomposepermissions.root.ui

import android.os.Parcelable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.decomposepermissions.home.createHomeComponent
import com.decomposepermissions.home.ui.HomeComponent
import com.decomposepermissions.multipane.createMultiPaneComponent
import com.decomposepermissions.utils.ComponentFactory
import kotlinx.parcelize.Parcelize

class RealRootComponent(
    componentContext: ComponentContext,
    private val componentFactory: ComponentFactory
) : ComponentContext by componentContext, RootComponent {

    private val navigation = StackNavigation<Config>()

    private val stack =
        childStack(
            source = navigation,
            initialConfiguration = Config.Home,
            handleBackButton = true,
            childFactory = ::createChild
        )

    override val childStack: Value<ChildStack<*, RootComponent.Child>> get() = stack

    private fun createChild(config: Config, componentContext: ComponentContext): RootComponent.Child =
        when (config) {
            is Config.Home -> RootComponent.Child.Home(
                componentFactory.createHomeComponent(
                    componentContext,
                    ::onHomeOutput
                )
            )
            is Config.MultiPane -> RootComponent.Child.MultiPane(
                componentFactory.createMultiPaneComponent(
                    componentContext
                )
            )
        }

    private fun onHomeOutput(output: HomeComponent.Output) {
        when (output) {
            is HomeComponent.Output.MultiPaneRequested -> {
                navigation.push(Config.MultiPane)
            }
        }
    }

    private sealed class Config : Parcelable {

        @Parcelize
        object Home : Config()

        @Parcelize
        object MultiPane : Config()
    }
}

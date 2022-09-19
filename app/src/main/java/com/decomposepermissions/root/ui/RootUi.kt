package com.decomposepermissions.root.ui

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.decomposepermissions.home.ui.FakeHomeComponent
import com.decomposepermissions.home.ui.HomeUi
import com.decomposepermissions.root.ui.RootComponent.Child
import com.decomposepermissions.theme.AppTheme

@ExperimentalDecomposeApi
@Composable
fun RootUi(
    component: RootComponent,
    modifier: Modifier = Modifier
) {
    AppTheme {
        Surface(modifier = modifier) {
            Children(stack = component.childStack) { child ->
                when (val instance = child.instance) {
                    is Child.Home -> HomeUi(component = instance.component)
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun RootUiPreview() {
    AppTheme {
        RootUi(FakeRootComponent())
    }
}

class FakeRootComponent : RootComponent {
    override val childStack: Value<ChildStack<*, Child>>
        get() = MutableValue(
            ChildStack(
                configuration = "fake",
                instance = Child.Home(FakeHomeComponent())
            )
        )
}

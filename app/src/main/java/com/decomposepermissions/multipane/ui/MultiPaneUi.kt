@file:Suppress("FunctionNaming")

package com.decomposepermissions.multipane.ui

import android.app.Activity
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.decomposepermissions.R.string
import com.decomposepermissions.multipane.ui.MultiPaneComponent.FirstPaneChild
import com.decomposepermissions.multipane.ui.MultiPaneComponent.SecondPaneChild
import com.decomposepermissions.multipane.ui.firstpane.FakeFirstPaneComponent
import com.decomposepermissions.multipane.ui.firstpane.FirstPaneUi
import com.decomposepermissions.multipane.ui.secondpane.FakeSecondPaneComponent
import com.decomposepermissions.multipane.ui.secondpane.SecondPaneUi
import com.decomposepermissions.theme.AppTheme

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun MultiPaneUi(
    component: MultiPaneComponent,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(id = string.multi_pane_title)
                    )

                },
                navigationIcon = {
                    IconButton(onClick = {
                        (context as? Activity)?.onBackPressed()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        content = {
            Row(modifier = Modifier.fillMaxSize()) {
                Children(
                    stack = component.firstPaneChildStack,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    when (val child = it.instance) {
                        is FirstPaneChild.FirstPane -> FirstPaneUi(component = child.component)
                    }
                }
                Divider(
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(2.dp)
                )

                Children(
                    stack = component.secondPaneChildStack,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    when (val child = it.instance) {
                        is SecondPaneChild.SecondPane -> SecondPaneUi(component = child.component)
                    }
                }
            }
        }
    )
}

@Preview(showSystemUi = true)
@Composable
fun MultiPaneUiPreview() {
    AppTheme {
        MultiPaneUi(FakeMultiPaneComponent())
    }
}

class FakeMultiPaneComponent : MultiPaneComponent {
    override val firstPaneChildStack: Value<ChildStack<*, FirstPaneChild>>
        get() = MutableValue(
            ChildStack(
                configuration = "fake",
                instance = FirstPaneChild.FirstPane(FakeFirstPaneComponent())
            )
        )

    override val secondPaneChildStack: Value<ChildStack<*, SecondPaneChild>>
        get() = MutableValue(
            ChildStack(
                configuration = "fake",
                instance = SecondPaneChild.SecondPane(FakeSecondPaneComponent())
            )
        )
}

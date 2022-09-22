@file:Suppress("FunctionNaming")

package com.decomposepermissions.home.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.decomposepermissions.R
import com.decomposepermissions.theme.AppTheme
import com.decomposepermissions.utils.LogData

@Composable
fun HomeUi(
    component: HomeComponent,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = {
                Text(
                    stringResource(id = R.string.home_title)
                )
            })
        },
        content = {
            Content(
                state = component.logsState,
                onRequestPermission = component::onRequestPermissionClick,
                onRequestMultiplePermission = component::onRequestMultiplePermission,
                onRequestPermissionFromChild = component::onRequestPermissionFromChild,
                onClearAppData = component::onClearAppData
            )
        }
    )
}

@Suppress("MagicNumber")
@Composable
private fun Content(
    state: MutableState<List<LogData>>,
    onRequestPermission: () -> Unit,
    onRequestMultiplePermission: () -> Unit,
    onRequestPermissionFromChild: () -> Unit,
    onClearAppData: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MenuButton(onClick = onRequestPermission, textResource = R.string.request_permission_btn)
            MenuButton(onClick = onRequestMultiplePermission, textResource = R.string.request_multiple_permission_btn)
            MenuButton(
                onClick = onRequestPermissionFromChild,
                textResource = R.string.request_permission_from_child_btn
            )
            MenuButton(
                onClick = onClearAppData,
                textResource = R.string.clear_app_data,
                modifier = Modifier.padding(top = 96.dp)
            )
        }

        val scrollState = rememberLazyListState()
        LaunchedEffect(state.value.size) {
            scrollState.animateScrollToItem(state.value.size)
        }
        LazyColumn(
            state = scrollState,
            userScrollEnabled = false,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .heightIn(0.dp, 150.dp)
                .graphicsLayer { alpha = 0.99f }
                .drawWithContent {
                    val colors = listOf(Color.Transparent, Color.Black)
                    drawContent()
                    drawRect(
                        brush = Brush.verticalGradient(colors),
                        blendMode = BlendMode.DstIn
                    )
                }
        ) {
            items(state.value) {
                LogContent(data = it)
            }
        }
    }
}

@Composable
private fun MenuButton(
    onClick: () -> Unit,
    textResource: Int,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(
            text = stringResource(id = textResource)
        )
    }
}

@Composable
private fun LogContent(
    data: LogData,
    modifier: Modifier = Modifier
) {
    Text(
        text = data.log,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
    )
}

@Preview(showSystemUi = true)
@Composable
fun HomeUiPreview() {
    AppTheme {
        HomeUi(FakeHomeComponent())
    }
}

class FakeHomeComponent : HomeComponent {

    @Suppress("MagicNumber")
    override val logsState: MutableState<List<LogData>> = mutableStateOf(
        MutableList(5) {
            LogData("Sample")
        }
    )

    override fun onRequestPermissionClick() = Unit

    override fun onRequestMultiplePermission() = Unit

    override fun onRequestPermissionFromChild() = Unit

    override fun onClearAppData() = Unit
}

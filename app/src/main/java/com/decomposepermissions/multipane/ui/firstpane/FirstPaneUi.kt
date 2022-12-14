@file:Suppress("FunctionNaming")

package com.decomposepermissions.multipane.ui.firstpane

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.decomposepermissions.theme.AppTheme
import com.decomposepermissions.theme.appTypography
import com.decomposepermissions.utils.LogData

@Composable
fun FirstPaneUi(
    component: FirstPaneComponent,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        state = rememberLazyListState(),
        modifier = modifier
    ) {
        items(component.logsState.value) {
            LogContent(data = it)
        }
    }
}

@Composable
private fun LogContent(
    data: LogData,
    modifier: Modifier = Modifier
) {
    Text(
        text = data.log,
        style = MaterialTheme.appTypography.caption,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    )
}

@Preview(showSystemUi = true)
@Composable
fun FirstPaneUiPreview() {
    AppTheme {
        FirstPaneUi(FakeFirstPaneComponent())
    }
}

class FakeFirstPaneComponent : FirstPaneComponent {
    override val logsState: MutableState<List<LogData>> = mutableStateOf(
        listOf(LogData("Sample"))
    )
}

package com.decomposepermissions.home.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.decomposepermissions.R
import com.decomposepermissions.theme.AppTheme
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest

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
                showToastEvent = component.showToastEvent,
                onRequestPermission = component::onRequestPermissionClick,
                onRequestMultiplePermission = component::onRequestMultiplePermission
            )
        }
    )
}

@Composable
private fun Content(
    showToastEvent: MutableSharedFlow<String>,
    onRequestPermission: () -> Unit,
    onRequestMultiplePermission: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        showToastEvent.collectLatest {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = onRequestPermission) {
            Text(
                text = stringResource(id = R.string.request_permission_btn)
            )
        }
        Button(onClick = onRequestMultiplePermission) {
            Text(
                text = stringResource(id = R.string.request_multiple_permission_btn)
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun HomeUiPreview() {
    AppTheme {
        HomeUi(FakeHomeComponent())
    }
}

class FakeHomeComponent : HomeComponent {

    override val showToastEvent: MutableSharedFlow<String> = MutableSharedFlow()

    override fun onRequestPermissionClick() = Unit

    override fun onRequestMultiplePermission() = Unit
}

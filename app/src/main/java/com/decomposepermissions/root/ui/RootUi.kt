package com.decomposepermissions.root.ui

import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.decomposepermissions.theme.AppTheme

@Composable
fun RootUi(
    component: RootComponent,
    modifier: Modifier = Modifier
) {
    AppTheme {
        Surface {
            Text("Test")
        }
    }
}

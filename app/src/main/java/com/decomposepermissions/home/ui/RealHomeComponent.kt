package com.decomposepermissions.home.ui

import android.Manifest
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.decomposepermissions.permissions.PermissionManager
import com.decomposepermissions.utils.componentCoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class RealHomeComponent(
    componentContext: ComponentContext,
    private val permissionManager: PermissionManager
) : ComponentContext by componentContext, HomeComponent {

    private val q: MutableValue<String> = MutableValue("sda")

    override val showToastEvent: MutableSharedFlow<String> = MutableSharedFlow()

    override fun onRequestPermissionClick() {
        permissionManager.requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .onGranted {
                showToast("Granted")
            }.onDenied {
                showToast("Denied")
            }.onAutoDenied {
                showToast("AutoDenied")
            }
    }

    override fun onRequestMultiplePermission() {
        TODO("Not yet implemented")
    }

    private fun showToast(text: String) {
        componentCoroutineScope().launch {
            showToastEvent.emit(text)
        }
    }
}

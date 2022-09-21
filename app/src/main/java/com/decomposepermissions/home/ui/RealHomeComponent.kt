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
        permissionManager.requestPermission(Manifest.permission.CAMERA)
            .onGranted {
                showToast("Camera: granted")
            }.onDenied {
                showToast("Camera: denied")
            }.onAutoDenied {
                showToast("Camera: auto denied")
            }
        permissionManager.requestPermission(Manifest.permission.READ_CONTACTS)
            .onGranted {
                showToast("Contacts: granted")
            }.onDenied {
                showToast("Contacts: denied")
            }.onAutoDenied {
                showToast("Contacts: auto denied")
            }
    }

    private fun showToast(text: String) {
        componentCoroutineScope().launch {
            showToastEvent.emit(text)
        }
    }
}

package com.decomposepermissions.home.ui

import android.Manifest
import com.arkivanov.decompose.ComponentContext
import com.decomposepermissions.permissions.PermissionManager
import timber.log.Timber

class RealHomeComponent(
    componentContext: ComponentContext,
    private val permissionManager: PermissionManager
) : ComponentContext by componentContext, HomeComponent {

    override fun onRequestPermissionClick() {
        permissionManager.requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .onGranted {
                Timber.d("Granted")
            }.onDenied {
                Timber.d("Denied")
            }.onAutoDenied {
                Timber.d("AutoDenied")
            }
    }
}

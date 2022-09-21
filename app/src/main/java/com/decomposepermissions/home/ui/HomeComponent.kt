package com.decomposepermissions.home.ui

import kotlinx.coroutines.flow.MutableSharedFlow

interface HomeComponent {

    val showToastEvent: MutableSharedFlow<String>

    fun onRequestPermissionClick()

    fun onRequestMultiplePermission()
}

package com.decomposepermissions.home.ui

import android.Manifest
import android.app.ActivityManager
import android.content.Context.ACTIVITY_SERVICE
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import com.decomposepermissions.permissions.PermissionManager
import com.decomposepermissions.utils.ActivityProvider
import kotlinx.datetime.Clock.System
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val PERMISSION_READ_STORAGE = "ReadStorage"
private const val PERMISSION_CAMERA = "Camera"
private const val PERMISSION_READ_CONTACTS = "ReadContacts"

private const val STATUS_GRANTED = "Granted"
private const val STATUS_DENIED = "Denied"
private const val STATUS_AUTO_DENIED = "Denied Automatically"

class RealHomeComponent(
    componentContext: ComponentContext,
    private val activityProvider: ActivityProvider,
    private val permissionManager: PermissionManager
) : ComponentContext by componentContext, HomeComponent {

    override val logsState: MutableState<List<LogData>> = mutableStateOf(listOf())

    override fun onRequestPermissionClick() {
        requestPermission(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            PERMISSION_READ_STORAGE
        )
    }

    override fun onRequestMultiplePermission() {
        requestPermission(
            Manifest.permission.CAMERA,
            PERMISSION_CAMERA
        )
        requestPermission(
            Manifest.permission.READ_CONTACTS,
            PERMISSION_READ_CONTACTS
        )
    }

    override fun onClearAppData() {
        activityProvider.activity?.let {
            (it.getSystemService(ACTIVITY_SERVICE) as ActivityManager).clearApplicationUserData()
        }
    }

    private fun requestPermission(permission: String, logTitle: String) {
        permissionManager.requestPermission(permission)
            .onGranted {
                showLog(logTitle, STATUS_GRANTED)
            }.onDenied {
                showLog(logTitle, STATUS_DENIED)
            }.onAutoDenied {
                showLog(logTitle, STATUS_AUTO_DENIED)
            }
    }

    private fun showLog(title: String, log: String) {
        logsState.value = logsState.value.toMutableList().apply {
            add(
                LogData("${getTimeString()}| $title $log")
            )
        }
    }

    private val timePattern = "HH:mm:ss"

    private fun getTimeString(): String {
        val formatter = SimpleDateFormat(timePattern, Locale.getDefault())
        return formatter.format(Date(System.now().toEpochMilliseconds()))
    }
}

package com.decomposepermissions.utils

import com.decomposepermissions.permissions.PermissionManager

private const val STATUS_GRANTED = "Granted"
private const val STATUS_DENIED = "Denied"
private const val STATUS_PERMANENTLY_DENIED = "Denied Permanently"

const val PERMISSION_READ_STORAGE = "ReadStorage"
const val PERMISSION_CAMERA = "Camera"
const val PERMISSION_READ_CONTACTS = "ReadContacts"
const val PERMISSION_READ_SMS = "ReadSms"
const val PERMISSION_ACCESS_LOCATION = "AccessLocation"

fun PermissionManager.Result.toMessage(): String = when (this) {
    PermissionManager.Result.Granted -> STATUS_GRANTED
    is PermissionManager.Result.Denied -> if (isPermanently) {
        STATUS_PERMANENTLY_DENIED
    } else {
        STATUS_DENIED
    }
}

package com.decomposepermissions.utils

import com.decomposepermissions.permissions.PermissionManager

private const val STATUS_GRANTED = "GRANTED"
private const val STATUS_DENIED = "DENIED"
private const val STATUS_PERMANENTLY_DENIED = "DENIED PERMANENTLY"

fun PermissionManager.Result.toMessage(): String = when (this) {
    PermissionManager.PermissionResult.Granted -> STATUS_GRANTED
    is PermissionManager.PermissionResult.Denied -> if (isPermanently) {
        STATUS_PERMANENTLY_DENIED
    } else {
        STATUS_DENIED
    }
    is PermissionManager.MultiplePermissionResult -> value.entries.joinToString {
        "${it.key} ${it.value.toMessage()}"
    }
}

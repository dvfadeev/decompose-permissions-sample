package com.decomposepermissions.utils

import com.decomposepermissions.permissions.MultiplePermissionResult
import com.decomposepermissions.permissions.SinglePermissionResult

private const val STATUS_GRANTED = "GRANTED"
private const val STATUS_DENIED = "DENIED"
private const val STATUS_PERMANENTLY_DENIED = "DENIED PERMANENTLY"

fun SinglePermissionResult.toMessage(): String = when (this) {
    SinglePermissionResult.Granted -> STATUS_GRANTED
    is SinglePermissionResult.Denied -> if (permanently) {
        STATUS_PERMANENTLY_DENIED
    } else {
        STATUS_DENIED
    }
}

fun MultiplePermissionResult.toMessage(): String = value.entries.joinToString {
    "${it.key} ${it.value.toMessage()}"
}

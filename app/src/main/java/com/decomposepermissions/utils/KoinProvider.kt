package com.decomposepermissions.utils

import android.app.Application
import org.koin.core.Koin

interface KoinProvider {
    val koin: Koin
}

val Application.koin get() = (this as KoinProvider).koin

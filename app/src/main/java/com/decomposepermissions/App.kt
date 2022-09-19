package com.decomposepermissions

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.Koin
import org.koin.dsl.koinApplication

class App : Application(), KoinProvider {

    override lateinit var koin: Koin
        private set

    override fun onCreate() {
        super.onCreate()
        koin = createKoin()
        koin.declare(ComponentFactory(koin))
    }

    private fun createKoin(): Koin {
        return koinApplication {
            androidContext(this@App)
            modules(allModules)
        }.koin
    }
}

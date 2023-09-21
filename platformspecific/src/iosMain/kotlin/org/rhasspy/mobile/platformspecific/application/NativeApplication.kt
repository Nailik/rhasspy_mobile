package org.rhasspy.mobile.platformspecific.application

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.dsl.module

actual abstract class NativeApplication : AppApplication() {

    init {
        onInit()
    }

    actual companion object {
        lateinit var koinApplicationInstance: NativeApplication
        actual val koinApplicationModule = module {
            single { koinApplicationInstance }
        }
    }

    actual fun onInit() {
        koinApplicationInstance = this
        onCreated()
    }

    actual val currentlyAppInBackground: MutableStateFlow<Boolean>
        get() = MutableStateFlow(false) //TODO #511
    actual val isAppInBackground: StateFlow<Boolean>
        get() = MutableStateFlow(false) //TODO#511
    actual abstract val isHasStarted: StateFlow<Boolean>
    actual fun isInstrumentedTest(): Boolean {
        //TODO #511
        return true
    }

    actual fun restart() {
        //TODO #511
    }

    actual abstract suspend fun resume()
    actual fun closeApp() {
        //TODO #511
    }

    actual abstract fun onCreated()

}
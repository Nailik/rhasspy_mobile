package org.rhasspy.mobile.platformspecific.application

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.dsl.module

actual abstract class NativeApplication {

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
        get() = MutableStateFlow(false) //TODO("Not yet implemented")
    actual val isAppInBackground: StateFlow<Boolean>
        get() = MutableStateFlow(false) //TODO("Not yet implemented")
    actual abstract val isHasStarted: StateFlow<Boolean>
    actual fun isInstrumentedTest(): Boolean {
        //TODO("Not yet implemented")
        return true
    }

    actual fun restart() {
        //TODO("Not yet implemented")
    }

    actual abstract suspend fun resume()
    actual fun closeApp() {
        //TODO("Not yet implemented")
    }

    actual abstract fun onCreated()


}
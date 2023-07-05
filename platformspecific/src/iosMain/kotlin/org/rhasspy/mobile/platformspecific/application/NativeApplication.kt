package org.rhasspy.mobile.platformspecific.application

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.dsl.module

actual abstract class NativeApplication : INativeApplication {

    @ThreadLocal
    actual companion object {
        lateinit var koinApplicationInstance: NativeApplication
        actual val koinApplicationModule = module {
            single<INativeApplication> { koinApplicationInstance }
        }
    }

    actual override fun onInit() {
        koinApplicationInstance = this
    }

    actual override val currentlyAppInBackground: MutableStateFlow<Boolean>
        get() = MutableStateFlow(false) //TODO("Not yet implemented")
    actual override val isAppInBackground: StateFlow<Boolean>
        get() = MutableStateFlow(false) //TODO("Not yet implemented")
    actual abstract override val isHasStarted: StateFlow<Boolean>
    actual override fun isInstrumentedTest(): Boolean {
        //TODO("Not yet implemented")
        return true
    }

    actual override fun restart() {
        //TODO("Not yet implemented")
    }

    actual override fun onCreate() {
        //TODO("Not yet implemented")
    }

    actual abstract override fun resume()
    actual abstract override fun startRecordingAction()
    actual override fun closeApp() {
        //TODO("Not yet implemented")
    }
}
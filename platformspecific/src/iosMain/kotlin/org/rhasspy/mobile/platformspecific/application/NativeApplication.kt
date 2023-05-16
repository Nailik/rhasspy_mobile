package org.rhasspy.mobile.platformspecific.application

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

actual abstract class NativeApplication {
    actual val currentlyAppInBackground: MutableStateFlow<Boolean>
        get() = MutableStateFlow(false) //TODO("Not yet implemented")
    actual val isAppInBackground: StateFlow<Boolean>
        get() = MutableStateFlow(false) //TODO("Not yet implemented")
    actual abstract val isHasStarted: StateFlow<Boolean>
    actual abstract fun setCrashlyticsCollectionEnabled(enabled: Boolean)
    actual fun isInstrumentedTest(): Boolean {
        //TODO("Not yet implemented")
        return true
    }

    actual fun restart() {
        //TODO("Not yet implemented")
    }

    actual fun onCreate() {
        //TODO("Not yet implemented")
    }

    actual abstract fun resume()
    actual abstract fun startRecordingAction()
    actual fun closeApp() {
        //TODO("Not yet implemented")
    }
}
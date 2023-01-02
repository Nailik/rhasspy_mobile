package org.rhasspy.mobile

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

actual abstract class NativeApplication {
    actual fun startNativeServices() {
    }

    actual fun restart() {
    }

    actual val currentlyAppInBackground: MutableStateFlow<Boolean>
        get() = TODO("Not yet implemented")
    actual val isAppInBackground: StateFlow<Boolean>
        get() = TODO("Not yet implemented")

    actual abstract suspend fun updateWidgetNative()


}
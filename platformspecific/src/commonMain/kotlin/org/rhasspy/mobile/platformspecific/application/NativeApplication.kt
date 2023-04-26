package org.rhasspy.mobile.platformspecific.application

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

expect abstract class NativeApplication() {

    val currentlyAppInBackground: MutableStateFlow<Boolean>
    val isAppInBackground: StateFlow<Boolean>
    abstract val isHasStarted: StateFlow<Boolean>

    abstract suspend fun updateWidgetNative()

    abstract fun resume()

    abstract fun setCrashlyticsCollectionEnabled(enabled: Boolean)

    abstract fun startRecordingAction()

    fun isInstrumentedTest(): Boolean

    fun restart()

    fun onCreate()

}
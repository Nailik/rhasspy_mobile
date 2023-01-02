package org.rhasspy.mobile.nativeutils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

expect abstract class NativeApplication() {

    fun restart()

    val currentlyAppInBackground: MutableStateFlow<Boolean>
    val isAppInBackground: StateFlow<Boolean>

    abstract suspend fun updateWidgetNative()

}
package org.rhasspy.mobile

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect abstract class NativeApplication() {

    fun startNativeServices()

    fun restart()

    val currentlyAppInBackground: MutableStateFlow<Boolean>
    val isAppInBackground: StateFlow<Boolean>

    abstract suspend fun updateWidgetNative()

    //TODO functions to get file for sounds, wakeword, keystore ... (subfolders)

}
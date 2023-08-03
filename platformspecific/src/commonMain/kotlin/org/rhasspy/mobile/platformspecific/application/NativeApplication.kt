package org.rhasspy.mobile.platformspecific.application

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.module.Module

expect abstract class NativeApplication() {

    companion object {
        val koinApplicationModule: Module
    }

    val currentlyAppInBackground: MutableStateFlow<Boolean>
    val isAppInBackground: StateFlow<Boolean>
    abstract val isHasStarted: StateFlow<Boolean>

    abstract fun resume()

    abstract fun onCreated()

    fun onInit()

    fun isInstrumentedTest(): Boolean

    fun closeApp()

    fun restart()

}
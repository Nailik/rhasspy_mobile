package org.rhasspy.mobile.platformspecific.application

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.module.Module

interface INativeApplication {

    val currentlyAppInBackground: MutableStateFlow<Boolean>
    val isAppInBackground: StateFlow<Boolean>
    val isHasStarted: StateFlow<Boolean>

    fun resume()

    fun startRecordingAction()

    fun onInit()

    fun isInstrumentedTest(): Boolean

    fun closeApp()

    fun restart()

    fun onCreate()

}

expect abstract class NativeApplication() : INativeApplication {

    companion object {
        val koinApplicationModule: Module
    }

    override val currentlyAppInBackground: MutableStateFlow<Boolean>
    override val isAppInBackground: StateFlow<Boolean>
    abstract override val isHasStarted: StateFlow<Boolean>

    abstract override fun resume()

    abstract override fun startRecordingAction()

    override fun onInit()

    override fun isInstrumentedTest(): Boolean

    override fun closeApp()

    override fun restart()

    override fun onCreate()

}
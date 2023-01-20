package org.rhasspy.mobile.viewmodel.configuration.test

import io.ktor.utils.io.core.Closeable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.rhasspy.mobile.middleware.ServiceState

abstract class IConfigurationTest : Closeable, KoinComponent {

    //test scope gets destroyed in stop test and recreated in start test
    protected var testScope = CoroutineScope(Dispatchers.Default)
        private set
    abstract val serviceState: StateFlow<ServiceState>

    fun initializeTest() {
        testScope = CoroutineScope(Dispatchers.Default)
    }

    override fun close() {
        testScope.cancel()
    }

}
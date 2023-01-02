package org.rhasspy.mobile.viewmodel.configuration.test

import io.ktor.utils.io.core.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.middleware.ServiceState
import org.rhasspy.mobile.middleware.IServiceMiddleware
import org.rhasspy.mobile.readOnly

abstract class IConfigurationTest : Closeable, KoinComponent {

    //test scope gets destroyed in stop test and recreated in start test
    protected var testScope = CoroutineScope(Dispatchers.Default)
        private set
    abstract val serviceState: StateFlow<ServiceState>

    fun initializeTest() {
        testScope = CoroutineScope(Dispatchers.Default)
    }

    override fun close() {
        onClose()
        testScope.cancel()
    }

    protected open fun onClose() {

    }

}
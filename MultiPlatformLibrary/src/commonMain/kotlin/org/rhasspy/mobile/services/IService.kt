package org.rhasspy.mobile.services

import io.ktor.utils.io.core.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.rhasspy.mobile.middleware.ServiceState
import org.rhasspy.mobile.readOnly

abstract class IService : KoinComponent, Closeable {

    protected val _currentState = MutableStateFlow<ServiceState>(ServiceState.Pending)
    val currentState = _currentState.readOnly

    override fun close() {
        onClose()
    }

    abstract fun onClose()

}
package org.rhasspy.mobile.services

import io.ktor.utils.io.core.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.rhasspy.mobile.middleware.ServiceState
import org.rhasspy.mobile.readOnly

abstract class IService : KoinComponent, Closeable {

    private val _serviceState = MutableStateFlow<ServiceState>(ServiceState.Pending)
    val serviceState = _serviceState.readOnly

    override fun close() {
        onClose()
    }

    open fun onClose() {}

}
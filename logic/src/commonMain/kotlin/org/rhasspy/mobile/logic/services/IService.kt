package org.rhasspy.mobile.logic.services

import io.ktor.utils.io.core.Closeable
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.rhasspy.mobile.data.service.ServiceState

abstract class IService : KoinComponent, Closeable {

    abstract val serviceState: StateFlow<ServiceState>

    override fun close() {
        onClose()
    }

    open fun onClose() {}

}
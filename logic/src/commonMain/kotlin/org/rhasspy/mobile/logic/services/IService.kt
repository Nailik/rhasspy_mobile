package org.rhasspy.mobile.logic.services

import io.ktor.utils.io.core.Closeable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.logic.logger.LogType

abstract class IService(
    val logType: LogType
) : KoinComponent, Closeable {
    protected val logger = logType.logger()

    open val serviceState: StateFlow<ServiceState> = MutableStateFlow<ServiceState>(ServiceState.Success)

    override fun close() {
        onClose()
    }

    open fun onClose() {}

}
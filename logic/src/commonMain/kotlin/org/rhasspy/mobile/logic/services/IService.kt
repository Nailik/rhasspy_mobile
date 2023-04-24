package org.rhasspy.mobile.logic.services

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.log.LogType
import org.rhasspy.mobile.logic.middleware.ServiceMiddleware
import org.rhasspy.mobile.platformspecific.application.NativeApplication

abstract class IService(
    val logType: LogType
) : KoinComponent {

    protected val nativeApplication by inject<NativeApplication>()
    protected val serviceMiddleware by inject<ServiceMiddleware>()

    protected val logger = logType.logger()

    open val serviceState: StateFlow<ServiceState> = MutableStateFlow<ServiceState>(ServiceState.Success)

}
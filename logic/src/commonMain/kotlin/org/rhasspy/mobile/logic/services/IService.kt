package org.rhasspy.mobile.logic.services

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.rhasspy.mobile.data.service.ServiceState

interface IService : KoinComponent {

    val logger: Logger

    val serviceState: StateFlow<ServiceState>

}
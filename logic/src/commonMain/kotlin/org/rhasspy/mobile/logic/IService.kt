package org.rhasspy.mobile.logic

import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.rhasspy.mobile.data.service.ServiceState

interface IService : KoinComponent {

    val serviceState: StateFlow<ServiceState>

}
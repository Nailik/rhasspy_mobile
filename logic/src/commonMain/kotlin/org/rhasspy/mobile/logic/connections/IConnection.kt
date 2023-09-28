package org.rhasspy.mobile.logic.connections

import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.rhasspy.mobile.data.service.ConnectionState

interface IConnection : KoinComponent {

    val connectionState: StateFlow<ConnectionState>

}
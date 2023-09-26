package org.rhasspy.mobile.logic

import org.koin.core.component.KoinComponent
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.data.service.ServiceState.ErrorState

interface IService : KoinComponent {

    val hasError: ErrorState?
    fun dispose()

}
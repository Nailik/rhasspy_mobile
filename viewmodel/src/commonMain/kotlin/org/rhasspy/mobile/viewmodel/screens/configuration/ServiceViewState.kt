package org.rhasspy.mobile.viewmodel.screens.configuration

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.logic.services.IService

@Stable
data class ServiceViewState internal constructor(
    val serviceState: StateFlow<ServiceState>
) {
    companion object {
        fun getInitialViewState(service: IService): ServiceViewState {
            return ServiceViewState(
                serviceState = service.serviceState
            )
        }
    }
}
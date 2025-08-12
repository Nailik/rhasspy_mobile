package org.rhasspy.mobile.viewmodel.screens.configuration

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.service.ServiceState

@Stable
data class ServiceViewState(
    val serviceState: StateFlow<ServiceState>,
)
package org.rhasspy.mobile.viewmodel.configuration

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.viewmodel.screens.configuration.ServiceViewState

@Stable
data class ServiceStateHeaderViewState(
    val serviceState: ServiceViewState,
    val isOpenServiceDialogEnabled: Boolean,
    val serviceStateDialogText: Any
)
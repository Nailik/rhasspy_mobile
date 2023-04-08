package org.rhasspy.mobile.viewmodel.configuration.event

import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.logic.logger.LogElement

data class IConfigurationViewState(
    val isBackPressDisabled: Boolean,
    val isLoading: Boolean,
    val editViewState: StateFlow<IConfigurationEditViewState>,
    val testViewState: StateFlow<IConfigurationTestViewState>,
    val serviceViewState: StateFlow<IConfigurationServiceViewState>
) {

    data class IConfigurationEditViewState(
        val hasUnsavedChanges: Boolean,
        val isTestingEnabled: Boolean
    )

    data class IConfigurationTestViewState(
        val isListFiltered: Boolean,
        val isListAutoscroll: Boolean,
        val logEvents: StateFlow<ImmutableList<LogElement>>
    )

    data class IConfigurationServiceViewState(
        val serviceState: ServiceState,
        val isOpenServiceDialogEnabled: Boolean,
        val serviceStateDialogText: Any
    )
}
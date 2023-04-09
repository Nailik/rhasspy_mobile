package org.rhasspy.mobile.viewmodel.configuration.event

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.logic.logger.LogElement
import org.rhasspy.mobile.viewmodel.screens.configuration.ServiceViewState

@Stable
data class IConfigurationViewState<V>(
    val contentViewState: V,
    val isBackPressDisabled: Boolean,
    val isLoading: Boolean,
    val editViewState: StateFlow<IConfigurationEditViewState>,
    val testViewState: StateFlow<IConfigurationTestViewState>
) {

    @Stable
    data class IConfigurationEditViewState(
        val hasUnsavedChanges: Boolean,
        val isTestingEnabled: Boolean,
        val serviceViewState: StateFlow<ServiceStateHeaderViewState>
    )

    @Stable
    data class IConfigurationTestViewState(
        val isListFiltered: Boolean,
        val isListAutoscroll: Boolean,
        val logEvents: StateFlow<ImmutableList<LogElement>>,
        val serviceViewState: StateFlow<ServiceStateHeaderViewState>
    )

    @Stable
    data class ServiceStateHeaderViewState(
        val serviceState: ServiceViewState,
        val isOpenServiceDialogEnabled: Boolean,
        val serviceStateDialogText: Any
    )
}
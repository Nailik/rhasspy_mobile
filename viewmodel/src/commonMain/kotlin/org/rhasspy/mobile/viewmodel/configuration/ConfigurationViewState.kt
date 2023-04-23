package org.rhasspy.mobile.viewmodel.configuration

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationUiNavigate.PopBackStack

@Stable
data class ConfigurationViewState<V : IConfigurationEditViewState> internal constructor(
    val isBackPressDisabled: Boolean,
    val serviceViewState: StateFlow<ServiceStateHeaderViewState>,
    val testViewState: StateFlow<ConfigurationTestViewState>,
    val editViewState: StateFlow<V>,
    val showUnsavedChangesDialog: Boolean = false,
    val popBackStack: PopBackStack,
    val hasUnsavedChanges: Boolean
)
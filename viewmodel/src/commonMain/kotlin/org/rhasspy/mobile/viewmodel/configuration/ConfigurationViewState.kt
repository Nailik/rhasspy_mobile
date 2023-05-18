package org.rhasspy.mobile.viewmodel.configuration

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow

@Stable
data class ConfigurationViewState<V : IConfigurationEditViewState> internal constructor(
    val isBackPressDisabled: Boolean,
    val serviceViewState: StateFlow<ServiceStateHeaderViewState>,
    val testViewState: StateFlow<ConfigurationTestViewState>,
    val editViewState: StateFlow<V>,
    val isShowUnsavedChangesDialog: Boolean = false,
    val hasUnsavedChanges: Boolean
)
package org.rhasspy.mobile.viewmodel.configuration

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow

@Stable
data class ConfigurationViewState<V : IConfigurationEditViewState>(
    val isBackPressDisabled: Boolean,
    val isLoading: Boolean,
    val serviceViewState: StateFlow<ServiceStateHeaderViewState>,
    val testViewState: StateFlow<ConfigurationTestViewState>,
    val editViewState: StateFlow<V>
)
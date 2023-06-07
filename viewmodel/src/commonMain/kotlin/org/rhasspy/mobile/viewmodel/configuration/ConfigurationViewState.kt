package org.rhasspy.mobile.viewmodel.configuration

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.viewmodel.configuration.edit.IConfigurationEditViewState
import org.rhasspy.mobile.viewmodel.configuration.test.IConfigurationTestViewState
import org.rhasspy.mobile.viewmodel.screens.configuration.ServiceViewState

@Stable
data class ConfigurationViewState<V : IConfigurationEditViewState> internal constructor(
    val serviceViewState: ServiceViewState,
    val isOpenServiceStateDialogEnabled: Boolean,
    val isShowServiceStateDialog: Boolean = false,
    val serviceStateDialogText: Any,
    val testViewState: StateFlow<IConfigurationTestViewState>,
    val editViewState: StateFlow<V>,
    val isShowUnsavedChangesDialog: Boolean = false,
    val hasUnsavedChanges: Boolean
)
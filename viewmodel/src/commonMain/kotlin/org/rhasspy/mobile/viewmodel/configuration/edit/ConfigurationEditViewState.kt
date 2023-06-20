package org.rhasspy.mobile.viewmodel.configuration.edit

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.viewmodel.screens.configuration.ServiceViewState

@Stable
data class ConfigurationEditViewState(
    val serviceViewState: ServiceViewState,
    val hasUnsavedChanges: Boolean = false,
    val isOpenServiceStateDialogEnabled: Boolean = false,
    val dialogState: ConfigurationDialogState? = null
) {

    sealed interface ConfigurationDialogState {

        object UnsavedChangesDialogState: ConfigurationDialogState
        data class ServiceStateDialogState(val dialogText: Any): ConfigurationDialogState

    }

}
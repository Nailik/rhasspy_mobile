package org.rhasspy.mobile.viewmodel.configuration.edit

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.viewmodel.ScreenViewState
import org.rhasspy.mobile.viewmodel.screens.configuration.ServiceViewState

@Stable
data class ConfigurationEditViewState(
    val serviceViewState: ServiceViewState,
    val screenViewState: ScreenViewState = ScreenViewState(),
    val isTestingEnabled: Boolean = false,
    val hasUnsavedChanges: Boolean = false,
    val isOpenServiceStateDialogEnabled: Boolean = false,
    val dialogState: DialogState? = null
) {

    sealed interface DialogState {

        object UnsavedChangesDialogState: DialogState
        data class ServiceStateDialogState(val dialogText: Any): DialogState

    }

}
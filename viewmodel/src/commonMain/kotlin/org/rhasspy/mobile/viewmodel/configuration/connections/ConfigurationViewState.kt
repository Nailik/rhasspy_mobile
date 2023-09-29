package org.rhasspy.mobile.viewmodel.configuration.connections

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.viewstate.TextWrapper
import org.rhasspy.mobile.viewmodel.screens.configuration.ServiceViewState

@Stable
data class ConfigurationViewState(
    val serviceViewState: ServiceViewState?,
    val hasUnsavedChanges: Boolean = false,
    val isOpenServiceStateDialogEnabled: Boolean = false,
    val dialogState: DialogState? = null
) {

    @Stable
    sealed interface DialogState {

        data object UnsavedChangesDialogState : DialogState
        data class ServiceStateDialogState(val dialogText: TextWrapper) : DialogState

    }

}
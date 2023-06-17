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
    val dialog: Dialogs? = null
) {

    sealed interface Dialogs {

        object UnsavedChangesDialog: Dialogs
        data class ServiceStateDialog(val dialogText: Any): Dialogs

    }


}
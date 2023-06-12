package org.rhasspy.mobile.viewmodel.configuration.edit

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.viewmodel.screens.configuration.ServiceViewState

@Stable
data class ConfigurationEditViewState(
    val serviceViewState: ServiceViewState,
    val isShowServiceStateDialog: Boolean = false,
    val isShowUnsavedChangesDialog: Boolean = false,
    val isTestingEnabled: Boolean = false,
    val hasUnsavedChanges: Boolean = false,
    val isOpenServiceStateDialogEnabled: Boolean = false
)
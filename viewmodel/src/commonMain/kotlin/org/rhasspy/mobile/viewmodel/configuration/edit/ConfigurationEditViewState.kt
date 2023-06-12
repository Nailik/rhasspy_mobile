package org.rhasspy.mobile.viewmodel.configuration.edit

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.viewmodel.screens.configuration.ServiceViewState

@Stable
data class ConfigurationEditViewState<T> internal constructor(
    val serviceViewState: ServiceViewState,
    val isShowServiceStateDialog: Boolean,
    val isShowUnsavedChangesDialog: Boolean,
    val dataState: StateFlow<ConfigurationDataState<T>>
) {

    data class ConfigurationDataState<T>(
        val isOpenServiceStateDialogEnabled: Boolean,
        val serviceStateDialogText: Any,
        val hasUnsavedChanges: Boolean,
        val isTestingEnabled: Boolean,
        val configurationEditData: T
    )


}
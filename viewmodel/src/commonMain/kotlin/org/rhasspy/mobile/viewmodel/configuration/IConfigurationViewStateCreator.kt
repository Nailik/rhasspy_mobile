package org.rhasspy.mobile.viewmodel.configuration

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.platformspecific.combineState

class IConfigurationViewStateCreator(
    private val serviceState: StateFlow<ServiceState>
) {

    operator fun <T> invoke(
        init: () -> T,
        viewState: StateFlow<IConfigurationViewState>,
        configurationViewState: MutableStateFlow<ConfigurationViewState>
    ): StateFlow<ConfigurationViewState> {

        return combineState(
            viewState,
            configurationViewState,
            serviceState
        ) { viewStateValue, configurationViewStateValue, serviceState ->

            val isHasUnsavedChanges = viewStateValue.editData != init()

            configurationViewState.update { it.copy(hasUnsavedChanges = isHasUnsavedChanges) }

            configurationViewStateValue.copy(
                hasUnsavedChanges = isHasUnsavedChanges,
                isOpenServiceStateDialogEnabled = serviceState is ServiceState.ErrorState,
            )

        }
    }

}
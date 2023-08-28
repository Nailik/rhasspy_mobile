package org.rhasspy.mobile.viewmodel.configuration

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.logic.IService
import org.rhasspy.mobile.platformspecific.combineState

class IConfigurationViewStateCreator(
    private val service: IService,
) {

    operator fun <T> invoke(
        init: () -> T,
        viewState: StateFlow<IConfigurationViewState>,
        configurationViewState: MutableStateFlow<ConfigurationViewState>
    ): StateFlow<ConfigurationViewState> {

        return combineState(
            viewState,
            configurationViewState,
            service.serviceState
        ) { viewStateValue, configurationViewStateValue, serviceState ->

            val isHasUnsavedChanges = viewStateValue.editData != init()

            configurationViewState.update { it.copy(hasUnsavedChanges = isHasUnsavedChanges) }

            configurationViewStateValue.copy(
                hasUnsavedChanges = isHasUnsavedChanges,
                isOpenServiceStateDialogEnabled = serviceState.isOpenServiceStateDialogEnabled(),
            )

        }
    }

}
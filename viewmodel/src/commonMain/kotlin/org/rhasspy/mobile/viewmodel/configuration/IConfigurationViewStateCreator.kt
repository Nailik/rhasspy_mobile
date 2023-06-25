package org.rhasspy.mobile.viewmodel.configuration

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.platformspecific.combineState

class IConfigurationViewStateCreator(
    private val service: IService,
) {

    operator fun <T> invoke(
        init: () -> T,
        editData: StateFlow<T>,
        configurationViewState: MutableStateFlow<IConfigurationViewState>
    ): StateFlow<IConfigurationViewState> {

        return combineState(
            editData,
            configurationViewState,
            service.serviceState
        ) { data, viewState, serviceState ->

            val isHasUnsavedChanges = data != init()

            configurationViewState.update { it.copy(hasUnsavedChanges = isHasUnsavedChanges) }

            viewState.copy(
                hasUnsavedChanges = isHasUnsavedChanges,
                isOpenServiceStateDialogEnabled = serviceState.isOpenServiceStateDialogEnabled(),
            )

        }
    }

}
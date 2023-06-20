package org.rhasspy.mobile.viewmodel.configuration.edit

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.platformspecific.combineState
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.viewmodel.screens.configuration.ServiceViewState

class ConfigurationEditViewStateCreator(
    private val service: IService,
) {

    operator fun <T> invoke(
        init: () -> T,
        editData: StateFlow<T>,
        configurationEditViewState: MutableStateFlow<ConfigurationEditViewState>
    ): StateFlow<ConfigurationEditViewState> {

        return combineState(
            editData,
            configurationEditViewState,
            service.serviceState
        ) { data, viewState, serviceState ->

            val isHasUnsavedChanges = data != init()

             viewState.copy(
                hasUnsavedChanges = isHasUnsavedChanges,
                isOpenServiceStateDialogEnabled = serviceState.isOpenServiceStateDialogEnabled(),
            )

        }
    }

}
package org.rhasspy.mobile.viewmodel.configuration.edit

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.viewmodel.configuration.edit.webserver.WebServerConfigurationViewState
import org.rhasspy.mobile.viewmodel.screens.configuration.ServiceViewState

class ConfigurationEditViewStateCreator(
    private val service: IService,
) {

    operator fun <T> invoke(
        init: () -> T,
        isTestingEnabled: (T) -> Boolean,
        editData: StateFlow<T>,
        configurationEditViewState: MutableStateFlow<ConfigurationEditViewState>
    ): StateFlow<ConfigurationEditViewState> {

        return combineStateFlow(
            editData,
            configurationEditViewState,
            service.serviceState
        ).mapReadonlyState {

            val isHasUnsavedChanges = editData.value != init()

            configurationEditViewState.value.copy(
                serviceViewState = ServiceViewState(service.serviceState),
                isTestingEnabled = isTestingEnabled(editData.value) && !isHasUnsavedChanges,
                hasUnsavedChanges = isHasUnsavedChanges,
                isOpenServiceStateDialogEnabled = service.serviceState.value.isOpenServiceStateDialogEnabled(),
            )

        }
    }

}
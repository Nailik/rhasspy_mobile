package org.rhasspy.mobile.viewmodel.configuration.edit.webserver

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.viewmodel.ScreenViewState
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditViewState
import org.rhasspy.mobile.viewmodel.configuration.edit.webserver.WebServerConfigurationViewState.WebServerConfigurationData
import org.rhasspy.mobile.viewmodel.screens.configuration.ServiceViewState

class WebServerConfigurationEditViewStateCreator(
    private val service: IService,
) {

    fun combine(
        editData: StateFlow<WebServerConfigurationData>,
        configurationEditViewState: StateFlow<ConfigurationEditViewState>
    ): StateFlow<WebServerConfigurationViewState> {

        return combineStateFlow(
            editData,
            configurationEditViewState,
            service.serviceState
        ).mapReadonlyState {

            val isHasUnsavedChanges = editData.value != WebServerConfigurationData()

            WebServerConfigurationViewState(
                editData = editData.value,
                editViewState = configurationEditViewState.value.copy(
                    hasUnsavedChanges = isHasUnsavedChanges,
                    isTestingEnabled = editData.value.isHttpServerEnabled && !isHasUnsavedChanges,
                    isOpenServiceStateDialogEnabled = service.serviceState.value.isOpenServiceStateDialogEnabled(),
                    serviceViewState = ServiceViewState(service.serviceState)
                )
            )

        }
    }

}
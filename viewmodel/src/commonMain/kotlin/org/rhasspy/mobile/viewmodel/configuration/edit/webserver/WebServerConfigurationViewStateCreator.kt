package org.rhasspy.mobile.viewmodel.configuration.edit.webserver

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.rhasspy.mobile.logic.services.webserver.WebServerService
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditViewState
import org.rhasspy.mobile.viewmodel.configuration.edit.webserver.WebServerConfigurationViewState.WebServerConfigurationData
import org.rhasspy.mobile.viewmodel.screens.configuration.ServiceViewState

class WebServerConfigurationViewStateCreator(
    private val webServerService: WebServerService,
    private val editData: StateFlow<WebServerConfigurationData>,
    private val editViewState: StateFlow<ConfigurationEditViewState>
) {

    private val updaterScope = CoroutineScope(Dispatchers.IO)
    private val viewState = MutableStateFlow(getViewState())

    operator fun invoke(): MutableStateFlow<WebServerConfigurationViewState> {

        updaterScope.launch {
            combineStateFlow(
                editViewState,
                editData,
                webServerService.serviceState
            ).collect {
                viewState.value = getViewState()
            }
        }

        return viewState
    }

    private fun getViewState(): WebServerConfigurationViewState {
        return WebServerConfigurationViewState(
            editData = editData.value,
            editViewState = editViewState.value.copy(
                hasUnsavedChanges = editData.value != WebServerConfigurationData(),
                isTestingEnabled = editData.value.isHttpServerEnabled,
                isOpenServiceStateDialogEnabled = webServerService.serviceState.value.isOpenServiceStateDialogEnabled()
            )
        )
    }

}
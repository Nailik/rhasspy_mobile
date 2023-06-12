package org.rhasspy.mobile.viewmodel.configuration.edit

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.viewmodel.configuration.edit.ConfigurationEditViewState.ConfigurationDataState

class ConfigurationDataStateCreator<T> internal constructor(
    private val service: IService,
    private val configurationDataState: StateFlow<T>,
    private val configurationEditDataState: StateFlow<T>
) {

    private val updaterScope = CoroutineScope(Dispatchers.IO)
    private  val viewState = MutableStateFlow(getViewState())

    operator fun invoke(): MutableStateFlow<ConfigurationDataState<T>> {

        updaterScope.launch {
            combineStateFlow(
                configurationDataState,
                configurationEditDataState,
                service.serviceState
            ).collect {
                viewState.value = getViewState()
            }
        }

        return viewState
    }

    private fun getViewState() : ConfigurationDataState<T> {
        val hasUnsavedChanged = configurationEditDataState.value != configurationDataState.value
        return ConfigurationDataState(
            isOpenServiceStateDialogEnabled = service.serviceState.value.isOpenServiceStateDialogEnabled(),
            serviceStateDialogText = service.serviceState.value.getDialogText(),
            hasUnsavedChanges = hasUnsavedChanged,
            isTestingEnabled = !hasUnsavedChanged,
            configurationEditData = configurationEditDataState.value
        )
    }

    private fun ServiceState.isOpenServiceStateDialogEnabled(): Boolean = (this is ServiceState.Exception || this is ServiceState.Error)
    private fun ServiceState.getDialogText(): Any = when (this) {
        is ServiceState.Error -> this.information
        is ServiceState.Exception -> this.exception?.toString() ?: ""
        else -> ""
    }

}
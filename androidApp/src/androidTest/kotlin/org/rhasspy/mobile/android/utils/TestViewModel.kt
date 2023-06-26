package org.rhasspy.mobile.android.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.data.log.LogType
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState
import org.rhasspy.mobile.viewmodel.screens.configuration.ServiceViewState

class TestService : IService(LogType.AudioPlayingService)

class TestViewModel : IConfigurationViewModel(
    service = TestService()
) {

    private val _stateFlow = MutableStateFlow(
        IConfigurationViewState(serviceViewState = ServiceViewState(TestService().serviceState))
    )

    var onSave = false
    var onDiscard = false

    override fun onDiscard() {
        onDiscard = true
    }

    override fun onSave() {
        onSave = true
    }

    fun setUnsavedChanges(value: Boolean) {
        _stateFlow.update { it.copy(hasUnsavedChanges = value) }
    }

    fun onRequestOverlayPermission() {
        requireOverlayPermission { }
    }

    override fun initViewStateCreator(configurationViewState: MutableStateFlow<IConfigurationViewState>): StateFlow<IConfigurationViewState> {
        return _stateFlow
    }

}
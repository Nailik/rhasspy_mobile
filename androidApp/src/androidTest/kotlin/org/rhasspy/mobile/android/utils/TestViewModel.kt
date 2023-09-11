package org.rhasspy.mobile.android.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.logic.IService
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState
import org.rhasspy.mobile.viewmodel.screens.configuration.ServiceViewState

class TestService : IService {
    override val serviceState: StateFlow<ServiceState>
        get() = MutableStateFlow(ServiceState.Disabled)
}

data class TestConfigurationViewState(
    override val editData: TestConfigurationData = TestConfigurationData(null)

) : IConfigurationViewState {

    data class TestConfigurationData(val data: Any?) : IConfigurationViewState.IConfigurationData

}

class TestViewModel : ConfigurationViewModel(
    serviceState = TestService().serviceState
) {

    private val _stateFlow = MutableStateFlow(
        ConfigurationViewState(serviceViewState = ServiceViewState(serviceState = TestService().serviceState))
    )

    var onSave = false
    var onDiscard = false

    override fun onDiscard() {
        onDiscard = true
    }

    override fun onSave() {
        onSave = true
    }

    fun onRequestOverlayPermission() {
        requireOverlayPermission { }
    }

    override fun initViewStateCreator(configurationViewState: MutableStateFlow<ConfigurationViewState>): StateFlow<ConfigurationViewState> {
        return _stateFlow
    }

}
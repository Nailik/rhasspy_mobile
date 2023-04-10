package org.rhasspy.mobile.viewmodel.configuration.mqtt

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationContentViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState.IConfigurationEditViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState.ServiceStateHeaderViewState

@Stable
data class MqttConfigurationViewState(): IConfigurationContentViewState {

    companion object {
        fun getInitial() = MqttConfigurationViewState()
    }

    override fun getEditViewState(serviceViewState: StateFlow<ServiceStateHeaderViewState>): IConfigurationEditViewState {
        return IConfigurationEditViewState()
    }

    fun isTestingEnabled(): Boolean = true

    override fun save() { }

}
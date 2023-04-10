package org.rhasspy.mobile.viewmodel.configuration.speechtotext

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationContentViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState.IConfigurationEditViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState.ServiceStateHeaderViewState

@Stable
data class SpeechToTextConfigurationViewState(): IConfigurationContentViewState {

    companion object {
        fun getInitial() = SpeechToTextConfigurationViewState()
    }

    override fun getEditViewState(serviceViewState: StateFlow<ServiceStateHeaderViewState>): IConfigurationEditViewState {
        return IConfigurationEditViewState()
    }

    fun isTestingEnabled(): Boolean = true

    override fun save() { }

}
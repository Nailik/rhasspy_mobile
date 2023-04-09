package org.rhasspy.mobile.viewmodel.configuration

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.viewmodel.configuration.event.IConfigurationViewState.IConfigurationEditViewState
import org.rhasspy.mobile.viewmodel.configuration.event.IConfigurationViewState.ServiceStateHeaderViewState

interface IConfigurationContentViewState {

    fun getEditViewState(serviceViewState: StateFlow<ServiceStateHeaderViewState>) : IConfigurationEditViewState

}
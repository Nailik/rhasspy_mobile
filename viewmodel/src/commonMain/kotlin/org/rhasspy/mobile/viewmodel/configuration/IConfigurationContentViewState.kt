package org.rhasspy.mobile.viewmodel.configuration

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState.IConfigurationEditViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState.ServiceStateHeaderViewState

interface IConfigurationContentViewState {

    fun getEditViewState(serviceViewState: StateFlow<ServiceStateHeaderViewState>) : IConfigurationEditViewState

    fun save()

}
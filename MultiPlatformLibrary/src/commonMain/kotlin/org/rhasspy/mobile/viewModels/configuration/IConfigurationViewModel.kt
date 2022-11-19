package org.rhasspy.mobile.viewModels.configuration

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.services.state.ServiceState

interface IConfigurationViewModel {

    val hasUnsavedChanges: StateFlow<Boolean>
    val isTestingEnabled: StateFlow<Boolean>
    val testState: StateFlow<List<ServiceState>>

    fun save()

    fun discard()

    fun test()

    fun stopTest()

}
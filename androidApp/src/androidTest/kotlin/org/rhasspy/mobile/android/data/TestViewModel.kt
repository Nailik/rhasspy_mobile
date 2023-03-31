package org.rhasspy.mobile.android.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.logic.logger.LogType
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.test.IConfigurationTest

class TestViewModel : IConfigurationViewModel() {

    var onSave = false
    var onDiscard = false

    override val testRunner: IConfigurationTest
        get() = object : IConfigurationTest() {
            override val serviceState: StateFlow<ServiceState>
                get() = MutableStateFlow(ServiceState.Success)
        }
    override val logType: LogType
        get() = LogType.HttpClientService
    override val serviceState: StateFlow<ServiceState>
        get() = MutableStateFlow(ServiceState.Success)

    val isHasUnsavedChanges = MutableStateFlow(false)
    override val hasUnsavedChanges: StateFlow<Boolean>
        get() = isHasUnsavedChanges
    override val isTestingEnabled: StateFlow<Boolean>
        get() = MutableStateFlow(true)

    override fun discard() {
        onDiscard = true
    }

    override fun onSave() {
        onSave = true
    }

    override fun initializeTestParams() {

    }
}
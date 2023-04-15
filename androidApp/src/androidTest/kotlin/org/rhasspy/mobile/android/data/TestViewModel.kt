package org.rhasspy.mobile.android.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.logic.logger.LogType
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationEditViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationTest
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewModel

class TestViewModelTest : IConfigurationTest() {
    override val serviceState: StateFlow<ServiceState>
        get() = MutableStateFlow(ServiceState.Success)

}

class TestService : IService(LogType.AudioPlayingService)

class TestViewState(private val isHasUnsavedChanges: Boolean = false) : IConfigurationEditViewState() {

    override val hasUnsavedChanges: Boolean
        get() = isHasUnsavedChanges
    override val isTestingEnabled: Boolean
        get() = false

}

class TestViewModel : IConfigurationViewModel<TestViewModelTest, TestViewState>(
    service = TestService(),
    testRunner = TestViewModelTest(),
    initialViewState = { TestViewState() }
) {

    var onSave = false
    var onDiscard = false

    override fun onDiscard() {
        onDiscard = true
    }

    override fun onSave() {
        onSave = true
    }

    override fun initializeTestParams() {

    }

    fun setContentViewState(testViewState: TestViewState) {
        contentViewState.value = testViewState
    }
}
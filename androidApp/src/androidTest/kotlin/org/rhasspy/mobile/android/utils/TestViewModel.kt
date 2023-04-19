package org.rhasspy.mobile.android.utils

import org.rhasspy.mobile.logic.logger.LogType
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationEditViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewModel

class TestService : IService(LogType.AudioPlayingService)

class TestViewState(private val isHasUnsavedChanges: Boolean = false) : IConfigurationEditViewState() {

    override val hasUnsavedChanges: Boolean
        get() = isHasUnsavedChanges
    override val isTestingEnabled: Boolean
        get() = false

}

class TestViewModel : IConfigurationViewModel<TestViewState>(
    service = TestService(),
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
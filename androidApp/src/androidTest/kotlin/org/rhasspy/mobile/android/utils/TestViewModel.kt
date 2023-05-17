package org.rhasspy.mobile.android.utils

import org.rhasspy.mobile.data.log.LogType
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationEditViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewModel
import org.rhasspy.mobile.viewmodel.navigation.Navigator

class TestService : IService(LogType.AudioPlayingService)

data class TestViewState(
    val data: Boolean = true
) : IConfigurationEditViewState() {

    override val isTestingEnabled: Boolean
        get() = false

}

class TestViewModel(navigator: Navigator) : IConfigurationViewModel<TestViewState>(
    service = TestService(),
    initialViewState = { TestViewState() },
    navigator = navigator
) {

    var onSave = false
    var onDiscard = false

    override fun onDiscard() {
        onDiscard = true
    }

    override fun onSave() {
        onSave = true
    }

    fun setUnsavedChanges(value: Boolean) {
        if (value) {
            updateViewState { it.copy(data = !it.data) }
        } else {
            contentViewState.value = TestViewState()
        }
    }


}
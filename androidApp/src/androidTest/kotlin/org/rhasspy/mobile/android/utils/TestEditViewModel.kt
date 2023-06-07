package org.rhasspy.mobile.android.utils

import org.rhasspy.mobile.data.log.LogType
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.viewmodel.configuration.edit.IConfigurationEditViewState
import org.rhasspy.mobile.viewmodel.configuration.edit.IConfigurationEditViewModel
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination

class TestService : IService(LogType.AudioPlayingService)

data class TestViewState(
    val data: Boolean = true
) : IConfigurationEditViewState() {

    override val isTestingEnabled: Boolean
        get() = false

}

class TestEditViewModel : IConfigurationEditViewModel<TestViewState>(
    service = TestService(),
    initialViewState = { TestViewState() },
    testPageDestination = TestNavigationDestinations.Test
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

    fun onRequestOverlayPermission() {
        requireOverlayPermission(Unit) { }
    }

}

enum class TestNavigationDestinations : NavigationDestination {
    Test
}
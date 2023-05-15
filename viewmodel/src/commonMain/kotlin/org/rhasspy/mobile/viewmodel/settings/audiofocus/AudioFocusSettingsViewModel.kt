package org.rhasspy.mobile.viewmodel.settings.audiofocus

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.navigation.Navigator
import org.rhasspy.mobile.viewmodel.settings.audiofocus.AudioFocusSettingsUiEvent.Change
import org.rhasspy.mobile.viewmodel.settings.audiofocus.AudioFocusSettingsUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.settings.audiofocus.AudioFocusSettingsUiEvent.Navigate
import org.rhasspy.mobile.viewmodel.settings.audiofocus.AudioFocusSettingsUiEvent.Navigate.BackClick

class AudioFocusSettingsViewModel(
    viewStateCreator: AudioFocusSettingsViewStateCreator,
    private val navigator: Navigator
) : ViewModel() {

    val viewState: StateFlow<AudioFocusSettingsViewState> = viewStateCreator()

    fun onEvent(event: AudioFocusSettingsUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Navigate -> onNavigate(event)
        }
    }

    private fun onChange(change: Change) {
        when (change) {
            is SelectAudioFocusOption -> AppSetting.audioFocusOption.value = change.option
            is SetAudioFocusOnDialog -> AppSetting.isAudioFocusOnDialog.value = change.enabled
            is SetAudioFocusOnNotification -> AppSetting.isAudioFocusOnNotification.value = change.enabled
            is SetAudioFocusOnRecord -> AppSetting.isAudioFocusOnRecord.value = change.enabled
            is SetAudioFocusOnSound -> AppSetting.isAudioFocusOnSound.value = change.enabled
        }
    }

    private fun onNavigate(navigate: Navigate) {
        when (navigate) {
            is BackClick -> navigator.popBackStack()
        }
    }

}
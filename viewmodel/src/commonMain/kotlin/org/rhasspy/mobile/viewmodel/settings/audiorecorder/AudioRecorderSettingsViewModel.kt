package org.rhasspy.mobile.viewmodel.settings.audiorecorder

import androidx.compose.runtime.Stable
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.navigation.Navigator
import org.rhasspy.mobile.viewmodel.settings.audiorecorder.AudioRecorderSettingsUiEvent.Change
import org.rhasspy.mobile.viewmodel.settings.audiorecorder.AudioRecorderSettingsUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.settings.audiorecorder.AudioRecorderSettingsUiEvent.Navigate
import org.rhasspy.mobile.viewmodel.settings.audiorecorder.AudioRecorderSettingsUiEvent.Navigate.BackClick

@Stable
class AudioRecorderSettingsViewModel(
    viewStateCreator: AudioRecorderSettingsViewStateCreator,
    private val navigator: Navigator
) : ViewModel() {

    val viewState: StateFlow<AudioRecorderSettingsViewState> = viewStateCreator()

    fun onEvent(event: AudioRecorderSettingsUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Navigate -> onNavigate(event)
        }
    }

    private fun onChange(change: Change) {
        when (change) {
            is SelectAudioRecorderChannelType -> AppSetting.audioRecorderChannel.value = change.audioRecorderChannelType
            is SelectAudioRecorderEncodingType -> AppSetting.audioRecorderEncoding.value = change.audioRecorderEncodingType
            is SelectAudioRecorderSampleRateType -> AppSetting.audioRecorderSampleRate.value = change.audioRecorderSampleRateType
        }
    }

    private fun onNavigate(navigate: Navigate) {
        when (navigate) {
            is BackClick -> navigator.popBackStack()
        }
    }

}
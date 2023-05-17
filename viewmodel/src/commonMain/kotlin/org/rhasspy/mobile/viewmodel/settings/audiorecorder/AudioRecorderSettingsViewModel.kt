package org.rhasspy.mobile.viewmodel.settings.audiorecorder

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.KViewModel
import org.rhasspy.mobile.viewmodel.settings.audiorecorder.AudioRecorderSettingsUiEvent.Action
import org.rhasspy.mobile.viewmodel.settings.audiorecorder.AudioRecorderSettingsUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.settings.audiorecorder.AudioRecorderSettingsUiEvent.Change
import org.rhasspy.mobile.viewmodel.settings.audiorecorder.AudioRecorderSettingsUiEvent.Change.*

@Stable
class AudioRecorderSettingsViewModel(
    viewStateCreator: AudioRecorderSettingsViewStateCreator
) : KViewModel() {

    val viewState: StateFlow<AudioRecorderSettingsViewState> = viewStateCreator()

    fun onEvent(event: AudioRecorderSettingsUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
        }
    }

    private fun onChange(change: Change) {
        when (change) {
            is SelectAudioRecorderChannelType -> AppSetting.audioRecorderChannel.value = change.audioRecorderChannelType
            is SelectAudioRecorderEncodingType -> AppSetting.audioRecorderEncoding.value = change.audioRecorderEncodingType
            is SelectAudioRecorderSampleRateType -> AppSetting.audioRecorderSampleRate.value = change.audioRecorderSampleRateType
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            is BackClick -> navigator.popBackStack()
        }
    }

}
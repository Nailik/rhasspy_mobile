package org.rhasspy.mobile.viewmodel.settings.audiorecorder

import androidx.compose.runtime.Stable
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.settings.audiorecorder.AudioRecorderSettingsUiEvent.Change
import org.rhasspy.mobile.viewmodel.settings.audiorecorder.AudioRecorderSettingsUiEvent.Change.*

@Stable
class AudioRecorderSettingsViewModel(
    viewStateCreator: AudioRecorderSettingsViewStateCreator
) : ViewModel() {

    val viewState: StateFlow<AudioRecorderSettingsViewState> = viewStateCreator()

    fun onEvent(event: AudioRecorderSettingsUiEvent) {
        when (event) {
            is Change -> onChange(event)
        }
    }

    private fun onChange(change: Change) {
        when (change) {
            is SelectAudioRecorderChannelType -> AppSetting.audioRecorderChannel.value = change.audioRecorderChannelType
            is SelectAudioRecorderEncodingType -> AppSetting.audioRecorderEncoding.value = change.audioRecorderEncodingType
            is SelectAudioRecorderSampleRateType -> AppSetting.audioRecorderSampleRate.value = change.audioRecorderSampleRateType
        }
    }

}
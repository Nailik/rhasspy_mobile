package org.rhasspy.mobile.viewmodel.settings.audiorecorder

import org.rhasspy.mobile.data.audiorecorder.AudioRecorderChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioRecorderSampleRateType
import org.rhasspy.mobile.viewmodel.settings.backgroundservice.BackgroundServiceUiEvent

sealed interface AudioRecorderSettingsUiEvent {

    sealed interface Navigate : AudioRecorderSettingsUiEvent {
        object BackClick: Navigate
    }

    sealed interface Change : AudioRecorderSettingsUiEvent {

        data class SelectAudioRecorderChannelType(val audioRecorderChannelType: AudioRecorderChannelType) : Change
        data class SelectAudioRecorderEncodingType(val audioRecorderEncodingType: AudioRecorderEncodingType) : Change
        data class SelectAudioRecorderSampleRateType(val audioRecorderSampleRateType: AudioRecorderSampleRateType) : Change

    }

}
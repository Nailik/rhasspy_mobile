package org.rhasspy.mobile.viewmodel.configuration.speechtotext

import org.rhasspy.mobile.data.audiorecorder.AudioFormatChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatSampleRateType
import org.rhasspy.mobile.data.service.option.SpeechToTextOption

sealed interface SpeechToTextConfigurationUiEvent {

    sealed interface Change : SpeechToTextConfigurationUiEvent {
        data class SelectSpeechToTextOption(val option: SpeechToTextOption) : Change
        data class SetUseCustomHttpEndpoint(val enabled: Boolean) : Change
        data class SetUseSpeechToTextMqttSilenceDetection(val enabled: Boolean) : Change
        data class UpdateSpeechToTextHttpEndpoint(val endpoint: String) : Change
    }

    sealed interface Action : SpeechToTextConfigurationUiEvent {
        data object OpenAudioRecorderFormat : Action
        data object OpenAudioOutputFormat : Action
        data object BackClick : Action

    }

    sealed interface AudioRecorderFormatUiEvent : SpeechToTextConfigurationUiEvent {

        sealed interface Change : AudioRecorderFormatUiEvent {

            data class SelectAudioRecorderChannelType(val value: AudioFormatChannelType) : Change
            data class SelectAudioRecorderEncodingType(val value: AudioFormatEncodingType) : Change
            data class SelectAudioRecorderSampleRateType(val value: AudioFormatSampleRateType) : Change
        }


    }

    sealed interface AudioOutputFormatUiEvent : SpeechToTextConfigurationUiEvent {

        sealed interface Change : AudioOutputFormatUiEvent {

            data class SelectAudioOutputChannelType(val value: AudioFormatChannelType) : Change
            data class SelectAudioOutputEncodingType(val value: AudioFormatEncodingType) : Change
            data class SelectAudioOutputSampleRateType(val value: AudioFormatSampleRateType) : Change
        }


    }

}
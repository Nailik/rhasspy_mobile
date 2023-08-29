package org.rhasspy.mobile.viewmodel.configuration.voiceactivitydetection

import org.rhasspy.mobile.data.service.option.VoiceActivityDetectionOption

sealed interface VoiceActivityDetectionUiEvent {

    sealed interface Change : VoiceActivityDetectionUiEvent {

        data class SelectVoiceActivityDetectionOption(val option: VoiceActivityDetectionOption) : Change

    }

    sealed interface Action : VoiceActivityDetectionUiEvent {

        data object BackClick : Action

    }

    sealed interface LocalSilenceDetectionUiEvent : VoiceActivityDetectionUiEvent {

        sealed interface Change : LocalSilenceDetectionUiEvent {
            data class UpdateSilenceDetectionMinimumTime(val time: String) : Change
            data class UpdateSilenceDetectionTime(val time: String) : Change
            data class UpdateSilenceDetectionAudioLevelLogarithm(val percentage: Float) : Change
        }

        sealed interface Action : LocalSilenceDetectionUiEvent {

            data object ToggleAudioLevelTest : Action

        }

    }

}
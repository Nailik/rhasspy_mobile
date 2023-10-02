package org.rhasspy.mobile.viewmodel.configuration.vad

import org.rhasspy.mobile.data.service.option.VoiceActivityDetectionOption

sealed interface VadDomainUiEvent {

    sealed interface Change : VadDomainUiEvent {

        data class SelectVadDomainOption(val option: VoiceActivityDetectionOption) : Change

    }

    sealed interface Action : VadDomainUiEvent {

        data object BackClick : Action

    }

    sealed interface LocalSilenceDetectionUiEvent : VadDomainUiEvent {

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
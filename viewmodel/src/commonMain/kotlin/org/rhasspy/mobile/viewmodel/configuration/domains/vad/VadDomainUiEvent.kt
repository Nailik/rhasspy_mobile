package org.rhasspy.mobile.viewmodel.configuration.domains.vad

import org.rhasspy.mobile.data.service.option.VadDomainOption

sealed interface VadDomainUiEvent {

    sealed interface Change : VadDomainUiEvent {

        data class SelectVadDomainOption(val option: VadDomainOption) : Change

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
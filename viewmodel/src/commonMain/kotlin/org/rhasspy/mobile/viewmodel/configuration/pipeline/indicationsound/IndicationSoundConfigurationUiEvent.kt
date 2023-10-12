package org.rhasspy.mobile.viewmodel.configuration.pipeline.indicationsound

import org.rhasspy.mobile.data.sounds.IndicationSoundOption

sealed interface IndicationSoundConfigurationUiEvent {

    sealed interface Change : IndicationSoundConfigurationUiEvent {

        data class SetSoundIndicationOption(val option: IndicationSoundOption) : Change
        data class UpdateSoundVolume(val volume: Float) : Change

    }

    sealed interface Action : IndicationSoundConfigurationUiEvent {

        data object ToggleAudioPlayerActive : Action
        data object ChooseSoundFile : Action

    }

    sealed interface Consumed : IndicationSoundConfigurationUiEvent {

        data object ShowSnackBar : Consumed

    }
}
package org.rhasspy.mobile.viewmodel.settings.indication.sound

import org.rhasspy.mobile.data.sounds.SoundOption

sealed interface IIndicationSoundSettingsUiEvent {

    sealed interface Change : IIndicationSoundSettingsUiEvent {

        data class SetSoundIndicationOption(val option: SoundOption) : Change
        data class SetSoundFile(val file: String) : Change
        data class UpdateSoundVolume(val volume: Float) : Change
        data class DeleteSoundFile(val file: String) : Change
        data class AddSoundFile(val file: String) : Change

    }

    sealed interface Action : IIndicationSoundSettingsUiEvent {

        object ToggleAudioPlayerActive : Action
        object ChooseSoundFile : Action

    }

    sealed interface Consumed : IIndicationSoundSettingsUiEvent {

        object ShowSnackBar : Consumed

    }
}
package org.rhasspy.mobile.data.pipeline

import kotlinx.serialization.Serializable
import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.data.sounds.SoundOption

@Serializable
data class LocalPipelineData(
    val isSoundIndicationEnabled: Boolean,
    val soundIndicationOutputOption: AudioOutputOption,
    val wakeSound: IndicationSoundOption,
    val errorSound: IndicationSoundOption,
    val recordedSound: IndicationSoundOption
) {

    @Serializable
    data class IndicationSoundOption(
        val volume: Float,
        val option: SoundOption,
        val indicationOutputOption: AudioOutputOption,
    )


}

/*
    val isSoundIndicationEnabled = ISetting(SettingsEnum.SoundIndication, true)
    val soundIndicationOutputOption = ISetting(
        key = SettingsEnum.SoundIndicationOutput,
        initial = AudioOutputOption.Notification,
        serializer = AudioOutputOption.serializer(),
    )


 val wakeSoundVolume = ISetting(SettingsEnum.WakeSoundVolume, 0.5F)
    val recordedSoundVolume = ISetting(SettingsEnum.RecordedSoundVolume, 0.5F)
    val errorSoundVolume = ISetting(SettingsEnum.ErrorSoundVolume, 0.5F)

    val wakeSound = ISetting(
        key = SettingsEnum.WakeSound,
        initial = SoundOption.Default.name,
    )
    val recordedSound = ISetting(
        key = SettingsEnum.RecordedSound,
        initial = SoundOption.Default.name,
    )
    val errorSound = ISetting(
        key = SettingsEnum.ErrorSound,
        initial = SoundOption.Default.name,
    )

    //saves sound as pair, first is fileName as String, second is used and indicates if this custom sound file is used
    val customWakeSounds = ISetting(
        key = SettingsEnum.CustomWakeSounds,
        initial = emptyList(),
        serializer = ListSerializer(String.serializer()),
    )
    val customRecordedSounds = ISetting(
        key = SettingsEnum.CustomRecordedSounds,
        initial = emptyList(),
        serializer = ListSerializer(String.serializer()),
    )
    val customErrorSounds = ISetting(
        key = SettingsEnum.CustomErrorSounds,
        initial = emptyList(),
        serializer = ListSerializer(String.serializer()),
    )

 */
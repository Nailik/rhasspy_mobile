package org.rhasspy.mobile.viewmodel.settings.indication.sound

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.resource.StableStringResource
import org.rhasspy.mobile.data.service.option.AudioOutputOption

@Stable
data class IIndicationSoundSettingsViewState(
    val soundSetting: String,
    val customSoundFiles: ImmutableList<String>,
    val soundVolume: Float,
    val isAudioPlaying: Boolean,
    val audioOutputOption: AudioOutputOption,
    val isNoSoundInformationBoxVisible: Boolean,
    val snackBarText: StableStringResource? = null,
) {

    constructor(
        soundSetting: String,
        customSoundFiles: ImmutableList<String>,
        soundVolume: Float,
        isAudioPlaying: Boolean,
        audioOutputOption: AudioOutputOption,
        deviceSoundVolume: Int?,
        deviceNotificationVolume: Int?,
    ) : this(
        soundSetting = soundSetting,
        customSoundFiles = customSoundFiles,
        soundVolume = soundVolume,
        isAudioPlaying = isAudioPlaying,
        audioOutputOption = audioOutputOption,
        isNoSoundInformationBoxVisible = when (audioOutputOption) {
            AudioOutputOption.Sound -> deviceSoundVolume == 0
            AudioOutputOption.Notification -> deviceNotificationVolume == 0
        }
    )

}
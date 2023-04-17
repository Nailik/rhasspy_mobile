package org.rhasspy.mobile.viewmodel.settings.indication.sound

import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.logic.services.localaudio.LocalAudioService
import org.rhasspy.mobile.logic.settings.AppSetting
import org.rhasspy.mobile.platformspecific.volume.DeviceVolume

data class IIndicationSoundSettingsViewState internal constructor(
    val soundSetting: String,
    val customSoundFiles: ImmutableList<String>,
    val soundVolume: Float,
    val isAudioPlaying: Boolean,
    val audioOutputOption: AudioOutputOption,
    val isNoSoundInformationBoxVisible: Boolean
) {

    companion object {
        fun getInitialViewState(
            soundSetting: String,
            customSoundFiles: ImmutableList<String>,
            soundVolume: Float,
            localAudioService: LocalAudioService,
        ): IIndicationSoundSettingsViewState {
            return IIndicationSoundSettingsViewState(
                soundSetting = soundSetting,
                customSoundFiles = customSoundFiles,
                soundVolume = soundVolume,
                isAudioPlaying = localAudioService.isPlayingState.value,
                audioOutputOption = AppSetting.soundIndicationOutputOption.value,
                isNoSoundInformationBoxVisible = when (AppSetting.soundIndicationOutputOption.value) {
                    AudioOutputOption.Sound -> DeviceVolume.volumeFlowSound.value == 0
                    AudioOutputOption.Notification -> DeviceVolume.volumeFlowNotification.value == 0
                }
            )
        }
    }

}
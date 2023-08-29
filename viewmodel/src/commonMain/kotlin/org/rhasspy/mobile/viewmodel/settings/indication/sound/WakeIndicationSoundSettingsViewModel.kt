package org.rhasspy.mobile.viewmodel.settings.indication.sound

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.logic.local.localaudio.ILocalAudioService
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.file.FolderType.SoundFolder.Wake
import org.rhasspy.mobile.settings.AppSetting

@Stable
class WakeIndicationSoundSettingsViewModel(
    localAudioService: ILocalAudioService,
    nativeApplication: NativeApplication
) : IIndicationSoundSettingsViewModel(
    localAudioService = localAudioService,
    nativeApplication = nativeApplication,
    customSoundOptions = AppSetting.customWakeSounds,
    soundSetting = AppSetting.wakeSound,
    soundVolume = AppSetting.wakeSoundVolume,
    soundFolderType = Wake
) {

    override val playSound = ILocalAudioService::playWakeSoundWithoutParameter

}
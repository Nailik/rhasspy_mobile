package org.rhasspy.mobile.viewmodel.settings.indication.sound

import org.rhasspy.mobile.logic.services.localaudio.LocalAudioService
import org.rhasspy.mobile.logic.settings.AppSetting
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.file.FolderType

class WakeIndicationSoundSettingsViewModel(
    localAudioService: LocalAudioService,
    nativeApplication: NativeApplication,
) : IIndicationSoundSettingsViewModel(
    localAudioService = localAudioService,
    nativeApplication = nativeApplication,
    customSoundOptions = AppSetting.customWakeSounds,
    soundSetting = AppSetting.wakeSound,
    soundVolume = AppSetting.wakeSoundVolume,
    soundFolderType = FolderType.SoundFolder.Wake
) {

    override val playSound = LocalAudioService::playWakeSoundWithoutParameter

}
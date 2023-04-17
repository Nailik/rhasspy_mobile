package org.rhasspy.mobile.viewmodel.settings.indication.sound

import org.rhasspy.mobile.logic.services.localaudio.LocalAudioService
import org.rhasspy.mobile.logic.settings.AppSetting
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.file.FolderType.SoundFolder.Error

class ErrorIndicationSoundSettingsViewModel(
    localAudioService: LocalAudioService,
    nativeApplication: NativeApplication,
) : IIndicationSoundSettingsViewModel(
    localAudioService = localAudioService,
    nativeApplication = nativeApplication,
    customSoundOptions = AppSetting.customErrorSounds,
    soundSetting = AppSetting.errorSound,
    soundVolume = AppSetting.errorSoundVolume,
    soundFolderType = Error
) {

    override val playSound = LocalAudioService::playErrorSound

}
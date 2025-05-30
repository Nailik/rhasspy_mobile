package org.rhasspy.mobile.viewmodel.settings.indication.sound

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.logic.services.localaudio.ILocalAudioService
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.file.FolderType.SoundFolder.Error
import org.rhasspy.mobile.settings.AppSetting

@Stable
class ErrorIndicationSoundSettingsViewModel(
    localAudioService: ILocalAudioService,
    nativeApplication: NativeApplication
) : IIndicationSoundSettingsViewModel(
    localAudioService = localAudioService,
    nativeApplication = nativeApplication,
    customSoundOptions = AppSetting.customErrorSounds,
    soundSetting = AppSetting.errorSound,
    soundVolume = AppSetting.errorSoundVolume,
    soundFolderType = Error
) {

    override val playSound = ILocalAudioService::playErrorSound

}
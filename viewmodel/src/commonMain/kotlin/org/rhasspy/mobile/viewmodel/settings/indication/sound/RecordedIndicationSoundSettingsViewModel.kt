package org.rhasspy.mobile.viewmodel.settings.indication.sound

import org.rhasspy.mobile.logic.services.localaudio.LocalAudioService
import org.rhasspy.mobile.logic.settings.AppSetting
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.file.FolderType

class RecordedIndicationSoundSettingsViewModel(
    localAudioService: LocalAudioService,
    nativeApplication: NativeApplication,
) : IIndicationSoundSettingsViewModel(
    localAudioService = localAudioService,
    nativeApplication = nativeApplication,
    customSoundOptions = AppSetting.customRecordedSounds,
    soundSetting = AppSetting.recordedSound,
    soundVolume = AppSetting.recordedSoundVolume,
    soundFolderType = FolderType.SoundFolder.Recorded
) {

    override val playSound = LocalAudioService::playRecordedSound

}
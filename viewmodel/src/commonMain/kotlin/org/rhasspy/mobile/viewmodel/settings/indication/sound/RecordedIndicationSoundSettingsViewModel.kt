package org.rhasspy.mobile.viewmodel.settings.indication.sound

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.logic.services.localaudio.ILocalAudioService
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.file.FolderType.SoundFolder.Recorded
import org.rhasspy.mobile.settings.AppSetting

@Stable
class RecordedIndicationSoundSettingsViewModel(
    localAudioService: ILocalAudioService,
    nativeApplication: NativeApplication
) : IIndicationSoundSettingsViewModel(
    localAudioService = localAudioService,
    nativeApplication = nativeApplication,
    customSoundOptions = AppSetting.customRecordedSounds,
    soundSetting = AppSetting.recordedSound,
    soundVolume = AppSetting.recordedSoundVolume,
    soundFolderType = Recorded
) {

    override val playSound = ILocalAudioService::playRecordedSound

}
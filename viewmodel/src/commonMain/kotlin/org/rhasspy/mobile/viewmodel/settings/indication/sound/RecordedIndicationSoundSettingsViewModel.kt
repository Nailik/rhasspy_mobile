package org.rhasspy.mobile.viewmodel.settings.indication.sound

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.logic.services.localaudio.LocalAudioService
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.file.FolderType.SoundFolder.Recorded

@Stable
class RecordedIndicationSoundSettingsViewModel(
    localAudioService: LocalAudioService,
    nativeApplication: NativeApplication,
    viewStateCreator: IIndicationSoundSettingsViewStateCreator
) : IIndicationSoundSettingsViewModel(
    localAudioService = localAudioService,
    nativeApplication = nativeApplication,
    customSoundOptions = AppSetting.customRecordedSounds,
    soundSetting = AppSetting.recordedSound,
    soundVolume = AppSetting.recordedSoundVolume,
    soundFolderType = Recorded,
    viewStateCreator = viewStateCreator
) {

    override val playSound = LocalAudioService::playRecordedSound

}
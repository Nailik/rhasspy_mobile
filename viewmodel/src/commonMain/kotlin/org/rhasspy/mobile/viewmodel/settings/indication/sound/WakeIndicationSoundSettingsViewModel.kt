package org.rhasspy.mobile.viewmodel.settings.indication.sound

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.logic.services.localaudio.LocalAudioService
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.file.FolderType.SoundFolder.Wake
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.navigation.Navigator

@Stable
class WakeIndicationSoundSettingsViewModel(
    localAudioService: LocalAudioService,
    nativeApplication: NativeApplication,
    navigator: Navigator,
    viewStateCreator: IIndicationSoundSettingsViewStateCreator
) : IIndicationSoundSettingsViewModel(
    localAudioService = localAudioService,
    nativeApplication = nativeApplication,
    navigator = navigator,
    customSoundOptions = AppSetting.customWakeSounds,
    soundSetting = AppSetting.wakeSound,
    soundVolume = AppSetting.wakeSoundVolume,
    soundFolderType = Wake,
    viewStateCreator = viewStateCreator
) {

    override val playSound = LocalAudioService::playWakeSoundWithoutParameter

}
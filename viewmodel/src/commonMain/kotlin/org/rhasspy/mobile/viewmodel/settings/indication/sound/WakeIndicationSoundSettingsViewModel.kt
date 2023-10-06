package org.rhasspy.mobile.viewmodel.settings.indication.sound

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.logic.connections.user.IUserConnection
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.file.FolderType.SoundFolder.Wake
import org.rhasspy.mobile.settings.AppSetting

@Stable
class WakeIndicationSoundSettingsViewModel(
    nativeApplication: NativeApplication,
    userConnection: IUserConnection,
) : IIndicationSoundSettingsViewModel(
    userConnection = userConnection,
    nativeApplication = nativeApplication,
    customSoundOptions = AppSetting.customWakeSounds,
    soundSetting = AppSetting.wakeSound,
    soundVolume = AppSetting.wakeSoundVolume,
    soundFolderType = Wake
) {

    override val playSound = userConnection::playWakeSound

}
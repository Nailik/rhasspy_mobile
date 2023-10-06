package org.rhasspy.mobile.viewmodel.settings.indication.sound

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.logic.connections.user.IUserConnection
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.file.FolderType.SoundFolder.Error
import org.rhasspy.mobile.settings.AppSetting

@Stable
class ErrorIndicationSoundSettingsViewModel(
    nativeApplication: NativeApplication,
    userConnection: IUserConnection,
) : IIndicationSoundSettingsViewModel(
    userConnection = userConnection,
    nativeApplication = nativeApplication,
    customSoundOptions = AppSetting.customErrorSounds,
    soundSetting = AppSetting.errorSound,
    soundVolume = AppSetting.errorSoundVolume,
    soundFolderType = Error
) {

    override val playSound = userConnection::playErrorSound

}
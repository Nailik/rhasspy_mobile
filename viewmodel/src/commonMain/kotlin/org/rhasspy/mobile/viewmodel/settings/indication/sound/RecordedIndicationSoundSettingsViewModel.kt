package org.rhasspy.mobile.viewmodel.settings.indication.sound

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.logic.connections.user.IUserConnection
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.file.FolderType.SoundFolder.Recorded
import org.rhasspy.mobile.settings.AppSetting

@Stable
class RecordedIndicationSoundSettingsViewModel(
    nativeApplication: NativeApplication,
    userConnection: IUserConnection,
) : IIndicationSoundSettingsViewModel(
    userConnection = userConnection,
    nativeApplication = nativeApplication,
    customSoundOptions = AppSetting.customRecordedSounds,
    soundSetting = AppSetting.recordedSound,
    soundVolume = AppSetting.recordedSoundVolume,
    soundFolderType = Recorded
) {

    override val playSound = userConnection::playRecordedSound

}
package org.rhasspy.mobile.viewmodel.settings.indication.sound

import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.logic.connections.user.IUserConnection
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.volume.DeviceVolume
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.settings.ISetting

class IIndicationSoundSettingsViewStateCreator(
    private val customSoundOptions: ISetting<List<String>>,
    private val soundSetting: ISetting<String>,
    private val soundVolume: ISetting<Float>,
    private val userConnection: IUserConnection,
) {

    private val updaterScope = CoroutineScope(Dispatchers.IO)

    operator fun invoke(): MutableStateFlow<IIndicationSoundSettingsViewState> {
        val viewState = MutableStateFlow(getViewState())

        updaterScope.launch {
            combineStateFlow(
                DeviceVolume.volumeFlowSound,
                DeviceVolume.volumeFlowNotification,
                AppSetting.soundIndicationOutputOption.data,
                userConnection.isPlayingState,
                customSoundOptions.data,
                soundSetting.data,
                soundVolume.data
            ).collect {
                viewState.value = getViewState()
            }
        }

        return viewState
    }


    private fun getViewState(): IIndicationSoundSettingsViewState {
        return IIndicationSoundSettingsViewState(
            soundSetting = soundSetting.value,
            customSoundFiles = customSoundOptions.value.toImmutableList(),
            soundVolume = soundVolume.value,
            isAudioPlaying = userConnection.isPlayingState.value,
            audioOutputOption = AppSetting.soundIndicationOutputOption.value,
            deviceSoundVolume = DeviceVolume.volumeFlowSound.value,
            deviceNotificationVolume = DeviceVolume.volumeFlowNotification.value
        )
    }

}
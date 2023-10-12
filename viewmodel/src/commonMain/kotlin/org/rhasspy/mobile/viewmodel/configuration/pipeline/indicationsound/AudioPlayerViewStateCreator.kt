package org.rhasspy.mobile.viewmodel.configuration.pipeline.indicationsound

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.service.option.AudioOutputOption
import org.rhasspy.mobile.logic.connections.user.IUserConnection
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.platformspecific.volume.DeviceVolume
import org.rhasspy.mobile.settings.ConfigurationSetting

class AudioPlayerViewStateCreator(
    private val userConnection: IUserConnection,
) {

    operator fun invoke(): StateFlow<AudioPlayerViewState> {
        return combineStateFlow(
            DeviceVolume.volumeFlowSound,
            DeviceVolume.volumeFlowNotification,
            ConfigurationSetting.pipelineData.data,
            userConnection.isPlayingState,
        ).mapReadonlyState {
            getViewState()
        }
    }

    private fun getViewState(): AudioPlayerViewState {
        val audioOutputOption = ConfigurationSetting.pipelineData.data.value.localPipelineData.soundIndicationOutputOption
        return AudioPlayerViewState(
            isAudioPlaying = userConnection.isPlayingState.value,
            audioOutputOption = audioOutputOption,
            isNoSoundInformationBoxVisible = when (audioOutputOption) {
                AudioOutputOption.Sound        -> DeviceVolume.volumeFlowSound.value == 0
                AudioOutputOption.Notification -> DeviceVolume.volumeFlowNotification.value == 0
            }
        )
    }


}
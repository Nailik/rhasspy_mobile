package org.rhasspy.mobile.viewmodel.screens.home

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.service.option.WakeDomainOption
import org.rhasspy.mobile.logic.connections.user.IUserConnection
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.microphone.MicrophoneFabViewStateCreator

class HomeScreenViewStateCreator(
    microphoneFabViewStateCreator: MicrophoneFabViewStateCreator,
    private val userConnection: IUserConnection,
) {

    private val microphoneFabViewStateFlow = microphoneFabViewStateCreator()

    operator fun invoke(): StateFlow<HomeScreenViewState> {

        return combineStateFlow(
            ConfigurationSetting.wakeDomainData.data,
            userConnection.isPlayingState,
            userConnection.isWakeUpEnabled,
            microphoneFabViewStateFlow
        ).mapReadonlyState {
            getViewState()
        }
    }

    private fun getViewState(): HomeScreenViewState {
        return HomeScreenViewState(
            //TODO#466 add isWakeUpEnabled
            isMicrophonePermissionRequired = ConfigurationSetting.wakeDomainData.value.wakeDomainOption in listOf(WakeDomainOption.Porcupine, WakeDomainOption.Udp), //TODO#466 move to user connection
            isPlayingRecording = userConnection.isPlayingState.value,
            isPlayingRecordingEnabled = !userConnection.isPlayingState.value,
            microphoneFabViewState = microphoneFabViewStateFlow.value,
        )
    }
}
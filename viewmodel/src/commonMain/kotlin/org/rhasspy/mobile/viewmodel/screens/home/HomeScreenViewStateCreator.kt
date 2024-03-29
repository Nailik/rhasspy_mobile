package org.rhasspy.mobile.viewmodel.screens.home

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.service.option.WakeWordOption
import org.rhasspy.mobile.logic.middleware.IServiceMiddleware
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.microphone.MicrophoneFabViewStateCreator

class HomeScreenViewStateCreator(
    microphoneFabViewStateCreator: MicrophoneFabViewStateCreator,
    private val serviceMiddleware: IServiceMiddleware
) {

    private val microphoneFabViewStateFlow = microphoneFabViewStateCreator()

    operator fun invoke(): StateFlow<HomeScreenViewState> {

        return combineStateFlow(
            ConfigurationSetting.wakeWordOption.data,
            serviceMiddleware.isPlayingRecording,
            serviceMiddleware.isPlayingRecordingEnabled,
            microphoneFabViewStateFlow
        ).mapReadonlyState {
            getViewState()
        }
    }

    private fun getViewState(): HomeScreenViewState {
        return HomeScreenViewState(
            isMicrophonePermissionRequired = ConfigurationSetting.wakeWordOption.value in listOf(WakeWordOption.Porcupine, WakeWordOption.Udp),
            isPlayingRecording = serviceMiddleware.isPlayingRecording.value,
            isPlayingRecordingEnabled = serviceMiddleware.isPlayingRecordingEnabled.value,
            microphoneFabViewState = microphoneFabViewStateFlow.value,
        )
    }
}
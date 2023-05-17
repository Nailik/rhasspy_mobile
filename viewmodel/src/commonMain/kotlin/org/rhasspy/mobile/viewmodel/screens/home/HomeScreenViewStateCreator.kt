package org.rhasspy.mobile.viewmodel.screens.home

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.logic.middleware.ServiceMiddleware
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.settings.ConfigurationSetting

class HomeScreenViewStateCreator(
    private val serviceMiddleware: ServiceMiddleware
) {

    private val updaterScope = CoroutineScope(Dispatchers.IO)

    operator fun invoke(): StateFlow<HomeScreenViewState> {
        val viewState = MutableStateFlow(getViewState())

        updaterScope.launch {
            combineStateFlow(
                ConfigurationSetting.wakeWordOption.data,
                serviceMiddleware.isPlayingRecording,
                serviceMiddleware.isPlayingRecordingEnabled
            ).collect {
                viewState.value = getViewState()
            }
        }

        return viewState
    }

    private fun getViewState(): HomeScreenViewState {
        return HomeScreenViewState(
            wakeWordOption = ConfigurationSetting.wakeWordOption.value,
            isPlayingRecording = serviceMiddleware.isPlayingRecording.value,
            isPlayingRecordingEnabled = serviceMiddleware.isPlayingRecordingEnabled.value
        )
    }
}
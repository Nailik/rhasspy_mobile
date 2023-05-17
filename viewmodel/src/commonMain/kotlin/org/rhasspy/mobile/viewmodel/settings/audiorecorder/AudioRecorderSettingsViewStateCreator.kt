package org.rhasspy.mobile.viewmodel.settings.audiorecorder

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.settings.AppSetting

class AudioRecorderSettingsViewStateCreator {

    private val updaterScope = CoroutineScope(Dispatchers.IO)

    operator fun invoke(): StateFlow<AudioRecorderSettingsViewState> {
        val viewState = MutableStateFlow(getViewState())

        updaterScope.launch {
            combineStateFlow(
                AppSetting.audioRecorderChannel.data,
                AppSetting.audioRecorderEncoding.data,
                AppSetting.audioRecorderSampleRate.data,
            ).collect {
                viewState.value = getViewState()
            }
        }

        return viewState
    }

    private fun getViewState(): AudioRecorderSettingsViewState {
        return AudioRecorderSettingsViewState(
            audioRecorderChannelType = AppSetting.audioRecorderChannel.value,
            audioRecorderEncodingType = AppSetting.audioRecorderEncoding.value,
            audioRecorderSampleRateType = AppSetting.audioRecorderSampleRate.value
        )
    }

}
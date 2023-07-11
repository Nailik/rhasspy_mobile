package org.rhasspy.mobile.viewmodel.settings.audiorecorder

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.AppSetting

class AudioRecorderSettingsViewStateCreator {

    operator fun invoke(): StateFlow<AudioRecorderSettingsViewState> {
        return combineStateFlow(
            AppSetting.audioRecorderChannel.data,
            AppSetting.audioRecorderEncoding.data,
            AppSetting.audioRecorderSampleRate.data,
        ).mapReadonlyState {
            getViewState()
        }
    }

    private fun getViewState(): AudioRecorderSettingsViewState {
        return AudioRecorderSettingsViewState(
            audioRecorderChannelType = AppSetting.audioRecorderChannel.value,
            audioRecorderEncodingType = AppSetting.audioRecorderEncoding.value,
            audioRecorderSampleRateType = AppSetting.audioRecorderSampleRate.value
        )
    }

}
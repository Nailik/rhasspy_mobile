package org.rhasspy.mobile.logic.domains.speechtotext

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.settings.ConfigurationSetting

internal class SpeechToTextServiceParamsCreator {

    operator fun invoke(): StateFlow<SpeechToTextServiceParams> {

        return combineStateFlow(
            AppSetting.isPauseRecordingOnMedia.data,
            ConfigurationSetting.speechToTextOption.data,
            ConfigurationSetting.dialogManagementOption.data,
            ConfigurationSetting.speechToTextAudioRecorderChannel.data,
            ConfigurationSetting.speechToTextAudioRecorderEncoding.data,
            ConfigurationSetting.speechToTextAudioRecorderSampleRate.data,
            ConfigurationSetting.speechToTextAudioOutputChannel.data,
            ConfigurationSetting.speechToTextAudioOutputEncoding.data,
            ConfigurationSetting.speechToTextAudioOutputSampleRate.data,
        ).mapReadonlyState {
            getParams()
        }

    }

    private fun getParams(): SpeechToTextServiceParams {
        return SpeechToTextServiceParams(
            isAutoPauseOnMediaPlayback = AppSetting.isPauseRecordingOnMedia.value,
            speechToTextOption = ConfigurationSetting.speechToTextOption.value,
            dialogManagementOption = ConfigurationSetting.dialogManagementOption.value,
            audioRecorderChannelType = ConfigurationSetting.speechToTextAudioRecorderChannel.value,
            audioRecorderEncodingType = ConfigurationSetting.speechToTextAudioRecorderEncoding.value,
            audioRecorderSampleRateType = ConfigurationSetting.speechToTextAudioRecorderSampleRate.value,
            audioOutputChannelType = ConfigurationSetting.speechToTextAudioOutputChannel.value,
            audioOutputEncodingType = ConfigurationSetting.speechToTextAudioOutputEncoding.value,
            audioOutputSampleRateType = ConfigurationSetting.speechToTextAudioOutputSampleRate.value,
            httpConnectionId = null
        )
    }

}
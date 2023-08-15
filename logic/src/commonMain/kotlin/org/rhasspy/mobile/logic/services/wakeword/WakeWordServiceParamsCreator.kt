package org.rhasspy.mobile.logic.services.wakeword

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.platformspecific.permission.IMicrophonePermission
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.settings.ConfigurationSetting

internal class WakeWordServiceParamsCreator(
    private val microphonePermission: IMicrophonePermission
) {

    operator fun invoke(): StateFlow<WakeWordServiceParams> {

        return combineStateFlow(
            microphonePermission.granted,
            AppSetting.isHotWordEnabled.data,
            AppSetting.isPauseRecordingOnPlayback.data,
            ConfigurationSetting.wakeWordAudioRecorderChannel.data,
            ConfigurationSetting.wakeWordAudioRecorderEncoding.data,
            ConfigurationSetting.wakeWordAudioRecorderSampleRate.data,
            ConfigurationSetting.wakeWordAudioOutputChannel.data,
            ConfigurationSetting.wakeWordAudioOutputEncoding.data,
            ConfigurationSetting.wakeWordAudioOutputSampleRate.data,
            ConfigurationSetting.wakeWordOption.data,
            ConfigurationSetting.wakeWordPorcupineAccessToken.data,
            ConfigurationSetting.wakeWordPorcupineKeywordDefaultOptions.data,
            ConfigurationSetting.wakeWordPorcupineKeywordCustomOptions.data,
            ConfigurationSetting.wakeWordPorcupineLanguage.data,
            ConfigurationSetting.wakeWordUdpOutputHost.data,
            ConfigurationSetting.wakeWordUdpOutputPort.data
        ).mapReadonlyState {
            getParams()
        }

    }

    private fun getParams(): WakeWordServiceParams {
        return WakeWordServiceParams(
            isMicrophonePermissionEnabled = microphonePermission.granted.value,
            isEnabled = AppSetting.isHotWordEnabled.value,
            isAutoPauseOnMediaPlayback = AppSetting.isPauseRecordingOnPlayback.value,
            audioRecorderChannelType = ConfigurationSetting.wakeWordAudioRecorderChannel.value,
            audioRecorderEncodingType = ConfigurationSetting.wakeWordAudioRecorderEncoding.value,
            audioRecorderSampleRateType = ConfigurationSetting.wakeWordAudioRecorderSampleRate.value,
            audioOutputChannelType = ConfigurationSetting.wakeWordAudioOutputChannel.value,
            audioOutputEncodingType = ConfigurationSetting.wakeWordAudioOutputEncoding.value,
            audioOutputSampleRateType = ConfigurationSetting.wakeWordAudioOutputSampleRate.value,
            wakeWordOption = ConfigurationSetting.wakeWordOption.value,
            wakeWordPorcupineAccessToken = ConfigurationSetting.wakeWordPorcupineAccessToken.value,
            wakeWordPorcupineKeywordDefaultOptions = ConfigurationSetting.wakeWordPorcupineKeywordDefaultOptions.value,
            wakeWordPorcupineKeywordCustomOptions = ConfigurationSetting.wakeWordPorcupineKeywordCustomOptions.value,
            wakeWordPorcupineLanguage = ConfigurationSetting.wakeWordPorcupineLanguage.value,
            wakeWordUdpOutputHost = ConfigurationSetting.wakeWordUdpOutputHost.value,
            wakeWordUdpOutputPort = ConfigurationSetting.wakeWordUdpOutputPort.value
        )
    }

}
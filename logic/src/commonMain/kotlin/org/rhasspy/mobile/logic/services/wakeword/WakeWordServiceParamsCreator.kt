package org.rhasspy.mobile.logic.services.wakeword

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.settings.ConfigurationSetting

internal class WakeWordServiceParamsCreator {

    operator fun invoke(): StateFlow<WakeWordServiceParams> {

        return combineStateFlow(
            AppSetting.audioRecorderSampleRate.data,
            AppSetting.audioRecorderChannel.data,
            AppSetting.audioRecorderEncoding.data,
            AppSetting.isHotWordEnabled.data,
            ConfigurationSetting.wakeWordPorcupineAudioRecorderSettings.data,
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
            isEnabled = AppSetting.isHotWordEnabled.value,
            audioRecorderSampleRateType = AppSetting.audioRecorderSampleRate.value,
            audioRecorderChannelType = AppSetting.audioRecorderChannel.value,
            audioRecorderEncodingType = AppSetting.audioRecorderEncoding.value,
            wakeWordOption = ConfigurationSetting.wakeWordOption.value,
            isUseCustomRecorder = ConfigurationSetting.wakeWordPorcupineAudioRecorderSettings.value,
            wakeWordPorcupineAccessToken = ConfigurationSetting.wakeWordPorcupineAccessToken.value,
            wakeWordPorcupineKeywordDefaultOptions = ConfigurationSetting.wakeWordPorcupineKeywordDefaultOptions.value,
            wakeWordPorcupineKeywordCustomOptions = ConfigurationSetting.wakeWordPorcupineKeywordCustomOptions.value,
            wakeWordPorcupineLanguage = ConfigurationSetting.wakeWordPorcupineLanguage.value,
            wakeWordUdpOutputHost = ConfigurationSetting.wakeWordUdpOutputHost.value,
            wakeWordUdpOutputPort = ConfigurationSetting.wakeWordUdpOutputPort.value
        )
    }

}
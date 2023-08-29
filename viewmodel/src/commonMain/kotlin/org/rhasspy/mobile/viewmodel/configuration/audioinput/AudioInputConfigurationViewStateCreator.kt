package org.rhasspy.mobile.viewmodel.configuration.audioinput

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationViewState.SpeechToTextConfigurationData.SpeechToTextAudioOutputConfigurationData
import org.rhasspy.mobile.viewmodel.configuration.speechtotext.SpeechToTextConfigurationViewState.SpeechToTextConfigurationData.SpeechToTextAudioRecorderConfigurationData
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewState.WakeWordConfigurationData.WakeWordAudioOutputConfigurationData
import org.rhasspy.mobile.viewmodel.configuration.wakeword.WakeWordConfigurationViewState.WakeWordConfigurationData.WakeWordAudioRecorderConfigurationData

class AudioInputConfigurationViewStateCreator {

    operator fun invoke(): StateFlow<AudioInputConfigurationViewState> {
        return combineStateFlow(
            ConfigurationSetting.wakeWordAudioRecorderChannel.data,
            ConfigurationSetting.wakeWordAudioRecorderEncoding.data,
            ConfigurationSetting.wakeWordAudioRecorderSampleRate.data,
            ConfigurationSetting.wakeWordAudioOutputChannel.data,
            ConfigurationSetting.wakeWordAudioOutputEncoding.data,
            ConfigurationSetting.wakeWordAudioOutputSampleRate.data,
            ConfigurationSetting.speechToTextAudioRecorderChannel.data,
            ConfigurationSetting.speechToTextAudioRecorderEncoding.data,
            ConfigurationSetting.speechToTextAudioRecorderSampleRate.data,
            ConfigurationSetting.speechToTextAudioOutputChannel.data,
            ConfigurationSetting.speechToTextAudioOutputEncoding.data,
            ConfigurationSetting.speechToTextAudioOutputSampleRate.data,
        ).mapReadonlyState {
            getViewState()
        }
    }

    private fun getViewState(): AudioInputConfigurationViewState {
        return AudioInputConfigurationViewState(
            wakeWordAudioRecorderData = WakeWordAudioRecorderConfigurationData(),
            wakeWordAudioOutputData = WakeWordAudioOutputConfigurationData(),
            speechToTextAudioRecorderData = SpeechToTextAudioRecorderConfigurationData(),
            speechToTextAudioOutputData = SpeechToTextAudioOutputConfigurationData()
        )

    }

}
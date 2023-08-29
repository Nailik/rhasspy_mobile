package org.rhasspy.mobile.viewmodel.configuration.speechtotext

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.audiorecorder.AudioFormatChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatSampleRateType
import org.rhasspy.mobile.data.httpclient.HttpClientPath
import org.rhasspy.mobile.data.service.option.SpeechToTextOption
import org.rhasspy.mobile.platformspecific.toImmutableList
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState.IConfigurationData

@Stable
data class SpeechToTextConfigurationViewState internal constructor(
    override val editData: SpeechToTextConfigurationData,
    val isOutputEncodingChangeEnabled: Boolean
) : IConfigurationViewState {

    @Stable
    data class SpeechToTextConfigurationData internal constructor(
        val speechToTextOption: SpeechToTextOption = ConfigurationSetting.speechToTextOption.value,
        val isUseCustomSpeechToTextHttpEndpoint: Boolean = ConfigurationSetting.isUseCustomSpeechToTextHttpEndpoint.value,
        val isUseSpeechToTextMqttSilenceDetection: Boolean = ConfigurationSetting.isUseSpeechToTextMqttSilenceDetection.value,
        val speechToTextHttpEndpoint: String = ConfigurationSetting.speechToTextHttpEndpoint.value,
        val speechToTextAudioRecorderData: SpeechToTextAudioRecorderConfigurationData = SpeechToTextAudioRecorderConfigurationData(),
        val speechToTextAudioOutputData: SpeechToTextAudioOutputConfigurationData = SpeechToTextAudioOutputConfigurationData()
    ) : IConfigurationData {

        val speechToTextOptions: ImmutableList<SpeechToTextOption> = SpeechToTextOption.values().toImmutableList()

        val speechToTextHttpEndpointText: String
            get() = if (isUseCustomSpeechToTextHttpEndpoint) speechToTextHttpEndpoint else "${ConfigurationSetting.httpClientServerEndpointHost.value}:${ConfigurationSetting.httpClientServerEndpointPort.value}/${HttpClientPath.SpeechToText.path}"

        @Stable
        data class SpeechToTextAudioRecorderConfigurationData(
            val audioRecorderChannelType: AudioFormatChannelType = ConfigurationSetting.speechToTextAudioRecorderChannel.value,
            val audioRecorderEncodingType: AudioFormatEncodingType = ConfigurationSetting.speechToTextAudioRecorderEncoding.value,
            val audioRecorderSampleRateType: AudioFormatSampleRateType = ConfigurationSetting.speechToTextAudioRecorderSampleRate.value,
        ) {
            val audioRecorderChannelTypes: ImmutableList<AudioFormatChannelType> = AudioFormatChannelType.values().toImmutableList()
            val audioRecorderEncodingTypes: ImmutableList<AudioFormatEncodingType> = AudioFormatEncodingType.supportedValues().toImmutableList()
            val audioRecorderSampleRateTypes: ImmutableList<AudioFormatSampleRateType> = AudioFormatSampleRateType.values().toImmutableList()
        }

        @Stable
        data class SpeechToTextAudioOutputConfigurationData(
            val audioOutputChannelType: AudioFormatChannelType = ConfigurationSetting.speechToTextAudioOutputChannel.value,
            val audioOutputEncodingType: AudioFormatEncodingType = ConfigurationSetting.speechToTextAudioOutputEncoding.value,
            val audioOutputSampleRateType: AudioFormatSampleRateType = ConfigurationSetting.speechToTextAudioOutputSampleRate.value,
        ) {
            val audioOutputChannelTypes: ImmutableList<AudioFormatChannelType> = AudioFormatChannelType.values().toImmutableList()
            val audioOutputEncodingTypes: ImmutableList<AudioFormatEncodingType> = AudioFormatEncodingType.supportedValues().toImmutableList()
            val audioOutputSampleRateTypes: ImmutableList<AudioFormatSampleRateType> = AudioFormatSampleRateType.values().toImmutableList()
        }

    }

}
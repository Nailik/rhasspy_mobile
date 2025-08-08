package org.rhasspy.mobile.viewmodel.configuration.speechtotext

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.audiorecorder.AudioFormatChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatSampleRateType
import org.rhasspy.mobile.data.httpclient.HttpClientPath
import org.rhasspy.mobile.data.service.option.SpeechToTextOption
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState.IConfigurationData

@Stable
data class SpeechToTextConfigurationViewState(
    override val editData: SpeechToTextConfigurationData,
    val isOutputEncodingChangeEnabled: Boolean
) : IConfigurationViewState {

    @Stable
    data class SpeechToTextConfigurationData(
        val speechToTextOption: SpeechToTextOption = ConfigurationSetting.speechToTextOption.value,
        val isUseCustomSpeechToTextHttpEndpoint: Boolean = ConfigurationSetting.isUseCustomSpeechToTextHttpEndpoint.value,
        val isUseSpeechToTextMqttSilenceDetection: Boolean = ConfigurationSetting.isUseSpeechToTextMqttSilenceDetection.value,
        val speechToTextHttpEndpoint: String = ConfigurationSetting.speechToTextHttpEndpoint.value,
        val speechToTextAudioRecorderFormatData: SpeechToTextAudioRecorderFormatConfigurationData = SpeechToTextAudioRecorderFormatConfigurationData(),
        val speechToTextAudioOutputFormatData: SpeechToTextAudioOutputFormatConfigurationData = SpeechToTextAudioOutputFormatConfigurationData()
    ) : IConfigurationData {

        val speechToTextOptions: ImmutableList<SpeechToTextOption> =
            SpeechToTextOption.entries.toTypedArray().toImmutableList()

        val speechToTextHttpEndpointText: String
            get() = if (isUseCustomSpeechToTextHttpEndpoint) speechToTextHttpEndpoint else "${ConfigurationSetting.httpClientServerEndpointHost.value}:${ConfigurationSetting.httpClientServerEndpointPort.value}/${HttpClientPath.SpeechToText.path}"

        @Stable
        data class SpeechToTextAudioRecorderFormatConfigurationData(
            val audioRecorderChannelType: AudioFormatChannelType = ConfigurationSetting.speechToTextAudioRecorderChannel.value,
            val audioRecorderEncodingType: AudioFormatEncodingType = ConfigurationSetting.speechToTextAudioRecorderEncoding.value,
            val audioRecorderSampleRateType: AudioFormatSampleRateType = ConfigurationSetting.speechToTextAudioRecorderSampleRate.value,
        ) {
            val audioRecorderChannelTypes: ImmutableList<AudioFormatChannelType> =
                AudioFormatChannelType.entries.toTypedArray().toImmutableList()
            val audioRecorderEncodingTypes: ImmutableList<AudioFormatEncodingType> =
                AudioFormatEncodingType.supportedValues().toImmutableList()
            val audioRecorderSampleRateTypes: ImmutableList<AudioFormatSampleRateType> =
                AudioFormatSampleRateType.entries.toTypedArray().toImmutableList()
        }

        @Stable
        data class SpeechToTextAudioOutputFormatConfigurationData(
            val audioOutputChannelType: AudioFormatChannelType = ConfigurationSetting.speechToTextAudioOutputChannel.value,
            val audioOutputEncodingType: AudioFormatEncodingType = ConfigurationSetting.speechToTextAudioOutputEncoding.value,
            val audioOutputSampleRateType: AudioFormatSampleRateType = ConfigurationSetting.speechToTextAudioOutputSampleRate.value,
        ) {
            val audioOutputChannelTypes: ImmutableList<AudioFormatChannelType> =
                AudioFormatChannelType.entries.toTypedArray().toImmutableList()
            val audioOutputEncodingTypes: ImmutableList<AudioFormatEncodingType> =
                AudioFormatEncodingType.supportedValues().toImmutableList()
            val audioOutputSampleRateTypes: ImmutableList<AudioFormatSampleRateType> =
                AudioFormatSampleRateType.entries.toTypedArray().toImmutableList()
        }

    }

}
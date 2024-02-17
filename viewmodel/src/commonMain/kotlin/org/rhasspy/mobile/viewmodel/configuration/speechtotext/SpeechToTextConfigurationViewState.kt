package org.rhasspy.mobile.viewmodel.configuration.speechtotext

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.audiorecorder.AudioFormatChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatSampleRateType
import org.rhasspy.mobile.data.audiorecorder.AudioSourceType
import org.rhasspy.mobile.data.service.option.SpeechToTextOption
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
        val isUseSpeechToTextMqttSilenceDetection: Boolean = ConfigurationSetting.isUseSpeechToTextMqttSilenceDetection.value,
        val speechToTextAudioRecorderData: SpeechToTextAudioRecorderConfigurationData = SpeechToTextAudioRecorderConfigurationData(),
        val speechToTextAudioOutputData: SpeechToTextAudioOutputConfigurationData = SpeechToTextAudioOutputConfigurationData()
    ) : IConfigurationData {

        val speechToTextOptions: ImmutableList<SpeechToTextOption> = SpeechToTextOption.entries.toImmutableList()

        @Stable
        data class SpeechToTextAudioRecorderConfigurationData(
            val audioRecorderSourceType: AudioSourceType = ConfigurationSetting.speechToTextAudioRecorderSourceType.value,
            val audioRecorderChannelType: AudioFormatChannelType = ConfigurationSetting.speechToTextAudioRecorderChannel.value,
            val audioRecorderEncodingType: AudioFormatEncodingType = ConfigurationSetting.speechToTextAudioRecorderEncoding.value,
            val audioRecorderSampleRateType: AudioFormatSampleRateType = ConfigurationSetting.speechToTextAudioRecorderSampleRate.value,
        ) {
            val audioRecorderSourceTypes: ImmutableList<AudioSourceType> = AudioSourceType.supportedValues().toImmutableList()
            val audioRecorderChannelTypes: ImmutableList<AudioFormatChannelType> = AudioFormatChannelType.entries.toImmutableList()
            val audioRecorderEncodingTypes: ImmutableList<AudioFormatEncodingType> = AudioFormatEncodingType.supportedValues().toImmutableList()
            val audioRecorderSampleRateTypes: ImmutableList<AudioFormatSampleRateType> = AudioFormatSampleRateType.entries.toImmutableList()
        }

        @Stable
        data class SpeechToTextAudioOutputConfigurationData(
            val audioOutputChannelType: AudioFormatChannelType = ConfigurationSetting.speechToTextAudioOutputChannel.value,
            val audioOutputEncodingType: AudioFormatEncodingType = ConfigurationSetting.speechToTextAudioOutputEncoding.value,
            val audioOutputSampleRateType: AudioFormatSampleRateType = ConfigurationSetting.speechToTextAudioOutputSampleRate.value,
        ) {
            val audioOutputChannelTypes: ImmutableList<AudioFormatChannelType> = AudioFormatChannelType.entries.toImmutableList()
            val audioOutputEncodingTypes: ImmutableList<AudioFormatEncodingType> = AudioFormatEncodingType.supportedValues().toImmutableList()
            val audioOutputSampleRateTypes: ImmutableList<AudioFormatSampleRateType> = AudioFormatSampleRateType.entries.toImmutableList()
        }

    }

}
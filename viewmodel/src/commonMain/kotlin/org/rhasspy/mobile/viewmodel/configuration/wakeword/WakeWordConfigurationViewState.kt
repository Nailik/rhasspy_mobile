package org.rhasspy.mobile.viewmodel.configuration.wakeword

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.audiorecorder.AudioFormatChannelType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatEncodingType
import org.rhasspy.mobile.data.audiorecorder.AudioFormatSampleRateType
import org.rhasspy.mobile.data.data.toStringOrEmpty
import org.rhasspy.mobile.data.porcupine.PorcupineCustomKeyword
import org.rhasspy.mobile.data.porcupine.PorcupineDefaultKeyword
import org.rhasspy.mobile.data.service.option.PorcupineLanguageOption
import org.rhasspy.mobile.data.service.option.WakeWordOption
import org.rhasspy.mobile.platformspecific.toImmutableList
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState.IConfigurationData

@Stable
data class WakeWordConfigurationViewState internal constructor(
    override val editData: WakeWordConfigurationData,
    val isRecorderEncodingChangeEnabled: Boolean,
    val isOutputEncodingChangeEnabled: Boolean,
    val porcupineWakeWordScreen: Int,
    val isMicrophonePermissionRequestVisible: Boolean
) : IConfigurationViewState {

    @Stable
    data class PorcupineCustomKeywordViewState internal constructor(
        val keyword: PorcupineCustomKeyword,
        val deleted: Boolean = false
    )

    @Stable
    data class WakeWordConfigurationData internal constructor(
        val wakeWordOption: WakeWordOption = ConfigurationSetting.wakeWordOption.value,
        val wakeWordPorcupineConfigurationData: WakeWordPorcupineConfigurationData = WakeWordPorcupineConfigurationData(),
        val wakeWordUdpConfigurationData: WakeWordUdpConfigurationData = WakeWordUdpConfigurationData(),
        val wakeWordAudioRecorderData: WakeWordAudioRecorderConfigurationData = WakeWordAudioRecorderConfigurationData(),
        val wakeWordAudioOutputData: WakeWordAudioOutputConfigurationData = WakeWordAudioOutputConfigurationData(),
    ) : IConfigurationData {

        val wakeWordOptions: ImmutableList<WakeWordOption> = WakeWordOption.entries.toTypedArray().toImmutableList()

        @Stable
        data class WakeWordAudioRecorderConfigurationData(
            val audioRecorderChannelType: AudioFormatChannelType = ConfigurationSetting.wakeWordAudioRecorderChannel.value,
            val audioRecorderEncodingType: AudioFormatEncodingType = ConfigurationSetting.wakeWordAudioRecorderEncoding.value,
            val audioRecorderSampleRateType: AudioFormatSampleRateType = ConfigurationSetting.wakeWordAudioRecorderSampleRate.value,
        ) {
            val audioRecorderChannelTypes: ImmutableList<AudioFormatChannelType> = AudioFormatChannelType.entries.toTypedArray().toImmutableList()
            val audioRecorderEncodingTypes: ImmutableList<AudioFormatEncodingType> = AudioFormatEncodingType.supportedValues().toImmutableList()
            val audioRecorderSampleRateTypes: ImmutableList<AudioFormatSampleRateType> = AudioFormatSampleRateType.entries.toTypedArray().toImmutableList()
        }

        @Stable
        data class WakeWordAudioOutputConfigurationData(
            val audioOutputChannelType: AudioFormatChannelType = ConfigurationSetting.wakeWordAudioOutputChannel.value,
            val audioOutputEncodingType: AudioFormatEncodingType = ConfigurationSetting.wakeWordAudioOutputEncoding.value,
            val audioOutputSampleRateType: AudioFormatSampleRateType = ConfigurationSetting.wakeWordAudioOutputSampleRate.value,
        ) {
            val audioOutputChannelTypes: ImmutableList<AudioFormatChannelType> = AudioFormatChannelType.entries.toTypedArray().toImmutableList()
            val audioOutputEncodingTypes: ImmutableList<AudioFormatEncodingType> = AudioFormatEncodingType.supportedValues().toImmutableList()
            val audioOutputSampleRateTypes: ImmutableList<AudioFormatSampleRateType> = AudioFormatSampleRateType.entries.toTypedArray().toImmutableList()
        }

        @Stable
        data class WakeWordPorcupineConfigurationData internal constructor(
            val accessToken: String = ConfigurationSetting.wakeWordPorcupineAccessToken.value,
            val porcupineLanguage: PorcupineLanguageOption = ConfigurationSetting.wakeWordPorcupineLanguage.value,
            val defaultOptions: ImmutableList<PorcupineDefaultKeyword> = ConfigurationSetting.wakeWordPorcupineKeywordDefaultOptions.value.toImmutableList(),
            val customOptions: ImmutableList<PorcupineCustomKeyword> = ConfigurationSetting.wakeWordPorcupineKeywordCustomOptions.value.toImmutableList(),
            val deletedCustomOptions: ImmutableList<PorcupineCustomKeyword> = persistentListOf(),
        ) {

            val languageOptions: ImmutableList<PorcupineLanguageOption> = PorcupineLanguageOption.entries.toTypedArray().toImmutableList()

            val customOptionsUi: ImmutableList<PorcupineCustomKeywordViewState> =
                customOptions.map {
                    PorcupineCustomKeywordViewState(
                        keyword = it,
                        deleted = deletedCustomOptions.contains(it)
                    )
                }.toImmutableList()

            val defaultOptionsUi: ImmutableList<PorcupineDefaultKeyword>
                get() = defaultOptions.filter { it.option.language == porcupineLanguage }.toImmutableList()

            val keywordCount: Int get() = defaultOptionsUi.count { it.isEnabled } + customOptionsUi.count { it.keyword.isEnabled }

        }

        @Stable
        data class WakeWordUdpConfigurationData internal constructor(
            val outputHost: String = ConfigurationSetting.wakeWordUdpOutputHost.value,
            val outputPort: Int? = ConfigurationSetting.wakeWordUdpOutputPort.value
        ) {

            val outputPortText: String = outputPort.toStringOrEmpty()

        }

    }

}
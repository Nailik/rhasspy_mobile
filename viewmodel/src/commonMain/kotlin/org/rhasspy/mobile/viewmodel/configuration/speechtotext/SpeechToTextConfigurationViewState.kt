package org.rhasspy.mobile.viewmodel.configuration.speechtotext

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.service.option.SpeechToTextOption
import org.rhasspy.mobile.logic.services.httpclient.HttpClientPath
import org.rhasspy.mobile.platformspecific.toImmutableList
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationEditViewState

@Stable
data class SpeechToTextConfigurationViewState internal constructor(
    val speechToTextOption: SpeechToTextOption = ConfigurationSetting.speechToTextOption.value,
    val isUseCustomSpeechToTextHttpEndpoint: Boolean = ConfigurationSetting.isUseCustomSpeechToTextHttpEndpoint.value,
    val isUseSpeechToTextMqttSilenceDetection: Boolean = ConfigurationSetting.isUseSpeechToTextMqttSilenceDetection.value,
    val speechToTextHttpEndpoint: String = ConfigurationSetting.speechToTextHttpEndpoint.value,
    val isTestRecordingAudio: Boolean = false
) : IConfigurationEditViewState() {

    val speechToTextOptions: ImmutableList<SpeechToTextOption> = SpeechToTextOption.values().toImmutableList()

    override val isTestingEnabled: Boolean get() = speechToTextOption != SpeechToTextOption.Disabled

    val speechToTextHttpEndpointText: String
        get() = if (isUseCustomSpeechToTextHttpEndpoint) speechToTextHttpEndpoint else
            HttpClientPath.SpeechToText.stringFromConfiguration()

}
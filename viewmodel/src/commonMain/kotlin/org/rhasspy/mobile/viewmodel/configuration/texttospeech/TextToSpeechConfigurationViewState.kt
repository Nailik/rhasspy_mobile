package org.rhasspy.mobile.viewmodel.configuration.texttospeech

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.httpclient.HttpClientPath
import org.rhasspy.mobile.data.service.option.TextToSpeechOption
import org.rhasspy.mobile.platformspecific.toImmutableList
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationEditViewState

@Stable
data class TextToSpeechConfigurationViewState internal constructor(
    val textToSpeechOption: TextToSpeechOption = ConfigurationSetting.textToSpeechOption.value,
    val isUseCustomTextToSpeechHttpEndpoint: Boolean = ConfigurationSetting.isUseCustomTextToSpeechHttpEndpoint.value,
    val textToSpeechHttpEndpoint: String = ConfigurationSetting.textToSpeechHttpEndpoint.value,
    val testTextToSpeechText: String = "",
) : IConfigurationEditViewState() {

    val textToSpeechOptions: ImmutableList<TextToSpeechOption> = TextToSpeechOption.values().toImmutableList()

    override val isTestingEnabled: Boolean get() = textToSpeechOption != TextToSpeechOption.Disabled

    val textToSpeechHttpEndpointText: String
        get() = if (isUseCustomTextToSpeechHttpEndpoint) textToSpeechHttpEndpoint else
            "${ConfigurationSetting.httpClientServerEndpointHost.value}:${ConfigurationSetting.httpClientServerEndpointPort.value}/${HttpClientPath.SpeechToText.path}"

}
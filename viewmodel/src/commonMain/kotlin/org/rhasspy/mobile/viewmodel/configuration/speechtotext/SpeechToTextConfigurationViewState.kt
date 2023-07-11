package org.rhasspy.mobile.viewmodel.configuration.speechtotext

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.httpclient.HttpClientPath
import org.rhasspy.mobile.data.service.option.SpeechToTextOption
import org.rhasspy.mobile.platformspecific.toImmutableList
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState.IConfigurationData

@Stable
data class SpeechToTextConfigurationViewState internal constructor(
    override val editData: SpeechToTextConfigurationData
) : IConfigurationViewState {

    @Stable
    data class SpeechToTextConfigurationData internal constructor(
        val speechToTextOption: SpeechToTextOption = ConfigurationSetting.speechToTextOption.value,
        val isUseCustomSpeechToTextHttpEndpoint: Boolean = ConfigurationSetting.isUseCustomSpeechToTextHttpEndpoint.value,
        val isUseSpeechToTextMqttSilenceDetection: Boolean = ConfigurationSetting.isUseSpeechToTextMqttSilenceDetection.value,
        val speechToTextHttpEndpoint: String = ConfigurationSetting.speechToTextHttpEndpoint.value
    ) : IConfigurationData {

        val speechToTextOptions: ImmutableList<SpeechToTextOption> = SpeechToTextOption.values().toImmutableList()

        val speechToTextHttpEndpointText: String
            get() = if (isUseCustomSpeechToTextHttpEndpoint) speechToTextHttpEndpoint else
                "${ConfigurationSetting.httpClientServerEndpointHost.value}:${ConfigurationSetting.httpClientServerEndpointPort.value}/${HttpClientPath.SpeechToText.path}"

    }

}
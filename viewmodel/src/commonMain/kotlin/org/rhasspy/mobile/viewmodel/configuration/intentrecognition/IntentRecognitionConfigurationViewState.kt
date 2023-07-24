package org.rhasspy.mobile.viewmodel.configuration.intentrecognition

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.httpclient.HttpClientPath
import org.rhasspy.mobile.data.service.option.IntentRecognitionOption
import org.rhasspy.mobile.platformspecific.toImmutableList
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState.IConfigurationData

@Stable
data class IntentRecognitionConfigurationViewState internal constructor(
    override val editData: IntentRecognitionConfigurationData
) : IConfigurationViewState {

    @Stable
    data class IntentRecognitionConfigurationData internal constructor(
        val intentRecognitionOption: IntentRecognitionOption = ConfigurationSetting.intentRecognitionOption.value,
        val isUseCustomIntentRecognitionHttpEndpoint: Boolean = ConfigurationSetting.isUseCustomIntentRecognitionHttpEndpoint.value,
        val intentRecognitionHttpEndpoint: String = ConfigurationSetting.intentRecognitionHttpEndpoint.value
    ) : IConfigurationData {

        val intentRecognitionOptionList: ImmutableList<IntentRecognitionOption> =
            IntentRecognitionOption.values().toImmutableList()

        val intentRecognitionHttpEndpointText: String =
            if (isUseCustomIntentRecognitionHttpEndpoint) intentRecognitionHttpEndpoint else
                "${ConfigurationSetting.httpClientServerEndpointHost.value}:${ConfigurationSetting.httpClientServerEndpointPort.value}/${HttpClientPath.TextToIntent.path}"

    }

}
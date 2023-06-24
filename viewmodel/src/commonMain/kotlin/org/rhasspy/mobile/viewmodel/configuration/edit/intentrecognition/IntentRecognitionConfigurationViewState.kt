package org.rhasspy.mobile.viewmodel.configuration.edit.intentrecognition

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.httpclient.HttpClientPath
import org.rhasspy.mobile.data.service.option.IntentRecognitionOption
import org.rhasspy.mobile.platformspecific.toImmutableList
import org.rhasspy.mobile.settings.ConfigurationSetting

@Stable
data class IntentRecognitionConfigurationViewState internal constructor(
    val editData: IntentRecognitionConfigurationData
) {

    @Stable
    data class IntentRecognitionConfigurationData internal constructor(
        val intentRecognitionOption: IntentRecognitionOption = ConfigurationSetting.intentRecognitionOption.value,
        val isUseCustomIntentRecognitionHttpEndpoint: Boolean = ConfigurationSetting.isUseCustomIntentRecognitionHttpEndpoint.value,
        val intentRecognitionHttpEndpoint: String = ConfigurationSetting.intentRecognitionHttpEndpoint.value
    ) {

        val intentRecognitionOptionList: ImmutableList<IntentRecognitionOption> = IntentRecognitionOption.values().toImmutableList()

        val intentRecognitionHttpEndpointText: String = if (isUseCustomIntentRecognitionHttpEndpoint) intentRecognitionHttpEndpoint else
            "${ConfigurationSetting.httpClientServerEndpointHost.value}:${ConfigurationSetting.httpClientServerEndpointPort.value}/${HttpClientPath.TextToIntent.path}"

    }

}
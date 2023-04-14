package org.rhasspy.mobile.viewmodel.configuration.intentrecognition

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.service.option.IntentRecognitionOption
import org.rhasspy.mobile.logic.services.httpclient.HttpClientPath
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.platformspecific.toImmutableList
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationEditViewState

@Stable
data class IntentRecognitionConfigurationViewState(
    val intentRecognitionOptionList: ImmutableList<IntentRecognitionOption> = IntentRecognitionOption.values().toImmutableList(),
    val intentRecognitionOption: IntentRecognitionOption = ConfigurationSetting.intentRecognitionOption.value,
    val isUseCustomIntentRecognitionHttpEndpoint: Boolean = ConfigurationSetting.isUseCustomIntentRecognitionHttpEndpoint.value,
    val intentRecognitionHttpEndpoint: String = ConfigurationSetting.intentRecognitionHttpEndpoint.value
) : IConfigurationEditViewState() {

    override val hasUnsavedChanges: Boolean
        get() = !(intentRecognitionOption == ConfigurationSetting.intentRecognitionOption.value &&
                isUseCustomIntentRecognitionHttpEndpoint == ConfigurationSetting.isUseCustomIntentRecognitionHttpEndpoint.value &&
                intentRecognitionHttpEndpoint == ConfigurationSetting.intentRecognitionHttpEndpoint.value)

    override val isTestingEnabled: Boolean get() = intentRecognitionOption != IntentRecognitionOption.Disabled

    val intentRecognitionHttpEndpointText: String
        get() = if (isUseCustomIntentRecognitionHttpEndpoint) {
            intentRecognitionHttpEndpoint
        } else HttpClientPath.TextToIntent.fromBaseConfiguration()

}
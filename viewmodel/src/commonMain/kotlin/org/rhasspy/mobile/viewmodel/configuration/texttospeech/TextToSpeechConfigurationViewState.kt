package org.rhasspy.mobile.viewmodel.configuration.texttospeech

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import org.rhasspy.mobile.data.service.option.TextToSpeechOption
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.platformspecific.toImmutableList
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationEditViewState

@Stable
data class TextToSpeechConfigurationViewState(
    val textToSpeechOptions: ImmutableList<TextToSpeechOption> = TextToSpeechOption.values().toImmutableList(),
    val textToSpeechOption: TextToSpeechOption = ConfigurationSetting.textToSpeechOption.value,
    val isUseCustomTextToSpeechHttpEndpoint: Boolean = ConfigurationSetting.isUseCustomTextToSpeechHttpEndpoint.value,
    val textToSpeechHttpEndpoint: String = ConfigurationSetting.textToSpeechHttpEndpoint.value
) : IConfigurationEditViewState() {

    override val hasUnsavedChanges: Boolean
        get() = !(textToSpeechOption == ConfigurationSetting.textToSpeechOption.value &&
                isUseCustomTextToSpeechHttpEndpoint == ConfigurationSetting.isUseCustomTextToSpeechHttpEndpoint.value &&
                textToSpeechHttpEndpoint == ConfigurationSetting.textToSpeechHttpEndpoint.value)

    override val isTestingEnabled: Boolean get() = textToSpeechOption != TextToSpeechOption.Disabled

}
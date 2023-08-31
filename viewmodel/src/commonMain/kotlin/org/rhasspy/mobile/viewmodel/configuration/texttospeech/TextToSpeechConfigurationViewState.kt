package org.rhasspy.mobile.viewmodel.configuration.texttospeech

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.rhasspy.mobile.data.service.option.TextToSpeechOption
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewState.IConfigurationData

@Stable
data class TextToSpeechConfigurationViewState internal constructor(
    override val editData: TextToSpeechConfigurationData
) : IConfigurationViewState {

    @Stable
    data class TextToSpeechConfigurationData(
        val textToSpeechOption: TextToSpeechOption = ConfigurationSetting.textToSpeechOption.value,
    ) : IConfigurationData {

        val textToSpeechOptions: ImmutableList<TextToSpeechOption> = TextToSpeechOption.entries.toImmutableList()

    }

}
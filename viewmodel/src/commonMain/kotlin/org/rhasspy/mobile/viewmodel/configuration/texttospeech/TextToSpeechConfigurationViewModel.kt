package org.rhasspy.mobile.viewmodel.configuration.texttospeech

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.logic.services.httpclient.HttpClientServiceParams
import org.rhasspy.mobile.logic.services.texttospeech.TextToSpeechService
import org.rhasspy.mobile.logic.services.texttospeech.TextToSpeechServiceParams
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.logic.update
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.texttospeech.TextToSpeechConfigurationUiAction.Change
import org.rhasspy.mobile.viewmodel.configuration.texttospeech.TextToSpeechConfigurationUiAction.Change.SelectTextToSpeechOption
import org.rhasspy.mobile.viewmodel.configuration.texttospeech.TextToSpeechConfigurationUiAction.Change.ToggleUseCustomHttpEndpoint
import org.rhasspy.mobile.viewmodel.configuration.texttospeech.TextToSpeechConfigurationUiAction.Change.UpdateTextToSpeechHttpEndpoint

@Stable
class TextToSpeechConfigurationViewModel(
    service: TextToSpeechService,
    testRunner: TextToSpeechConfigurationTest
) : IConfigurationViewModel<TextToSpeechConfigurationTest, TextToSpeechConfigurationViewState>(
    service = service,
    testRunner = testRunner,
    initialViewState = ::TextToSpeechConfigurationViewState
) {

    fun onAction(action: TextToSpeechConfigurationUiAction) {
        when (action) {
            is Change -> onChange(action)
        }
    }

    private fun onChange(change: Change) {
        contentViewState.update {
            when (change) {
                is SelectTextToSpeechOption -> it.copy(textToSpeechOption = change.option)
                ToggleUseCustomHttpEndpoint -> it.copy(isUseCustomTextToSpeechHttpEndpoint = !it.isUseCustomTextToSpeechHttpEndpoint)
                is UpdateTextToSpeechHttpEndpoint -> it.copy(textToSpeechHttpEndpoint = change.value)
            }
        }
    }

    override fun onSave() {
        ConfigurationSetting.textToSpeechOption.value = data.textToSpeechOption
        ConfigurationSetting.isUseCustomTextToSpeechHttpEndpoint.value = data.isUseCustomTextToSpeechHttpEndpoint
        ConfigurationSetting.textToSpeechHttpEndpoint.value = data.textToSpeechHttpEndpoint
    }

    private val _testTextToSpeechText = MutableStateFlow("")
    val testTextToSpeechText = _testTextToSpeechText.readOnly

    //update the test text
    fun updateTestTextToSpeechText(text: String) {
        _testTextToSpeechText.value = text
    }

    override fun initializeTestParams() {
        get<TextToSpeechServiceParams> {
            parametersOf(
                TextToSpeechServiceParams(
                    textToSpeechOption = data.textToSpeechOption
                )
            )
        }

        get<HttpClientServiceParams> {
            parametersOf(
                HttpClientServiceParams(
                    isUseCustomTextToSpeechHttpEndpoint = data.isUseCustomTextToSpeechHttpEndpoint,
                    textToSpeechHttpEndpoint = data.textToSpeechHttpEndpoint
                )
            )
        }
    }

    fun startTextToSpeech() = testRunner.startTextToSpeech(_testTextToSpeechText.value)

}
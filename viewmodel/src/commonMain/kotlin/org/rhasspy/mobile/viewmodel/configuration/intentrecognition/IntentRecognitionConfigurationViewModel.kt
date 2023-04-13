package org.rhasspy.mobile.viewmodel.configuration.intentrecognition

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.logic.services.httpclient.HttpClientServiceParams
import org.rhasspy.mobile.logic.services.intentrecognition.IntentRecognitionService
import org.rhasspy.mobile.logic.services.intentrecognition.IntentRecognitionServiceParams
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationUiAction.ChangeIntentRecognitionHttpEndpoint
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationUiAction.SelectIntentRecognitionOption
import org.rhasspy.mobile.viewmodel.configuration.intentrecognition.IntentRecognitionConfigurationUiAction.ToggleUseCustomHttpEndpoint

class IntentRecognitionConfigurationViewModel(
    service: IntentRecognitionService,
    testRunner: IntentRecognitionConfigurationTest
) : IConfigurationViewModel<IntentRecognitionConfigurationTest, IntentRecognitionConfigurationViewState>(
    service = service,
    testRunner = testRunner,
    initialViewState = ::IntentRecognitionConfigurationViewState
) {

    fun onAction(action: IntentRecognitionConfigurationUiAction){
        contentViewState.update {
            when(action) {
                is ChangeIntentRecognitionHttpEndpoint -> it.copy(intentRecognitionHttpEndpoint = action.value)
                is SelectIntentRecognitionOption -> it.copy(intentRecognitionOption = action.option)
                ToggleUseCustomHttpEndpoint -> it.copy(isUseCustomIntentRecognitionHttpEndpoint = !it.isUseCustomIntentRecognitionHttpEndpoint)
            }
        }
    }

    override fun onSave() {
        ConfigurationSetting.intentRecognitionOption.value = data.intentRecognitionOption
        ConfigurationSetting.isUseCustomIntentRecognitionHttpEndpoint.value = data.isUseCustomIntentRecognitionHttpEndpoint
        ConfigurationSetting.intentRecognitionHttpEndpoint.value = data.intentRecognitionHttpEndpoint
    }

    private val _testIntentRecognitionText = MutableStateFlow("")
    val testIntentRecognitionText = _testIntentRecognitionText.readOnly

    fun updateTestIntentRecognitionText(text: String) {
        _testIntentRecognitionText.value = text
    }

    override fun initializeTestParams() {
        get<IntentRecognitionServiceParams> {
            parametersOf(
                IntentRecognitionServiceParams(
                    intentRecognitionOption = data.intentRecognitionOption
                )
            )
        }

        get<HttpClientServiceParams> {
            parametersOf(
                HttpClientServiceParams(
                    isUseCustomTextToSpeechHttpEndpoint = data.isUseCustomIntentRecognitionHttpEndpoint,
                    intentRecognitionHttpEndpoint = data.intentRecognitionHttpEndpoint,
                )
            )
        }
    }

    fun runIntentRecognition() = testRunner.runIntentRecognition(_testIntentRecognitionText.value)

}
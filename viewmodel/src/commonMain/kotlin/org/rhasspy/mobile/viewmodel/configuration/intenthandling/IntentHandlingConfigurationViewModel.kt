package org.rhasspy.mobile.viewmodel.configuration.intenthandling

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.logic.services.dialog.DialogManagerService
import org.rhasspy.mobile.logic.services.homeassistant.HomeAssistantServiceParams
import org.rhasspy.mobile.logic.services.httpclient.HttpClientServiceParams
import org.rhasspy.mobile.logic.services.intenthandling.IntentHandlingServiceParams
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationUiAction.ChangeIntentHandlingHassAccessToken
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationUiAction.ChangeIntentHandlingHassEndpoint
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationUiAction.ChangeIntentHandlingHttpEndpoint
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationUiAction.SelectIntentHandlingHassOption
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationUiAction.SelectIntentHandlingOption

@Stable
class IntentHandlingConfigurationViewModel(
    service: DialogManagerService,
    testRunner: IntentHandlingConfigurationTest
) : IConfigurationViewModel<IntentHandlingConfigurationTest, IntentHandlingConfigurationViewState>(
    service = service,
    testRunner = testRunner,
    initialViewState = ::IntentHandlingConfigurationViewState
) {

    fun onAction(action: IntentHandlingConfigurationUiAction) {
        contentViewState.update {
            when (action) {
                is ChangeIntentHandlingHassAccessToken -> it.copy(intentHandlingHassAccessToken = action.value)
                is ChangeIntentHandlingHassEndpoint -> it.copy(intentHandlingHassEndpoint = action.value)
                is ChangeIntentHandlingHttpEndpoint -> it.copy(intentHandlingHttpEndpoint = action.value)
                is SelectIntentHandlingHassOption -> it.copy(intentHandlingHassOption = action.option)
                is SelectIntentHandlingOption -> it.copy(intentHandlingOption = action.option)
            }
        }
    }

    private val _testIntentNameText = MutableStateFlow("")
    val testIntentNameText = _testIntentNameText.readOnly
    private val _testIntentText = MutableStateFlow("")
    val testIntentText = _testIntentText.readOnly

    fun updateTestIntentNameText(text: String) {
        _testIntentNameText.value = text
    }

    fun updateTestIntentText(text: String) {
        _testIntentText.value = text
    }

    override fun onSave() {
        ConfigurationSetting.intentHandlingOption.value = data.intentHandlingOption
        ConfigurationSetting.intentHandlingHttpEndpoint.value = data.intentHandlingHttpEndpoint
        ConfigurationSetting.intentHandlingHassEndpoint.value = data.intentHandlingHassEndpoint
        ConfigurationSetting.intentHandlingHassAccessToken.value = data.intentHandlingHassAccessToken
        ConfigurationSetting.intentHandlingHomeAssistantOption.value = data.intentHandlingHassOption
    }

    override fun initializeTestParams() {
        get<IntentHandlingServiceParams> {
            parametersOf(
                IntentHandlingServiceParams(
                    intentHandlingOption = data.intentHandlingOption
                )
            )
        }

        get<HttpClientServiceParams> {
            parametersOf(
                HttpClientServiceParams(
                    intentHandlingHttpEndpoint = data.intentHandlingHttpEndpoint,
                    intentHandlingHassEndpoint = data.intentHandlingHassEndpoint,
                    intentHandlingHassAccessToken = data.intentHandlingHassAccessToken,
                    intentHandlingOption = data.intentHandlingOption
                )
            )
        }

        get<HomeAssistantServiceParams> {
            parametersOf(
                HomeAssistantServiceParams(
                    intentHandlingHomeAssistantOption = data.intentHandlingHassOption
                )
            )
        }
    }

    fun testIntentHandling() =
        testRunner.handleIntent(_testIntentNameText.value, _testIntentText.value)

}
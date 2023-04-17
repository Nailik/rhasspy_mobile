package org.rhasspy.mobile.viewmodel.configuration.intenthandling

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.logic.services.dialog.DialogManagerService
import org.rhasspy.mobile.logic.services.homeassistant.HomeAssistantServiceParams
import org.rhasspy.mobile.logic.services.httpclient.HttpClientServiceParams
import org.rhasspy.mobile.logic.services.intenthandling.IntentHandlingService
import org.rhasspy.mobile.logic.services.intenthandling.IntentHandlingServiceParams
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationUiEvent.Action.RunIntentHandlingTest
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationUiEvent.Change.*

@Stable
class IntentHandlingConfigurationViewModel(
    service: DialogManagerService
) : IConfigurationViewModel<IntentHandlingConfigurationViewState>(
    service = service,
    initialViewState = ::IntentHandlingConfigurationViewState
) {

    fun onEvent(event: IntentHandlingConfigurationUiEvent) {
        when(event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
        }
    }

    private fun onChange(change: Change) {
        contentViewState.update {
            when (change) {
                is ChangeIntentHandlingHassAccessToken -> it.copy(intentHandlingHassAccessToken = change.token)
                is ChangeIntentHandlingHassEndpoint -> it.copy(intentHandlingHassEndpoint = change.endpoint)
                is ChangeIntentHandlingHttpEndpoint -> it.copy(intentHandlingHttpEndpoint = change.endpoint)
                is SelectIntentHandlingHassOption -> it.copy(intentHandlingHassOption = change.option)
                is SelectIntentHandlingOption -> it.copy(intentHandlingOption = change.option)
                is UpdateTestIntentHandlingName -> it.copy(testIntentHandlingName = change.name)
                is UpdateTestIntentHandlingText -> it.copy(testIntentHandlingText = change.text)
            }
        }
    }

    private fun onAction(action: Action) {
        when(action) {
            RunIntentHandlingTest -> {
                testScope.launch {
                    get<IntentHandlingService>().intentHandling(
                        intentName = data.testIntentHandlingName,
                        intent = data.testIntentHandlingText
                    )
                }
            }
        }
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

}
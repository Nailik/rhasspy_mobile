package org.rhasspy.mobile.viewmodel.configuration.intenthandling

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.rhasspy.mobile.logic.services.dialog.DialogManagerService
import org.rhasspy.mobile.logic.services.intenthandling.IntentHandlingService
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
        when (event) {
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
        when (action) {
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

}
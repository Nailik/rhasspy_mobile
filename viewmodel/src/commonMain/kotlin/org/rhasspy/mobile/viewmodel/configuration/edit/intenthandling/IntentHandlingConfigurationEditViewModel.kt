package org.rhasspy.mobile.viewmodel.configuration.edit.intenthandling

import androidx.compose.runtime.Stable
import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.rhasspy.mobile.logic.services.dialog.DialogManagerService
import org.rhasspy.mobile.logic.services.intenthandling.IntentHandlingService
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.edit.IConfigurationEditViewModel
import org.rhasspy.mobile.viewmodel.configuration.edit.intenthandling.IntentHandlingConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.edit.intenthandling.IntentHandlingConfigurationUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.edit.intenthandling.IntentHandlingConfigurationUiEvent.Action.RunIntentHandlingTest
import org.rhasspy.mobile.viewmodel.configuration.edit.intenthandling.IntentHandlingConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.edit.intenthandling.IntentHandlingConfigurationUiEvent.Change.*
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.IntentHandlingConfigurationScreenDestination.EditScreen
import org.rhasspy.mobile.viewmodel.navigation.destinations.configuration.IntentHandlingConfigurationScreenDestination.TestScreen

@Stable
class IntentHandlingConfigurationEditViewModel(
    service: DialogManagerService
) : IConfigurationEditViewModel<IntentHandlingConfigurationViewState>(
    service = service,
    initialViewState = ::IntentHandlingConfigurationViewState,
    testPageDestination = TestScreen
) {

    val screen = navigator.topScreen(EditScreen)

    fun onEvent(event: IntentHandlingConfigurationUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
        }
    }

    private fun onChange(change: Change) {
        updateViewState {
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

            BackClick -> navigator.onBackPressed()
        }
    }

    override fun onDiscard() {}

    override fun onSave() {
        ConfigurationSetting.intentHandlingOption.value = data.intentHandlingOption
        ConfigurationSetting.intentHandlingHttpEndpoint.value = data.intentHandlingHttpEndpoint
        ConfigurationSetting.intentHandlingHomeAssistantEndpoint.value = data.intentHandlingHassEndpoint
        ConfigurationSetting.intentHandlingHomeAssistantAccessToken.value = data.intentHandlingHassAccessToken
        ConfigurationSetting.intentHandlingHomeAssistantOption.value = data.intentHandlingHassOption
    }

}
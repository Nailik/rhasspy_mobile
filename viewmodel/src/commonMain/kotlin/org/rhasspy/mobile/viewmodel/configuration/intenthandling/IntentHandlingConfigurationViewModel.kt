package org.rhasspy.mobile.viewmodel.configuration.intenthandling

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.logic.services.intenthandling.IIntentHandlingService
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewModel
import org.rhasspy.mobile.viewmodel.configuration.ConfigurationViewState
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationUiEvent.Action.ScanHomeAssistantAccessToken
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationUiEvent.Change
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationUiEvent.Change.ChangeIntentHandlingHomeAssistantAccessToken
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationUiEvent.Change.ChangeIntentHandlingHomeAssistantEndpoint
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationUiEvent.Change.ChangeIntentHandlingHttpEndpoint
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationUiEvent.Change.SelectIntentHandlingHomeAssistantOption
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationUiEvent.Change.SelectIntentHandlingOption
import org.rhasspy.mobile.viewmodel.configuration.intenthandling.IntentHandlingConfigurationViewState.IntentHandlingConfigurationData

@Stable
class IntentHandlingConfigurationViewModel(
    service: IIntentHandlingService,
) : ConfigurationViewModel(
    service = service
) {

    private val _viewState =
        MutableStateFlow(IntentHandlingConfigurationViewState(IntentHandlingConfigurationData()))
    val viewState = _viewState.readOnly

    override fun initViewStateCreator(
        configurationViewState: MutableStateFlow<ConfigurationViewState>,
    ): StateFlow<ConfigurationViewState> {
        return viewStateCreator(
            init = ::IntentHandlingConfigurationData,
            viewState = viewState,
            configurationViewState = configurationViewState
        )
    }

    fun onEvent(event: IntentHandlingConfigurationUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
        }
    }

    private fun onChange(change: Change) {
        _viewState.update {
            it.copy(editData = with(it.editData) {
                when (change) {
                    is ChangeIntentHandlingHomeAssistantAccessToken -> copy(
                        intentHandlingHomeAssistantAccessToken = change.token
                    )

                    is ChangeIntentHandlingHomeAssistantEndpoint -> copy(
                        intentHandlingHomeAssistantEndpoint = change.endpoint
                    )

                    is ChangeIntentHandlingHttpEndpoint -> copy(intentHandlingHttpEndpoint = change.endpoint)
                    is SelectIntentHandlingHomeAssistantOption -> copy(
                        intentHandlingHomeAssistantOption = change.option
                    )

                    is SelectIntentHandlingOption -> copy(intentHandlingOption = change.option)
                }
            })
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            BackClick -> navigator.onBackPressed()
            ScanHomeAssistantAccessToken -> scanQRCode {
                onChange(
                    ChangeIntentHandlingHomeAssistantAccessToken(it)
                )
            }
        }
    }

    override fun onDiscard() {
        _viewState.update { it.copy(editData = IntentHandlingConfigurationData()) }
    }

    override fun onSave() {
        with(_viewState.value.editData) {
            ConfigurationSetting.intentHandlingOption.value = intentHandlingOption
            ConfigurationSetting.intentHandlingHttpEndpoint.value = intentHandlingHttpEndpoint
            ConfigurationSetting.intentHandlingHomeAssistantEndpoint.value =
                intentHandlingHomeAssistantEndpoint
            ConfigurationSetting.intentHandlingHomeAssistantAccessToken.value =
                intentHandlingHomeAssistantAccessToken
            ConfigurationSetting.intentHandlingHomeAssistantOption.value =
                intentHandlingHomeAssistantOption
        }
    }

}
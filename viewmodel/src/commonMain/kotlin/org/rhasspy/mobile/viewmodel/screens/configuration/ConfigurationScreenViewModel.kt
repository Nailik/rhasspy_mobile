package org.rhasspy.mobile.viewmodel.screens.configuration

import androidx.compose.runtime.Stable
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.event.EventState.Consumed
import org.rhasspy.mobile.data.event.EventState.Triggered
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.navigation.Navigator
import org.rhasspy.mobile.viewmodel.navigation.Screen.ConfigurationScreen.ConfigurationDetailScreen.*
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiEvent.*
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiEvent.Action.ScrollToError
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiEvent.Change.SiteIdChange
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiEvent.Navigate.*
import org.rhasspy.mobile.viewmodel.screens.configuration.IConfigurationScreenUiStateEvent.ScrollToErrorEventIState

@Stable
class ConfigurationScreenViewModel(
    private val viewStateCreator: ConfigurationScreenViewStateCreator,
    private val navigator: Navigator
) : ViewModel() {

    val viewState: StateFlow<ConfigurationScreenViewState> = viewStateCreator()

    fun onEvent(event: ConfigurationScreenUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
            is Navigate -> onNavigate(event)
        }
    }

    private fun onChange(change: Change) {
        when (change) {
            is SiteIdChange -> ConfigurationSetting.siteId.value = change.text
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            ScrollToError -> viewStateCreator.updateScrollToError(Triggered)
        }
    }

    private fun onNavigate(navigate: Navigate) {
        navigator.navigate(
            when (navigate) {
                AudioPlayingClick -> AudioPlayingConfigurationScreen.EditScreen
                IntentHandlingClick -> IntentHandlingConfigurationScreen.EditScreen
                IntentRecognitionClick -> IntentRecognitionConfigurationScreen.EditScreen
                MqttClick -> MqttConfigurationScreen.EditScreen
                DialogManagementClick -> DialogManagementConfigurationScreen.EditScreen
                RemoteHermesHttpClick -> RemoteHermesHttpConfigurationScreen.EditScreen
                SpeechToTextClick -> SpeechToTextConfigurationScreen.EditScreen
                TextToSpeechClick -> TextToSpeechConfigurationScreen.EditScreen
                WakeWordClick -> WakeWordConfigurationScreen.EditScreen.OverViewScreen
                WebserverClick -> WebServerConfigurationScreen.EditScreen
            }
        )
    }


    fun onConsumed(event: IConfigurationScreenUiStateEvent) {
        when (event) {
            is ScrollToErrorEventIState -> viewStateCreator.updateScrollToError(Consumed)
        }
    }

}
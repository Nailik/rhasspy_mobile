package org.rhasspy.mobile.viewmodel.screens.configuration

import androidx.compose.runtime.Stable
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.event.EventState.Consumed
import org.rhasspy.mobile.data.event.EventState.Triggered
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.navigation.Navigator
import org.rhasspy.mobile.viewmodel.navigation.destinations.ConfigurationScreenDestination
import org.rhasspy.mobile.viewmodel.navigation.destinations.ConfigurationScreenDestination.*
import org.rhasspy.mobile.viewmodel.navigation.destinations.MainNavigationDestination
import org.rhasspy.mobile.viewmodel.navigation.destinations.MainNavigationDestination.HomeScreen
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiEvent.*
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiEvent.Action.BackClick
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
    val screen = navigator.getBackStack(ConfigurationScreenDestination::class, OverviewScreen)

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
            BackClick -> navigator.popBackStack()
        }
    }

    private fun onNavigate(navigate: Navigate) {
        navigator.navigate(
            type = ConfigurationScreenDestination::class,
            screen = when (navigate) {
                AudioPlayingClick -> AudioPlayingConfigurationScreenDestination
                IntentHandlingClick -> IntentHandlingConfigurationScreen
                IntentRecognitionClick -> IntentRecognitionConfigurationScreen
                MqttClick -> MqttConfigurationScreen
                DialogManagementClick -> DialogManagementConfigurationScreen
                RemoteHermesHttpClick -> RemoteHermesHttpConfigurationScreen
                SpeechToTextClick -> SpeechToTextConfigurationScreen
                TextToSpeechClick -> TextToSpeechConfigurationScreen
                WakeWordClick -> WakeWordConfigurationScreen
                WebserverClick -> WebServerConfigurationScreen
            }
        )
    }


    fun onConsumed(event: IConfigurationScreenUiStateEvent) {
        when (event) {
            is ScrollToErrorEventIState -> viewStateCreator.updateScrollToError(Consumed)
        }
    }

}
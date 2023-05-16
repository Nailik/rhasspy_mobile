package org.rhasspy.mobile.viewmodel.screens.settings

import androidx.compose.runtime.Stable
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.viewmodel.navigation.Navigator
import org.rhasspy.mobile.viewmodel.navigation.destinations.SettingsScreenDestination
import org.rhasspy.mobile.viewmodel.navigation.destinations.SettingsScreenDestination.*
import org.rhasspy.mobile.viewmodel.screens.settings.SettingsScreenUiEvent.Action
import org.rhasspy.mobile.viewmodel.screens.settings.SettingsScreenUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.screens.settings.SettingsScreenUiEvent.Navigate
import org.rhasspy.mobile.viewmodel.screens.settings.SettingsScreenUiEvent.Navigate.*

@Stable
class SettingsScreenViewModel(
    viewStateCreator: SettingsScreenViewStateCreator,
    private val navigator: Navigator
) : ViewModel() {

    val viewState: StateFlow<SettingsScreenViewState> = viewStateCreator()
    val screen = navigator.getBackStack(SettingsScreenDestination::class, OverviewScreen)

    fun onEvent(event: SettingsScreenUiEvent) {
        when (event) {
            is Action -> onAction(event)
            is Navigate -> onNavigate(event)
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            BackClick -> navigator.popBackStack()
        }
    }

    private fun onNavigate(navigate: Navigate) {
        navigator.set(
            type = SettingsScreenDestination::class,
            screen = when (navigate) {
                AboutClick -> AboutSettings
                AudioFocusClick -> AudioFocusSettings
                AudioRecorderSettingsClick -> AudioRecorderSettings
                AutomaticSilenceDetectionClick -> AutomaticSilenceDetectionSettings
                BackgroundServiceClick -> BackgroundServiceSettings
                DeviceClick -> DeviceSettings
                IndicationClick -> IndicationSettings
                LanguageClick -> LanguageSettingsScreen
                LogClick -> LogSettings
                MicrophoneOverlayClick -> MicrophoneOverlaySettings
                SaveAndRestoreClick -> SaveAndRestoreSettings
            }
        )
    }

}
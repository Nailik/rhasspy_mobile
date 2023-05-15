package org.rhasspy.mobile.viewmodel.screens.settings

import androidx.compose.runtime.Stable
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.viewmodel.navigation.Navigator
import org.rhasspy.mobile.viewmodel.navigation.Screen.SettingsScreen.*
import org.rhasspy.mobile.viewmodel.screens.settings.SettingsScreenUiEvent.Navigate
import org.rhasspy.mobile.viewmodel.screens.settings.SettingsScreenUiEvent.Navigate.*

@Stable
class SettingsScreenViewModel(
    viewStateCreator: SettingsScreenViewStateCreator,
    private val navigator: Navigator
) : ViewModel() {

    val viewState: StateFlow<SettingsScreenViewState> = viewStateCreator()

    fun onEvent(event: SettingsScreenUiEvent) {
        when (event) {
            is Navigate -> onNavigate(event)
        }
    }

    private fun onNavigate(navigate: Navigate) {
        navigator.navigate(
            when (navigate) {
                AboutClick -> AboutSettings
                AudioFocusClick -> AudioFocusSettings
                AudioRecorderSettingsClick -> AudioRecorderSettings
                AutomaticSilenceDetectionClick -> AutomaticSilenceDetectionSettings
                BackgroundServiceClick -> BackgroundServiceSettings
                DeviceClick -> DeviceSettings
                IndicationClick -> IndicationSettings.Overview
                LanguageClick -> LanguageSettingsScreen
                LogClick -> LogSettings
                MicrophoneOverlayClick -> MicrophoneOverlaySettings
                SaveAndRestoreClick -> SaveAndRestoreSettings
            }
        )
    }

}
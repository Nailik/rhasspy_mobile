package org.rhasspy.mobile.viewmodel.configuration.audioinput

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.viewmodel.configuration.audioinput.AudioInputConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.audioinput.AudioInputConfigurationUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.ConfigurationScreenNavigationDestination.SpeechToTextConfigurationScreen
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.ConfigurationScreenNavigationDestination.WakeWordConfigurationScreen
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.SpeechToTextConfigurationScreenDestination
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.WakeWordConfigurationScreenDestination
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel

@Stable
class AudioInputConfigurationViewModel(
    val viewStateCreator: AudioInputConfigurationViewStateCreator
) : ScreenViewModel() {

    val viewState = viewStateCreator()

    fun onEvent(event: AudioInputConfigurationUiEvent) {
        when (event) {
            is Action -> onAction(event)
        }
    }

    fun onAction(action: Action) {
        when (action) {
            BackClick                            -> navigator.onBackPressed()
            OpenWakeWordRecorderFormatScreen     -> navigator.navigate(WakeWordConfigurationScreenDestination.AudioRecorderFormatScreen)
            OpenWakeWordOutputFormatScreen       -> navigator.navigate(WakeWordConfigurationScreenDestination.AudioOutputFormatScreen)
            OpenTextToSpeechRecorderFormatScreen -> navigator.navigate(SpeechToTextConfigurationScreenDestination.AudioRecorderFormatScreen)
            OpenTextToSpeechOutputFormatScreen   -> navigator.navigate(SpeechToTextConfigurationScreenDestination.AudioOutputFormatScreen)
        }
    }

}
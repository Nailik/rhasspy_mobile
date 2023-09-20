package org.rhasspy.mobile.viewmodel.configuration.audioinput

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.audioinput.AudioInputConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.audioinput.AudioInputConfigurationUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.AudioInputDomainScreenDestination.AudioInputFormatScreen
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.AudioInputDomainScreenDestination.AudioOutputFormatScreen
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel

@Stable
class AudioInputConfigurationViewModel : ScreenViewModel() {

    val viewState = ConfigurationSetting.micDomainData.data.mapReadonlyState { AudioInputConfigurationViewState(it) }

    //TODO use audomatic gain control
    fun onEvent(event: AudioInputConfigurationUiEvent) {
        when (event) {
            is Action -> onAction(event)
        }
    }

    fun onAction(action: Action) {
        when (action) {
            BackClick                           -> navigator.onBackPressed()
            OpenInputFormatConfigurationScreen  -> navigator.navigate(AudioInputFormatScreen)
            OpenOutputFormatConfigurationScreen -> navigator.navigate(AudioOutputFormatScreen)
        }
    }

}
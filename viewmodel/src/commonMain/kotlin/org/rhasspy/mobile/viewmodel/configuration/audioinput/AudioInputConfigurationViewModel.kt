package org.rhasspy.mobile.viewmodel.configuration.audioinput

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.platformspecific.features.FeatureAvailability
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.audioinput.AudioInputConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.audioinput.AudioInputConfigurationUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.AudioInputDomainScreenDestination.AudioInputFormatScreen
import org.rhasspy.mobile.viewmodel.navigation.NavigationDestination.AudioInputDomainScreenDestination.AudioOutputFormatScreen
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel

@Stable
class AudioInputConfigurationViewModel(
    mapper: AudioInputConfigurationDataMapper,
) : ScreenViewModel() {

    val viewState = MutableStateFlow(
        AudioInputConfigurationViewState(
            editData = mapper(ConfigurationSetting.micDomainData.value),
            isUseAutomaticGainControlVisible = FeatureAvailability.isUseAutomaticGainControlEnabled
        )
    )

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
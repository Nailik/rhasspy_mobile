package org.rhasspy.mobile.viewmodel.screens.settings

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel
import org.rhasspy.mobile.viewmodel.navigation.destinations.SettingsScreenDestination
import org.rhasspy.mobile.viewmodel.screens.settings.SettingsScreenUiEvent.Action
import org.rhasspy.mobile.viewmodel.screens.settings.SettingsScreenUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.screens.settings.SettingsScreenUiEvent.Action.Navigate

@Stable
class SettingsScreenViewModel(
    viewStateCreator: SettingsScreenViewStateCreator
) : ScreenViewModel() {

    val viewState: StateFlow<SettingsScreenViewState> = viewStateCreator()
    val screen = navigator.topScreen<SettingsScreenDestination>()

    fun onEvent(event: SettingsScreenUiEvent) {
        when (event) {
            is Action -> onAction(event)
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            BackClick -> navigator.onBackPressed()
            is Navigate -> navigator.navigate(action.destination)
        }
    }

}
package org.rhasspy.mobile.viewmodel.screens.settings

import androidx.compose.runtime.Stable
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.viewmodel.navigation.Navigator
import org.rhasspy.mobile.viewmodel.navigation.destinations.SettingsScreenDestination
import org.rhasspy.mobile.viewmodel.screens.settings.SettingsScreenUiEvent.Action
import org.rhasspy.mobile.viewmodel.screens.settings.SettingsScreenUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.screens.settings.SettingsScreenUiEvent.Action.Navigate

@Stable
class SettingsScreenViewModel(
    viewStateCreator: SettingsScreenViewStateCreator,
    private val navigator: Navigator
) : ViewModel() {

    val viewState: StateFlow<SettingsScreenViewState> = viewStateCreator()
    val screen = navigator.topScreen<SettingsScreenDestination>()

    fun onEvent(event: SettingsScreenUiEvent) {
        when (event) {
            is Action -> onAction(event)
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            BackClick -> navigator.popBackStack()
            is Navigate -> navigator.navigate(action.destination)
        }
    }

}
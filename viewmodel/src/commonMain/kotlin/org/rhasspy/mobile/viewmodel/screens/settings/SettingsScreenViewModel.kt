package org.rhasspy.mobile.viewmodel.screens.settings

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.link.LinkType.WikiSettings
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.settings.SettingsScreenUiEvent.Action
import org.rhasspy.mobile.viewmodel.screens.settings.SettingsScreenUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.screens.settings.SettingsScreenUiEvent.Action.Navigate
import org.rhasspy.mobile.viewmodel.screens.settings.SettingsScreenUiEvent.Action.OpenWikiLink

@Stable
class SettingsScreenViewModel(
    viewStateCreator: SettingsScreenViewStateCreator
) : ScreenViewModel() {

    val viewState: StateFlow<SettingsScreenViewState> = viewStateCreator()

    fun onEvent(event: SettingsScreenUiEvent) {
        when (event) {
            is Action -> onAction(event)
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            BackClick -> navigator.onBackPressed()
            is Navigate -> navigator.navigate(action.destination)
            OpenWikiLink -> openLink(WikiSettings)
        }
    }

}
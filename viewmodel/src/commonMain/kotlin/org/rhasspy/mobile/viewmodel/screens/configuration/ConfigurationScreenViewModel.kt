package org.rhasspy.mobile.viewmodel.screens.configuration

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.event.EventState.Consumed
import org.rhasspy.mobile.data.event.EventState.Triggered
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.KViewModel
import org.rhasspy.mobile.viewmodel.navigation.destinations.ConfigurationScreenNavigationDestination
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiEvent.Action
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiEvent.Change
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiEvent.Change.SiteIdChange
import org.rhasspy.mobile.viewmodel.screens.configuration.IConfigurationScreenUiStateEvent.ScrollToErrorEventIState

@Stable
class ConfigurationScreenViewModel(
    private val viewStateCreator: ConfigurationScreenViewStateCreator
) : KViewModel() {

    val viewState: StateFlow<ConfigurationScreenViewState> = viewStateCreator()
    val screen = navigator.topScreen<ConfigurationScreenNavigationDestination>()

    fun onEvent(event: ConfigurationScreenUiEvent) {
        when (event) {
            is Change -> onChange(event)
            is Action -> onAction(event)
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
            BackClick -> navigator.onBackPressed()
            is Navigate -> navigator.navigate(action.destination)
        }
    }

    fun onConsumed(event: IConfigurationScreenUiStateEvent) {
        when (event) {
            is ScrollToErrorEventIState -> viewStateCreator.updateScrollToError(Consumed)
        }
    }

}
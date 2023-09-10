package org.rhasspy.mobile.viewmodel.screens.configuration

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.data.link.LinkType
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiEvent.Action
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiEvent.Change
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiEvent.Change.SiteIdChange

@Stable
class ConfigurationScreenViewModel(
    viewStateCreator: ConfigurationScreenViewStateCreator
) : ScreenViewModel() {

    private val _viewState: MutableStateFlow<ConfigurationScreenViewState> = viewStateCreator()
    val viewState = _viewState.readOnly

    fun onEvent(event: ConfigurationScreenUiEvent) {
        when (event) {
            is Change   -> onChange(event)
            is Action   -> onAction(event)
        }
    }

    private fun onChange(change: Change) {
        when (change) {
            is SiteIdChange -> ConfigurationSetting.siteId.value = change.text
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            BackClick          -> navigator.onBackPressed()
            is Navigate        -> navigator.navigate(action.destination)
            OpenWikiLink       -> openLink(LinkType.WikiConfiguration)
        }
    }

}
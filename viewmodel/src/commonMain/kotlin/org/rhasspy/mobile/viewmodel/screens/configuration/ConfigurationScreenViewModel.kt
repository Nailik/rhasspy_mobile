package org.rhasspy.mobile.viewmodel.screens.configuration

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.rhasspy.mobile.data.link.LinkType
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiEvent.*
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiEvent.Action.*
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiEvent.Change.SiteIdChange
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiEvent.Consumed.ScrollToError

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
            is Consumed -> onConsumed(event)
        }
    }

    private fun onChange(change: Change) {
        when (change) {
            is SiteIdChange -> ConfigurationSetting.siteId.value = change.text
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            ScrollToErrorClick -> _viewState.update { it.copy(scrollToError = viewState.value.firstErrorIndex.value) }
            BackClick          -> navigator.onBackPressed()
            is Navigate        -> navigator.navigate(action.destination)
            OpenWikiLink       -> openLink(LinkType.Wiki)
        }
    }

    private fun onConsumed(consumed: Consumed) {
        when (consumed) {
            ScrollToError -> _viewState.update { it.copy(scrollToError = null) }
        }
    }

}
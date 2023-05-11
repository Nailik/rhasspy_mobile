package org.rhasspy.mobile.viewmodel.screens.configuration

import androidx.compose.runtime.Stable
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.data.event.EventState.Consumed
import org.rhasspy.mobile.data.event.EventState.Triggered
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiEvent.Action
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiEvent.Action.ScrollToError
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiEvent.Change
import org.rhasspy.mobile.viewmodel.screens.configuration.ConfigurationScreenUiEvent.Change.SiteIdChange
import org.rhasspy.mobile.viewmodel.screens.configuration.IConfigurationScreenUiStateEvent.ScrollToErrorEventIState

@Stable
class ConfigurationScreenViewModel(
    private val viewStateCreator: ConfigurationScreenViewStateCreator
) : ViewModel() {

    val viewState: StateFlow<ConfigurationScreenViewState> = viewStateCreator()

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
        }
    }

    fun onConsumed(event: IConfigurationScreenUiStateEvent) {
        when (event) {
            is ScrollToErrorEventIState -> viewStateCreator.updateScrollToError(Consumed)
        }
    }

}
package org.rhasspy.mobile.viewmodel.configuration.connections

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.viewmodel.configuration.connections.ConnectionsConfigurationUiEvent.Action
import org.rhasspy.mobile.viewmodel.configuration.connections.ConnectionsConfigurationUiEvent.Action.BackClick
import org.rhasspy.mobile.viewmodel.configuration.connections.ConnectionsConfigurationUiEvent.Action.Navigate
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel

@Stable
class ConnectionsConfigurationViewModel(
    viewStateCreator: ConnectionsScreenViewStateCreator
) : ScreenViewModel() {

    val viewState = viewStateCreator()

    fun onEvent(event: ConnectionsConfigurationUiEvent) {
        when (event) {
            is Action -> onAction(event)
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            BackClick   -> navigator.onBackPressed()
            is Navigate -> navigator.navigate(action.destination)
        }
    }

}
package org.rhasspy.mobile.viewmodel.screens.home

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.logic.middleware.ServiceMiddleware
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.PlayStopRecording
import org.rhasspy.mobile.viewmodel.KViewModel
import org.rhasspy.mobile.viewmodel.screens.home.HomeScreenUiEvent.Action
import org.rhasspy.mobile.viewmodel.screens.home.HomeScreenUiEvent.Action.TogglePlayRecording

@Stable
class HomeScreenViewModel(
    private val serviceMiddleware: ServiceMiddleware,
    viewStateCreator: HomeScreenViewStateCreator
) : KViewModel() {

    val viewState: StateFlow<HomeScreenViewState> = viewStateCreator()

    fun onEvent(event: HomeScreenUiEvent) {
        when (event) {
            is Action -> onAction(event)
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            TogglePlayRecording -> serviceMiddleware.action(PlayStopRecording)
        }
    }

}
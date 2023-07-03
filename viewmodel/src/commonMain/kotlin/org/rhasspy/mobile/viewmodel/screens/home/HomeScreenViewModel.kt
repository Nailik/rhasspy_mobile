package org.rhasspy.mobile.viewmodel.screens.home

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.logic.middleware.IServiceMiddleware
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.PlayStopRecording
import org.rhasspy.mobile.viewmodel.screen.IScreenViewModel
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.home.HomeScreenUiEvent.Action
import org.rhasspy.mobile.viewmodel.screens.home.HomeScreenUiEvent.Action.MicrophoneFabClick
import org.rhasspy.mobile.viewmodel.screens.home.HomeScreenUiEvent.Action.TogglePlayRecording

interface IHomeScreeViewModel : IScreenViewModel

@Stable
class HomeScreenViewModel(
    private val serviceMiddleware: IServiceMiddleware,
    viewStateCreator: HomeScreenViewStateCreator
) : IHomeScreeViewModel, ScreenViewModel() {

    val viewState: StateFlow<HomeScreenViewState> = viewStateCreator()

    fun onEvent(event: HomeScreenUiEvent) {
        when (event) {
            is Action -> onAction(event)
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            TogglePlayRecording -> serviceMiddleware.action(PlayStopRecording)
            MicrophoneFabClick -> requireMicrophonePermission(serviceMiddleware::userSessionClick)
        }
    }

}
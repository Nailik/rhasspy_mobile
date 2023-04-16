package org.rhasspy.mobile.viewmodel.screens.home

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.rhasspy.mobile.logic.middleware.ServiceMiddleware
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.PlayStopRecording
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.viewmodel.screens.home.HomeScreenUiEvent.Action
import org.rhasspy.mobile.viewmodel.screens.home.HomeScreenUiEvent.Action.TogglePlayRecording

class HomeScreenViewModel(
    private val serviceMiddleware: ServiceMiddleware
) : ViewModel(), KoinComponent {

    private val _viewState =
        MutableStateFlow(HomeScreenViewState.getInitialViewState(serviceMiddleware))
    val viewState = _viewState.readOnly

    fun onEvent(event: HomeScreenUiEvent) {
        when(event) {
            is Action -> onAction(event)
        }
    }

    private fun onAction(action: Action) {
        when(action) {
            TogglePlayRecording -> serviceMiddleware.action(PlayStopRecording)
        }
    }

}
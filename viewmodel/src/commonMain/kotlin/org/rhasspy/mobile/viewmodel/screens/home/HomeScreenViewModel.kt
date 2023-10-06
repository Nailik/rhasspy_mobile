package org.rhasspy.mobile.viewmodel.screens.home

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.logic.connections.user.IUserConnection
import org.rhasspy.mobile.viewmodel.overlay.indication.IndicationOverlayViewModel
import org.rhasspy.mobile.viewmodel.screen.IScreenViewModel
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel
import org.rhasspy.mobile.viewmodel.screens.home.HomeScreenUiEvent.Action
import org.rhasspy.mobile.viewmodel.screens.home.HomeScreenUiEvent.Action.MicrophoneFabClick
import org.rhasspy.mobile.viewmodel.screens.home.HomeScreenUiEvent.Action.TogglePlayRecording

interface IHomeScreeViewModel : IScreenViewModel

@Stable
class HomeScreenViewModel(
    private val userConnection: IUserConnection,
    val indicationOverlayViewModel: IndicationOverlayViewModel,
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
            TogglePlayRecording -> userConnection.playRecordingAction()
            MicrophoneFabClick  -> requireMicrophonePermission(userConnection::sessionAction)
        }
    }

}
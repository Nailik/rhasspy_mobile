package org.rhasspy.mobile.viewmodel.microphone

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.logic.middleware.IServiceMiddleware
import org.rhasspy.mobile.viewmodel.microphone.MicrophoneFabUiEvent.Action
import org.rhasspy.mobile.viewmodel.microphone.MicrophoneFabUiEvent.Action.MicrophoneFabClick
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModel
import org.rhasspy.mobile.viewmodel.screen.ScreenViewModelUiEvent.Action.RequestMicrophonePermission

@Stable
class MicrophoneFabViewModel(
    private val serviceMiddleware: IServiceMiddleware,
    viewStateCreator: MicrophoneFabViewStateCreator
) : ScreenViewModel() {

    val viewState: StateFlow<MicrophoneFabViewState> = viewStateCreator()

    fun onEvent(event: MicrophoneFabUiEvent) {
        when (event) {
            is Action -> onAction(event)
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            MicrophoneFabClick -> {
                if (!microphonePermission.granted.value) {
                    onEvent(RequestMicrophonePermission)
                } else {
                    serviceMiddleware.userSessionClick()
                }
            }
        }
    }

}
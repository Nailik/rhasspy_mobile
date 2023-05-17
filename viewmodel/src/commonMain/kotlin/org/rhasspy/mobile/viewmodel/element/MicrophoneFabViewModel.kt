package org.rhasspy.mobile.viewmodel.element

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.logic.middleware.ServiceMiddleware
import org.rhasspy.mobile.viewmodel.KViewModel
import org.rhasspy.mobile.viewmodel.element.MicrophoneFabUiEvent.Action
import org.rhasspy.mobile.viewmodel.element.MicrophoneFabUiEvent.Action.UserSessionClick

@Stable
class MicrophoneFabViewModel(
    private val serviceMiddleware: ServiceMiddleware,
    viewStateCreator: MicrophoneFabViewStateCreator
) : KViewModel() {

    val viewState: StateFlow<MicrophoneFabViewState> = viewStateCreator()

    fun onEvent(event: MicrophoneFabUiEvent) {
        when (event) {
            is Action -> onAction(event)
        }
    }

    private fun onAction(action: Action) {
        when (action) {
            UserSessionClick -> serviceMiddleware.userSessionClick()
        }
    }

}
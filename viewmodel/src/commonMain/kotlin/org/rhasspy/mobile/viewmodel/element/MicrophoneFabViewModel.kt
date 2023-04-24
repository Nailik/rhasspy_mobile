package org.rhasspy.mobile.viewmodel.element

import androidx.compose.runtime.Stable
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.rhasspy.mobile.logic.middleware.ServiceMiddleware
import org.rhasspy.mobile.viewmodel.element.MicrophoneFabUiEvent.Action
import org.rhasspy.mobile.viewmodel.element.MicrophoneFabUiEvent.Action.UserSessionClick

@Stable
class MicrophoneFabViewModel(
    private val serviceMiddleware: ServiceMiddleware,
    viewStateCreator: MicrophoneFabViewStateCreator
) : ViewModel(), KoinComponent {

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
package org.rhasspy.mobile.viewmodel.assist

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.rhasspy.mobile.logic.connections.user.IUserConnection
import org.rhasspy.mobile.viewmodel.assist.AssistantUiEvent.Activate
import org.rhasspy.mobile.viewmodel.overlay.indication.IndicationOverlayViewModel

class AssistantViewModel(
    private val userConnection: IUserConnection,
    val indicationViewModel: IndicationOverlayViewModel
) : ViewModel() {

    fun awaitIdle(function: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            var isInitial = true
            userConnection.showVisualIndicationState.collect {
                if (!it) {
                    if (isInitial) {
                        isInitial = false
                    } else {
                        function()
                        cancel()
                    }
                }
            }
        }
    }

    fun onEvent(event: AssistantUiEvent) {
        when (event) {
            is Activate -> userConnection.sessionAction()
        }
    }


}
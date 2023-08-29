package org.rhasspy.mobile.viewmodel.assist

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.rhasspy.mobile.logic.local.indication.IIndicationService
import org.rhasspy.mobile.logic.middleware.IServiceMiddleware
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.StartListening
import org.rhasspy.mobile.logic.middleware.Source
import org.rhasspy.mobile.viewmodel.assist.AssistantUiEvent.Activate

class AssistantViewModel(
    private val serviceMiddleware: IServiceMiddleware,
    private val indicationService: IIndicationService
) : ViewModel() {

    fun awaitIdle(function: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            var isInitial = true
            indicationService.isShowVisualIndication.collect {
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
            is Activate -> serviceMiddleware.action(StartListening(Source.Local, false))
        }
    }


}
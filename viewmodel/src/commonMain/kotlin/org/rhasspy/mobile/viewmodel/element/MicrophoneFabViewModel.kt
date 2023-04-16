package org.rhasspy.mobile.viewmodel.element

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.rhasspy.mobile.data.service.option.WakeWordOption
import org.rhasspy.mobile.logic.middleware.ServiceMiddleware
import org.rhasspy.mobile.logic.services.dialog.DialogManagerService
import org.rhasspy.mobile.logic.services.dialog.DialogManagerServiceState
import org.rhasspy.mobile.logic.services.wakeword.WakeWordService
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.logic.update
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.permission.MicrophonePermission
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.viewmodel.element.MicrophoneFabUiEvent.Action
import org.rhasspy.mobile.viewmodel.element.MicrophoneFabUiEvent.Action.UserSessionClick
import org.rhasspy.mobile.viewmodel.element.MicrophoneFabViewState.Companion.isMicrophonePermissionRequired
import org.rhasspy.mobile.viewmodel.element.MicrophoneFabViewState.Companion.isRecording

class MicrophoneFabViewModel(
    private val dialogManagerService: DialogManagerService,
    private val serviceMiddleware: ServiceMiddleware,
    private val wakeWordService: WakeWordService
) : ViewModel(), KoinComponent {

    private val _viewState = MutableStateFlow(
        MicrophoneFabViewState.getInitialViewState(
            dialogManagerService = dialogManagerService,
            serviceMiddleware = serviceMiddleware,
            wakeWordService = wakeWordService
        )
    )
    val viewState = _viewState.readOnly

    init {
        viewModelScope.launch(Dispatchers.Default) {
            combineStateFlow(
                ConfigurationSetting.wakeWordOption.data,
                dialogManagerService.currentDialogState,
                serviceMiddleware.isUserActionEnabled,
                wakeWordService.isRecording,
                MicrophonePermission.granted,
            ) { arr -> arr }
                .collect { data ->
                    _viewState.update {
                        it.copy(
                            isMicrophonePermissionRequired = isMicrophonePermissionRequired(data[0] as WakeWordOption),
                            dialogManagerServiceState = data[1] as DialogManagerServiceState,
                            isUserActionEnabled = data[2] as Boolean,
                            isShowBorder = data[3] as Boolean,
                            isShowMicOn = data[4] as Boolean,
                            isRecording = isRecording(data[1] as DialogManagerServiceState)
                        )
                    }
                }
        }
    }

    fun onEvent(event: MicrophoneFabUiEvent) {
        when(event) {
            is Action -> onAction(event)
        }
    }

    private fun onAction(action: Action) {
        when(action) {
            UserSessionClick -> serviceMiddleware.userSessionClick()
        }
    }

}
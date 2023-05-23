package org.rhasspy.mobile.viewmodel.element

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.logic.middleware.ServiceMiddleware
import org.rhasspy.mobile.logic.services.dialog.DialogManagerService
import org.rhasspy.mobile.logic.services.wakeword.WakeWordService
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.permission.MicrophonePermission

class MicrophoneFabViewStateCreator(
    private val dialogManagerService: DialogManagerService,
    private val serviceMiddleware: ServiceMiddleware,
    private val wakeWordService: WakeWordService,
    private val microphonePermission: MicrophonePermission
) {
    private val updaterScope = CoroutineScope(Dispatchers.IO)

    operator fun invoke(): StateFlow<MicrophoneFabViewState> {
        val viewState = MutableStateFlow(getViewState())

        updaterScope.launch {
            combineStateFlow(
                dialogManagerService.currentDialogState,
                serviceMiddleware.isUserActionEnabled,
                wakeWordService.isRecording,
                microphonePermission.granted,
            ).collect {
                viewState.value = getViewState()
            }
        }

        return viewState
    }

    private fun getViewState(): MicrophoneFabViewState {
        return MicrophoneFabViewState(
            isMicrophonePermissionAllowed = microphonePermission.granted.value,
            dialogManagerServiceState = dialogManagerService.currentDialogState.value,
            isUserActionEnabled = serviceMiddleware.isUserActionEnabled.value,
            isShowBorder = wakeWordService.isRecording.value,
            isShowMicOn = microphonePermission.granted.value
        )
    }

}
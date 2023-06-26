package org.rhasspy.mobile.viewmodel.microphone

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.logic.middleware.ServiceMiddleware
import org.rhasspy.mobile.logic.services.dialog.DialogManagerService
import org.rhasspy.mobile.logic.services.wakeword.WakeWordService
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.platformspecific.permission.MicrophonePermission

class MicrophoneFabViewStateCreator(
    private val dialogManagerService: DialogManagerService,
    private val serviceMiddleware: ServiceMiddleware,
    private val wakeWordService: WakeWordService,
    private val microphonePermission: MicrophonePermission
) {

    operator fun invoke(): StateFlow<MicrophoneFabViewState> {

        return combineStateFlow(
            dialogManagerService.currentDialogState,
            serviceMiddleware.isUserActionEnabled,
            wakeWordService.isRecording,
            microphonePermission.granted,
        ).mapReadonlyState {
            getViewState()
        }

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
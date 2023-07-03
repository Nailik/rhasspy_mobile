package org.rhasspy.mobile.viewmodel.microphone

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.logic.middleware.IServiceMiddleware
import org.rhasspy.mobile.logic.services.dialog.IDialogManagerService
import org.rhasspy.mobile.logic.services.wakeword.IWakeWordService
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.platformspecific.permission.IMicrophonePermission

class MicrophoneFabViewStateCreator(
    private val dialogManagerService: IDialogManagerService,
    private val serviceMiddleware: IServiceMiddleware,
    private val wakeWordService: IWakeWordService,
    private val microphonePermission: IMicrophonePermission
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
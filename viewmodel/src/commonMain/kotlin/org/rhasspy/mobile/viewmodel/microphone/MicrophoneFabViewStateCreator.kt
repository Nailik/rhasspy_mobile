package org.rhasspy.mobile.viewmodel.microphone

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.logic.domains.asr.IAsrDomain
import org.rhasspy.mobile.logic.domains.wake.IWakeDomain
import org.rhasspy.mobile.logic.middleware.IServiceMiddleware
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.platformspecific.permission.IMicrophonePermission

class MicrophoneFabViewStateCreator(
    private val serviceMiddleware: IServiceMiddleware,
    private val wakeWordService: IWakeDomain,
    private val microphonePermission: IMicrophonePermission,
    private val speechToTextService: IAsrDomain
) {

    operator fun invoke(): StateFlow<MicrophoneFabViewState> {

        return combineStateFlow(
            //TODO speechToTextService.isRecording,
            serviceMiddleware.isUserActionEnabled,
            //TODO wakeWordService.isRecording,
            microphonePermission.granted,
        ).mapReadonlyState {
            getViewState()
        }

    }

    private fun getViewState(): MicrophoneFabViewState {
        return MicrophoneFabViewState(
            isMicrophonePermissionAllowed = microphonePermission.granted.value,
            isUserActionEnabled = serviceMiddleware.isUserActionEnabled.value,
            isShowBorder = false,//TODO wakeWordService.isRecording.value,
            isRecording = false,//TODO  speechToTextService.isRecording.value
        )
    }

}
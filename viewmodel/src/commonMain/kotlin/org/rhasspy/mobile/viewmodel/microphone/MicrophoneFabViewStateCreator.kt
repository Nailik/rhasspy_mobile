package org.rhasspy.mobile.viewmodel.microphone

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.logic.domains.speechtotext.ISpeechToTextService
import org.rhasspy.mobile.logic.domains.wakeword.IWakeWordService
import org.rhasspy.mobile.logic.middleware.IServiceMiddleware
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.platformspecific.permission.IMicrophonePermission

class MicrophoneFabViewStateCreator(
    private val serviceMiddleware: IServiceMiddleware,
    private val wakeWordService: IWakeWordService,
    private val microphonePermission: IMicrophonePermission,
    private val speechToTextService: ISpeechToTextService
) {

    operator fun invoke(): StateFlow<MicrophoneFabViewState> {

        return combineStateFlow(
            speechToTextService.isRecording,
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
            isUserActionEnabled = serviceMiddleware.isUserActionEnabled.value,
            isShowBorder = wakeWordService.isRecording.value,
            isRecording = speechToTextService.isRecording.value
        )
    }

}
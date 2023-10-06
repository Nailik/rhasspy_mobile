package org.rhasspy.mobile.viewmodel.microphone

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.logic.connections.user.IUserConnection
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.platformspecific.permission.IMicrophonePermission

class MicrophoneFabViewStateCreator(
    private val microphonePermission: IMicrophonePermission,
    private val userConnection: IUserConnection,
) {

    operator fun invoke(): StateFlow<MicrophoneFabViewState> {

        return combineStateFlow(
            userConnection.micDomainRecordingState,
            userConnection.asrDomainRecordingState,
            microphonePermission.granted,
        ).mapReadonlyState {
            getViewState()
        }

    }

    private fun getViewState(): MicrophoneFabViewState {
        return MicrophoneFabViewState(
            isMicrophonePermissionAllowed = microphonePermission.granted.value,
            isShowBorder = userConnection.micDomainRecordingState.value,
            isRecording = userConnection.asrDomainRecordingState.value,
        )
    }

}
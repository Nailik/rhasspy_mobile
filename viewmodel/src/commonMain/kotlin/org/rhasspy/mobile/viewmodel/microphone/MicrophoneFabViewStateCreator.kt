package org.rhasspy.mobile.viewmodel.microphone

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.logic.connections.user.IUserConnection
import org.rhasspy.mobile.logic.middleware.IServiceMiddleware
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.platformspecific.permission.IMicrophonePermission

class MicrophoneFabViewStateCreator(
    private val serviceMiddleware: IServiceMiddleware,
    private val microphonePermission: IMicrophonePermission,
    private val userConnection: IUserConnection,
) {

    operator fun invoke(): StateFlow<MicrophoneFabViewState> {

        return combineStateFlow(
            userConnection.micDomainRecordingState,
            serviceMiddleware.isUserActionEnabled,
            userConnection.asrDomainRecordingState,
            microphonePermission.granted,
        ).mapReadonlyState {
            getViewState()
        }

    }

    private fun getViewState(): MicrophoneFabViewState {
        return MicrophoneFabViewState(
            isMicrophonePermissionAllowed = microphonePermission.granted.value,
            isUserActionEnabled = serviceMiddleware.isUserActionEnabled.value,
            isShowBorder = userConnection.micDomainRecordingState.value,
            isRecording = userConnection.asrDomainRecordingState.value,
        )
    }

}
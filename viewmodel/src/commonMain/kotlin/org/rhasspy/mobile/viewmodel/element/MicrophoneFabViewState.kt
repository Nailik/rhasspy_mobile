package org.rhasspy.mobile.viewmodel.element

import org.koin.core.component.KoinComponent
import org.rhasspy.mobile.data.service.option.WakeWordOption
import org.rhasspy.mobile.logic.middleware.ServiceMiddleware
import org.rhasspy.mobile.logic.services.dialog.DialogManagerService
import org.rhasspy.mobile.logic.services.dialog.DialogManagerServiceState
import org.rhasspy.mobile.logic.services.wakeword.WakeWordService
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.platformspecific.permission.MicrophonePermission

data class MicrophoneFabViewState(
    val isMicrophonePermissionRequired: Boolean,
    val dialogManagerServiceState: DialogManagerServiceState,
    val isUserActionEnabled: Boolean,
    val isShowBorder: Boolean,
    val isShowMicOn: Boolean,
    val isRecording: Boolean
) {

    companion object : KoinComponent {

        fun getInitialViewState(
            dialogManagerService: DialogManagerService,
            serviceMiddleware: ServiceMiddleware,
            wakeWordService: WakeWordService
        ) : MicrophoneFabViewState {
            return MicrophoneFabViewState(
                isMicrophonePermissionRequired = isMicrophonePermissionRequired(ConfigurationSetting.wakeWordOption.value),
                dialogManagerServiceState = dialogManagerService.currentDialogState.value,
                isUserActionEnabled = serviceMiddleware.isUserActionEnabled.value,
                isShowBorder = wakeWordService.isRecording.value,
                isShowMicOn = MicrophonePermission.granted.value,
                isRecording = isRecording(dialogManagerService.currentDialogState.value)
            )
        }

        fun isMicrophonePermissionRequired(wakeWordOption: WakeWordOption): Boolean {
            return wakeWordOption == WakeWordOption.Porcupine || wakeWordOption == WakeWordOption.Udp
        }

        fun isRecording(dialogManagerServiceState: DialogManagerServiceState): Boolean {
            return dialogManagerServiceState == DialogManagerServiceState.RecordingIntent
        }

    }


}
package org.rhasspy.mobile.viewmodel.element

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.logic.middleware.ServiceMiddleware
import org.rhasspy.mobile.logic.services.dialog.DialogManagerService
import org.rhasspy.mobile.logic.services.wakeword.WakeWordService
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.permission.MicrophonePermission

class MicrophoneFabViewStateCreator(
    private val dialogManagerService: DialogManagerService,
    private val serviceMiddleware: ServiceMiddleware,
    private val wakeWordService: WakeWordService
) {
    private val updaterScope = CoroutineScope(Dispatchers.Default)

    operator fun invoke(): StateFlow<MicrophoneFabViewState> {
        val viewState = MutableStateFlow(getViewState())

        updaterScope.launch {
            combineStateFlow(
                ConfigurationSetting.wakeWordOption.data,
                dialogManagerService.currentDialogState,
                serviceMiddleware.isUserActionEnabled,
                wakeWordService.isRecording,
                MicrophonePermission.granted,
            ).collect {
                viewState.value = getViewState()
            }
        }

        return viewState
    }

    private fun getViewState(): MicrophoneFabViewState {
        return MicrophoneFabViewState(
            wakeWordOption = ConfigurationSetting.wakeWordOption.value,
            dialogManagerServiceState = dialogManagerService.currentDialogState.value,
            isUserActionEnabled = serviceMiddleware.isUserActionEnabled.value,
            isShowBorder = wakeWordService.isRecording.value,
            isShowMicOn = MicrophonePermission.granted.value
        )
    }

}
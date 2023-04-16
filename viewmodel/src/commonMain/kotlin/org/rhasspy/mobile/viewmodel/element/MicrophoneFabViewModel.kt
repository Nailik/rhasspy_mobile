package org.rhasspy.mobile.viewmodel.element

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.logic.getSafe
import org.rhasspy.mobile.logic.middleware.ServiceMiddleware
import org.rhasspy.mobile.logic.services.dialog.DialogManagerService
import org.rhasspy.mobile.logic.services.dialog.DialogManagerServiceState
import org.rhasspy.mobile.logic.services.wakeword.WakeWordService
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.combineState
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.platformspecific.permission.MicrophonePermission
import org.rhasspy.mobile.platformspecific.readOnly

class MicrophoneFabViewModel : ViewModel(), KoinComponent {

    private val _viewState = MutableStateFlow(MicrophoneFabViewState())
    val viewState = _viewState.readOnly

    private val dialogManagerServiceState
        get() = getSafe<DialogManagerService>()?.currentDialogState ?: MutableStateFlow(
            DialogManagerServiceState.Idle
        ).readOnly

    val isUserActionEnabled
        get() = getSafe<ServiceMiddleware>()?.isUserActionEnabled ?: MutableStateFlow(false).readOnly

    val isShowBorder
        get() = getSafe<WakeWordService>()?.isRecording ?: MutableStateFlow(false)
    val isShowMicOn: StateFlow<Boolean> = MicrophonePermission.granted
    val isRecording get() = dialogManagerServiceState.mapReadonlyState { it == DialogManagerServiceState.RecordingIntent }

    init {
        viewModelScope.launch {
            combineState(isShowBorder, isShowMicOn, isRecording, isUserActionEnabled) { _, _, _, _ ->
                listOf(isShowBorder, isShowMicOn, isRecording, isUserActionEnabled)
            }.collect {
                viewModelScope.launch {
                    get<NativeApplication>().updateWidgetNative()
                }
            }
        }
    }

    fun onClick() {
        getSafe<ServiceMiddleware>()?.userSessionClick()
    }

}
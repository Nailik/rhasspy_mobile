package org.rhasspy.mobile.viewmodel.overlay

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.rhasspy.mobile.*
import org.rhasspy.mobile.settings.option.MicrophoneOverlaySizeOption
import org.rhasspy.mobile.middleware.IServiceMiddleware
import org.rhasspy.mobile.nativeutils.MicrophonePermission
import org.rhasspy.mobile.nativeutils.OverlayPermission
import org.rhasspy.mobile.services.dialog.DialogManagerService
import org.rhasspy.mobile.services.dialog.DialogManagerServiceState
import org.rhasspy.mobile.settings.AppSetting
import kotlin.math.roundToInt

class MicrophoneOverlayViewModel : ViewModel(), KoinComponent {

    private val dialogManagerServiceState
        get() = getSafe<DialogManagerService>()?.currentDialogState ?: MutableStateFlow(DialogManagerServiceState.Idle).readOnly

    val shouldOverlayBeShown = combineState(
        OverlayPermission.granted,
        AppSetting.microphoneOverlaySizeOption.data,
        Application.Instance.isAppInBackground,
        AppSetting.isMicrophoneOverlayWhileAppEnabled.data
    ) { permissionGranted, microphoneOverlaySizeOption, isAppInBackground, isMicrophoneOverlayWhileApp ->
        permissionGranted && microphoneOverlaySizeOption != MicrophoneOverlaySizeOption.Disabled && (isAppInBackground || isMicrophoneOverlayWhileApp)
    }

    val microphoneOverlayPositionX: Int get() = AppSetting.microphoneOverlayPositionX.value
    val microphoneOverlayPositionY: Int get() = AppSetting.microphoneOverlayPositionY.value

    val microphoneOverlaySize = AppSetting.microphoneOverlaySizeOption.data.mapReadonlyState { it.size }

    val isActionEnabled = dialogManagerServiceState.mapReadonlyState { it == DialogManagerServiceState.Idle || it == DialogManagerServiceState.AwaitingWakeWord }
    val isShowBorder = dialogManagerServiceState.mapReadonlyState { it == DialogManagerServiceState.AwaitingWakeWord }
    val isShowMicOn: StateFlow<Boolean> = MicrophonePermission.granted
    val isRecording = dialogManagerServiceState.mapReadonlyState { it == DialogManagerServiceState.RecordingIntent }

    fun updateMicrophoneOverlayPosition(offsetX: Float, offsetY: Float) {
        AppSetting.microphoneOverlayPositionX.value = (((AppSetting.microphoneOverlayPositionX.value + offsetX).roundToInt()))
        AppSetting.microphoneOverlayPositionY.value = (((AppSetting.microphoneOverlayPositionY.value + offsetY).roundToInt()))
    }

    fun onClick() {
        getSafe<IServiceMiddleware>()?.userSessionClick()
    }

}
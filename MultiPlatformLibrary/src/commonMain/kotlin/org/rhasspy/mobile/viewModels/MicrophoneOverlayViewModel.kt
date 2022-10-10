package org.rhasspy.mobile.viewModels

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.Application
import org.rhasspy.mobile.combineState
import org.rhasspy.mobile.nativeutils.OverlayPermission
import org.rhasspy.mobile.settings.AppSettings
import kotlin.math.roundToInt

class MicrophoneOverlayViewModel : ViewModel() {

    val shouldOverlayBeShown: StateFlow<Boolean>
        get() = combineState(
            OverlayPermission.granted,
            AppSettings.isMicrophoneOverlayEnabled.data,
            Application.Instance.isAppInBackground,
            AppSettings.isMicrophoneOverlayWhileApp.data
        ) { permissionGranted, isMicrophoneOverlayEnabled, isAppInBackground, isMicrophoneOverlayWhileApp ->
            permissionGranted && isMicrophoneOverlayEnabled && (isAppInBackground || isMicrophoneOverlayWhileApp)
        }

    val microphoneOverlayPositionX: Int get() = AppSettings.microphoneOverlayPositionX.value
    val microphoneOverlayPositionY: Int get() = AppSettings.microphoneOverlayPositionY.value

    fun updateMicrophoneOverlayPosition(offsetX: Float, offsetY: Float) {
        AppSettings.microphoneOverlayPositionX.data.value = (((AppSettings.microphoneOverlayPositionX.value + offsetX).roundToInt()))
        AppSettings.microphoneOverlayPositionY.data.value = (((AppSettings.microphoneOverlayPositionX.value + offsetY).roundToInt()))
    }

}
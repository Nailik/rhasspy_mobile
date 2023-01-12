package org.rhasspy.mobile.viewmodel.overlay

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.koin.core.component.KoinComponent
import org.rhasspy.mobile.Application
import org.rhasspy.mobile.combineState
import org.rhasspy.mobile.mapReadonlyState
import org.rhasspy.mobile.nativeutils.OverlayPermission
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.settings.option.MicrophoneOverlaySizeOption
import kotlin.math.roundToInt

class MicrophoneOverlayViewModel : ViewModel(), KoinComponent {

    val shouldOverlayBeShown = combineState(
        OverlayPermission.granted,
        AppSetting.microphoneOverlaySizeOption.data,
        Application.nativeInstance.isAppInBackground,
        AppSetting.isMicrophoneOverlayWhileAppEnabled.data
    ) { permissionGranted, microphoneOverlaySizeOption, isAppInBackground, isMicrophoneOverlayWhileApp ->
        permissionGranted && microphoneOverlaySizeOption != MicrophoneOverlaySizeOption.Disabled && (isAppInBackground || isMicrophoneOverlayWhileApp)
    }

    val microphoneOverlayPositionX: Int get() = AppSetting.microphoneOverlayPositionX.value
    val microphoneOverlayPositionY: Int get() = AppSetting.microphoneOverlayPositionY.value

    val microphoneOverlaySize =
        AppSetting.microphoneOverlaySizeOption.data.mapReadonlyState { it.size }

    fun updateMicrophoneOverlayPosition(offsetX: Float, offsetY: Float) {
        AppSetting.microphoneOverlayPositionX.value =
            (((AppSetting.microphoneOverlayPositionX.value + offsetX).roundToInt()))
        AppSetting.microphoneOverlayPositionY.value =
            (((AppSetting.microphoneOverlayPositionY.value + offsetY).roundToInt()))
    }

}
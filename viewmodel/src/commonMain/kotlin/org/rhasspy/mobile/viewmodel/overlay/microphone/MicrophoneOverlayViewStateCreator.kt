package org.rhasspy.mobile.viewmodel.overlay.microphone

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.permission.OverlayPermission
import org.rhasspy.mobile.settings.AppSetting
import org.rhasspy.mobile.viewmodel.element.MicrophoneFabViewStateCreator

class MicrophoneOverlayViewStateCreator(
    private val nativeApplication: NativeApplication,
    private val overlayPermission: OverlayPermission,
    microphoneFabViewStateCreator: MicrophoneFabViewStateCreator
) {
    private val updaterScope = CoroutineScope(Dispatchers.IO)
    private val microphoneFabViewStateFlow = microphoneFabViewStateCreator()

    operator fun invoke(): StateFlow<MicrophoneOverlayViewState> {
        val viewState = MutableStateFlow(getViewState())
        updaterScope.launch {
            combineStateFlow(
                overlayPermission.granted,
                nativeApplication.isAppInBackground,
                AppSetting.isMicrophoneOverlayWhileAppEnabled.data,
                AppSetting.microphoneOverlaySizeOption.data,
                AppSetting.microphoneOverlayPositionX.data,
                AppSetting.microphoneOverlayPositionY.data,
                microphoneFabViewStateFlow
            ).collect {
                viewState.value = getViewState()
            }
        }
        return viewState
    }

    private fun getViewState(): MicrophoneOverlayViewState {
        return MicrophoneOverlayViewState(
            isOverlayPermissionGranted = overlayPermission.granted.value,
            isAppInBackground = nativeApplication.isAppInBackground.value,
            isMicrophoneOverlayWhileAppEnabled = AppSetting.isMicrophoneOverlayWhileAppEnabled.value,
            microphoneOverlaySizeOption = AppSetting.microphoneOverlaySizeOption.value,
            microphoneOverlayPositionX = AppSetting.microphoneOverlayPositionX.value,
            microphoneOverlayPositionY = AppSetting.microphoneOverlayPositionY.value,
            microphoneFabViewState = microphoneFabViewStateFlow.value
        )
    }

}
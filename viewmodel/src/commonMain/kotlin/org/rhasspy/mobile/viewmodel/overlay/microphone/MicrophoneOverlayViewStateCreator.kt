package org.rhasspy.mobile.viewmodel.overlay.microphone

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.rhasspy.mobile.logic.settings.AppSetting
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.permission.OverlayPermission

class MicrophoneOverlayViewStateCreator(
    private val nativeApplication: NativeApplication
) {
    private val updaterScope = CoroutineScope(Dispatchers.Default)

    operator fun invoke(): StateFlow<MicrophoneOverlayViewState> {
        val viewState = MutableStateFlow(getViewState())
        updaterScope.launch {
            combineStateFlow(
                OverlayPermission.granted,
                nativeApplication.isAppInBackground,
                AppSetting.isMicrophoneOverlayWhileAppEnabled.data,
                AppSetting.microphoneOverlaySizeOption.data,
                AppSetting.microphoneOverlayPositionX.data,
                AppSetting.microphoneOverlayPositionY.data
            ).onEach {
                viewState.value = getViewState()
            }
        }
        return viewState
    }

    private fun getViewState(): MicrophoneOverlayViewState {
        return MicrophoneOverlayViewState(
            isOverlayPermissionGranted = OverlayPermission.granted.value,
            isAppInBackground = nativeApplication.isAppInBackground.value,
            isMicrophoneOverlayWhileAppEnabled = AppSetting.isMicrophoneOverlayWhileAppEnabled.value,
            microphoneOverlaySizeOption = AppSetting.microphoneOverlaySizeOption.value,
            microphoneOverlayPositionX = AppSetting.microphoneOverlayPositionX.value,
            microphoneOverlayPositionY = AppSetting.microphoneOverlayPositionY.value
        )
    }

}
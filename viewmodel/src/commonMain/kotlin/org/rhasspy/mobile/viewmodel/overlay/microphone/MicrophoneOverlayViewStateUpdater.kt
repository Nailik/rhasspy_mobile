package org.rhasspy.mobile.viewmodel.overlay.microphone

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.logic.settings.AppSetting
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.permission.OverlayPermission

class MicrophoneOverlayViewStateUpdater(
    private val _viewState: MutableStateFlow<MicrophoneOverlayViewState>,
    private val nativeApplication: NativeApplication
) {
    private val updaterScope = CoroutineScope(Dispatchers.Default)

    init {
        updaterScope.launch {
            combineStateFlow(
                OverlayPermission.granted,
                AppSetting.microphoneOverlaySizeOption.data,
                nativeApplication.isAppInBackground,
                AppSetting.isMicrophoneOverlayWhileAppEnabled.data
            ) .collect {
                _viewState.value = MicrophoneOverlayViewState(nativeApplication)
            }
        }
    }

}
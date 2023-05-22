package org.rhasspy.mobile.viewmodel.overlay.microphone

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.service.option.MicrophoneOverlaySizeOption
import org.rhasspy.mobile.viewmodel.element.MicrophoneFabViewState

@Stable
data class MicrophoneOverlayViewState internal constructor(
    val shouldOverlayBeShown: Boolean,
    val microphoneOverlayPositionX: Int,
    val microphoneOverlayPositionY: Int,
    val microphoneOverlaySize: Int,
    val microphoneFabViewState: MicrophoneFabViewState
) {

    constructor(
        isOverlayPermissionGranted: Boolean,
        isAppInBackground: Boolean,
        isMicrophoneOverlayWhileAppEnabled: Boolean,
        microphoneOverlaySizeOption: MicrophoneOverlaySizeOption,
        microphoneOverlayPositionX: Int,
        microphoneOverlayPositionY: Int,
        microphoneFabViewState: MicrophoneFabViewState
    ) : this(
        shouldOverlayBeShown = isOverlayPermissionGranted &&
                microphoneOverlaySizeOption != MicrophoneOverlaySizeOption.Disabled &&
                (isAppInBackground || isMicrophoneOverlayWhileAppEnabled),
        microphoneOverlayPositionX = microphoneOverlayPositionX,
        microphoneOverlayPositionY = microphoneOverlayPositionY,
        microphoneOverlaySize = microphoneOverlaySizeOption.size,
        microphoneFabViewState = microphoneFabViewState
    )

}
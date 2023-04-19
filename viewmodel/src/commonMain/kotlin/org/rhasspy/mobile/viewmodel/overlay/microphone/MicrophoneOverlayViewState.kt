package org.rhasspy.mobile.viewmodel.overlay.microphone

import androidx.compose.runtime.Stable
import org.rhasspy.mobile.data.service.option.MicrophoneOverlaySizeOption

@Stable
data class MicrophoneOverlayViewState internal constructor(
    val shouldOverlayBeShown: Boolean,
    val microphoneOverlayPositionX: Int,
    val microphoneOverlayPositionY: Int,
    val microphoneOverlaySize: Int
) {

    constructor(
        isOverlayPermissionGranted: Boolean,
        isAppInBackground: Boolean,
        isMicrophoneOverlayWhileAppEnabled: Boolean,
        microphoneOverlaySizeOption: MicrophoneOverlaySizeOption,
        microphoneOverlayPositionX: Int,
        microphoneOverlayPositionY: Int
    ) : this(
        shouldOverlayBeShown = isOverlayPermissionGranted &&
                microphoneOverlaySizeOption != MicrophoneOverlaySizeOption.Disabled &&
                (!isAppInBackground || isMicrophoneOverlayWhileAppEnabled),
        microphoneOverlayPositionX = microphoneOverlayPositionX,
        microphoneOverlayPositionY = microphoneOverlayPositionY,
        microphoneOverlaySize = microphoneOverlaySizeOption.size
    )

}
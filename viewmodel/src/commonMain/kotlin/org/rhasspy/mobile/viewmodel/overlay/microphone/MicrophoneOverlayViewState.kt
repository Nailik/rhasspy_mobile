package org.rhasspy.mobile.viewmodel.overlay.microphone

import org.rhasspy.mobile.data.service.option.MicrophoneOverlaySizeOption
import org.rhasspy.mobile.logic.settings.AppSetting
import org.rhasspy.mobile.platformspecific.application.NativeApplication
import org.rhasspy.mobile.platformspecific.permission.OverlayPermission

data class MicrophoneOverlayViewState internal constructor(
    val shouldOverlayBeShown: Boolean,
    val microphoneOverlayPositionX: Int,
    val microphoneOverlayPositionY: Int,
    val microphoneOverlaySize: Int
) {

    constructor(nativeApplication: NativeApplication) : this (
        shouldOverlayBeShown = OverlayPermission.granted.value &&
                AppSetting.microphoneOverlaySizeOption.value != MicrophoneOverlaySizeOption.Disabled &&
                (nativeApplication.isAppInBackground.value && AppSetting.isMicrophoneOverlayWhileAppEnabled.value),
        microphoneOverlayPositionX = AppSetting.microphoneOverlayPositionX.value,
        microphoneOverlayPositionY = AppSetting.microphoneOverlayPositionY.value,
        microphoneOverlaySize = AppSetting.microphoneOverlaySizeOption.value.size
    )

}
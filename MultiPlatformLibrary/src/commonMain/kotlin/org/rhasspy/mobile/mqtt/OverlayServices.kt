package org.rhasspy.mobile.mqtt

import co.touchlab.kermit.Logger
import org.rhasspy.mobile.settings.option.MicrophoneOverlaySizeOption
import org.rhasspy.mobile.nativeutils.OverlayPermission
import org.rhasspy.mobile.settings.AppSetting

object OverlayServices {
    private val logger = Logger.withTag("OverlayServices")

    fun checkPermission() {
        logger.v { "check Permissions" }
        if (!OverlayPermission.isGranted()) {
            logger.v { "reset overlay settings because permission is missing" }
            //reset services that need the permission
            AppSetting.microphoneOverlaySizeOption.value = MicrophoneOverlaySizeOption.Disabled
            AppSetting.isWakeWordLightIndicationEnabled.value = false
        }
    }

}
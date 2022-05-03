package org.rhasspy.mobile.mqtt

import co.touchlab.kermit.Logger
import org.rhasspy.mobile.nativeutils.OverlayPermission
import org.rhasspy.mobile.settings.AppSettings

object OverlayServices {
    private val logger = Logger.withTag("OverlayServices")

    fun checkPermission() {
        logger.v { "check Permissions" }
        if (!OverlayPermission.isGranted()) {
            logger.v { "reset overlay settings because permission is missing" }
            //reset services that need the permission
            AppSettings.isMicrophoneOverlayEnabled.data = false
            AppSettings.isWakeWordLightIndication.data = false
        }
    }

}
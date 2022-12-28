package org.rhasspy.mobile.services.settings

import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.settings.AppSettings

/**
 * handles changes of app settings that are called remotely
 */
class AppSettingsService : IService() {

    override fun onClose() {

    }

    fun hotWordToggle(value: Boolean) {
        AppSettings.isHotWordEnabled.value = value
    }

    fun intentHandlingToggle(value: Boolean) {
        AppSettings.isIntentHandlingEnabled.value = value
    }

    fun audioOutputToggle(value: Boolean) {
        AppSettings.isAudioOutputEnabled.value = value
    }

    fun setAudioVolume(volume: Float) {
        AppSettings.volume.value = volume
    }

}
package org.rhasspy.mobile.services.settings

import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.settings.AppSetting

//TODO logging
/**
 * handles changes of app settings that are called remotely
 */
class AppSettingsService : IService() {

    override fun onClose() {

    }

    fun hotWordToggle(value: Boolean) {
        AppSetting.isHotWordEnabled.value = value
    }

    fun intentHandlingToggle(value: Boolean) {
        AppSetting.isIntentHandlingEnabled.value = value
    }

    fun audioOutputToggle(value: Boolean) {
        AppSetting.isAudioOutputEnabled.value = value
    }

    fun setAudioVolume(volume: Float) {
        AppSetting.volume.value = volume
    }

}
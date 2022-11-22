package org.rhasspy.mobile.services.settings

import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.settings.AppSettings

/**
 * handles changes of app settings that are called remotely
 */
class AppSettingsService : IService() {

    override fun onClose() {
//        TODO("Not yet implemented")
    }

    fun hotWordToggleOnMqtt() {
        AppSettings.isHotWordEnabled.value = true
    }

    fun hotWordToggleOffMqtt() {
        AppSettings.isHotWordEnabled.value = false
    }

    fun intentHandlingToggleOnMqtt() {
        AppSettings.isIntentHandlingEnabled.value = true
    }

    fun intentHandlingToggleOffMqtt() {
        AppSettings.isIntentHandlingEnabled.value = false
    }

    fun audioOutputToggleOnMqtt() {
        AppSettings.isAudioOutputEnabled.value = true
    }

    fun audioOutputToggleOffMqtt() {
        AppSettings.isAudioOutputEnabled.value = false
    }

    fun setAudioVolumeMqtt(volume: Float) {
        AppSettings.volume.value = volume
    }

    fun toggleListenForWakeWebServer(value: Boolean) {
        AppSettings.isHotWordEnabled.value = value
    }

    fun setVolumeWebServer(volume: Float) {
        AppSettings.volume.value = volume
    }

}
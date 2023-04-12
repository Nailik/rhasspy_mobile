package org.rhasspy.mobile.logic.services.settings

import org.rhasspy.mobile.logic.logger.LogType
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.logic.settings.AppSetting

/**
 * handles changes of app settings that are called remotely
 */
class AppSettingsService : IService(LogType.AppSettingsService) {
    fun hotWordToggle(value: Boolean) {
        logger.d { "hotWordToggle value: $value" }
        AppSetting.isHotWordEnabled.value = value
    }

    fun intentHandlingToggle(value: Boolean) {
        logger.d { "intentHandlingToggle value: $value" }
        AppSetting.isIntentHandlingEnabled.value = value
    }

    fun audioOutputToggle(value: Boolean) {
        logger.d { "audioOutputToggle value: $value" }
        AppSetting.isAudioOutputEnabled.value = value
    }

    fun setAudioVolume(volume: Float) {
        logger.d { "setAudioVolume volume: $volume" }
        AppSetting.volume.value = volume
    }

}
package org.rhasspy.mobile.logic.services.settings

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.log.LogType
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.logic.services.IService
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.AppSetting

interface IAppSettingsService : IService {

    override val serviceState: StateFlow<ServiceState>

    fun hotWordToggle(value: Boolean)
    fun intentHandlingToggle(value: Boolean)
    fun audioOutputToggle(value: Boolean)
    fun setAudioVolume(volume: Float)

}

/**
 * handles changes of app settings that are called remotely
 */
internal class AppSettingsService : IAppSettingsService {

    override val logger = LogType.AppSettingsService.logger()

    private val _serviceState = MutableStateFlow<ServiceState>(ServiceState.Success)
    override val serviceState = _serviceState.readOnly

    override fun hotWordToggle(value: Boolean) {
        logger.d { "hotWordToggle value: $value" }
        AppSetting.isHotWordEnabled.value = value
    }

    override fun intentHandlingToggle(value: Boolean) {
        logger.d { "intentHandlingToggle value: $value" }
        AppSetting.isIntentHandlingEnabled.value = value
    }

    override fun audioOutputToggle(value: Boolean) {
        logger.d { "audioOutputToggle value: $value" }
        AppSetting.isAudioOutputEnabled.value = value
    }

    override fun setAudioVolume(volume: Float) {
        logger.d { "setAudioVolume volume: $volume" }
        AppSetting.volume.value = volume
    }

}
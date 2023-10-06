package org.rhasspy.mobile.logic.local.settings

import co.touchlab.kermit.Logger
import org.rhasspy.mobile.logic.IDomain
import org.rhasspy.mobile.logic.Source
import org.rhasspy.mobile.logic.Source.*
import org.rhasspy.mobile.settings.AppSetting

internal interface IAppSettingsUtil : IDomain {

    fun hotWordToggle(value: Boolean, source: Source)
    fun intentHandlingToggle(value: Boolean, source: Source)
    fun audioOutputToggle(value: Boolean, source: Source)
    fun setAudioVolume(volume: Float, source: Source)

}

/**
 * handles changes of app settings that are called remotely
 */
internal class AppSettingsUtil : IAppSettingsUtil {

    private val logger = Logger.withTag("AppSettingsService")

    override fun hotWordToggle(value: Boolean, source: Source) {

        when (source) {
            HttpApi -> if (!AppSetting.isHttpApiDeviceChangeEnabled.value) return
            Local   -> Unit
            is Mqtt -> if (!AppSetting.isMqttApiDeviceChangeEnabled.value) return
        }

        logger.d { "hotWordToggle value: $value" }
        AppSetting.isHotWordEnabled.value = value
    }

    override fun intentHandlingToggle(value: Boolean, source: Source) {

        when (source) {
            HttpApi -> if (!AppSetting.isHttpApiDeviceChangeEnabled.value) return
            Local   -> Unit
            is Mqtt -> if (!AppSetting.isMqttApiDeviceChangeEnabled.value) return
        }

        logger.d { "intentHandlingToggle value: $value" }
        AppSetting.isIntentHandlingEnabled.value = value
    }

    override fun audioOutputToggle(value: Boolean, source: Source) {

        when (source) {
            HttpApi -> if (!AppSetting.isHttpApiDeviceChangeEnabled.value) return
            Local   -> Unit
            is Mqtt -> if (!AppSetting.isMqttApiDeviceChangeEnabled.value) return
        }

        logger.d { "audioOutputToggle value: $value" }
        AppSetting.isAudioOutputEnabled.value = value
    }

    override fun setAudioVolume(volume: Float, source: Source) {

        when (source) {
            HttpApi -> if (!AppSetting.isHttpApiDeviceChangeEnabled.value) return
            Local   -> Unit
            is Mqtt -> if (!AppSetting.isMqttApiDeviceChangeEnabled.value) return
        }

        logger.d { "setAudioVolume volume: $volume" }
        AppSetting.volume.value = volume
    }

    override fun dispose() {}

}
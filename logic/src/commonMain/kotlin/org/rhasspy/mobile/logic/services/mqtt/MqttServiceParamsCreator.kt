package org.rhasspy.mobile.logic.services.mqtt

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.data.mqtt.MqttServiceConnectionOptions
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.ConfigurationSetting

internal class MqttServiceParamsCreator {

    operator fun invoke(): StateFlow<MqttServiceParams> {

        return combineStateFlow(
            ConfigurationSetting.siteId.data,
            ConfigurationSetting.isMqttEnabled.data,
            ConfigurationSetting.mqttHost.data,
            ConfigurationSetting.mqttPort.data,
            ConfigurationSetting.mqttRetryInterval.data,
            ConfigurationSetting.isMqttSSLEnabled.data,
            ConfigurationSetting.mqttKeyStoreFile.data,
            ConfigurationSetting.mqttKeyStorePassword.data,
            ConfigurationSetting.mqttUserName.data,
            ConfigurationSetting.mqttPassword.data,
            ConfigurationSetting.mqttConnectionTimeout.data,
            ConfigurationSetting.mqttKeepAliveInterval.data,
            ConfigurationSetting.isUseSpeechToTextMqttSilenceDetection.data,
            ConfigurationSetting.audioPlayingMqttSiteId.data
        ).mapReadonlyState {
            getParams()
        }

    }

    private fun getParams(): MqttServiceParams {
        return MqttServiceParams(
            siteId = ConfigurationSetting.siteId.value,
            isMqttEnabled = ConfigurationSetting.isMqttEnabled.value,
            mqttHost = ConfigurationSetting.mqttHost.value,
            mqttPort = ConfigurationSetting.mqttPort.value,
            retryInterval = ConfigurationSetting.mqttRetryInterval.value,
            mqttServiceConnectionOptions = MqttServiceConnectionOptions(
                isSSLEnabled = ConfigurationSetting.isMqttSSLEnabled.value,
                keyStorePath = ConfigurationSetting.mqttKeyStoreFile.value,
                keyStorePassword = ConfigurationSetting.mqttKeyStorePassword.value,
                connUsername = ConfigurationSetting.mqttUserName.value,
                connPassword = ConfigurationSetting.mqttPassword.value,
                connectionTimeout = ConfigurationSetting.mqttConnectionTimeout.value.toInt(),
                keepAliveInterval = ConfigurationSetting.mqttKeepAliveInterval.value.toInt()
            ),
            isUseSpeechToTextMqttSilenceDetection = ConfigurationSetting.isUseSpeechToTextMqttSilenceDetection.value,
            audioPlayingMqttSiteId = ConfigurationSetting.audioPlayingMqttSiteId.value
        )
    }

}
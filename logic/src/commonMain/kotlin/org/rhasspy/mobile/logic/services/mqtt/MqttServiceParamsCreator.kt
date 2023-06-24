package org.rhasspy.mobile.logic.services.mqtt

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.data.mqtt.MqttServiceConnectionOptions
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.settings.ConfigurationSetting

class MqttServiceParamsCreator {

    private val updaterScope = CoroutineScope(Dispatchers.IO)
    private val paramsFlow = MutableStateFlow(getParams())

    operator fun invoke(): StateFlow<MqttServiceParams> {
        updaterScope.launch {
            combineStateFlow(
                ConfigurationSetting.siteId.data,
                ConfigurationSetting.isMqttEnabled.data,
                ConfigurationSetting.mqttHost.data,
                ConfigurationSetting.mqttPort.data,
                ConfigurationSetting.mqttRetryInterval.data,
                ConfigurationSetting.isMqttSSLEnabled.data,
                ConfigurationSetting.mqttKeyStoreFile.data,
                ConfigurationSetting.mqttUserName.data,
                ConfigurationSetting.mqttPassword.data,
                ConfigurationSetting.mqttConnectionTimeout.data,
                ConfigurationSetting.mqttKeepAliveInterval.data,
                ConfigurationSetting.isUseSpeechToTextMqttSilenceDetection.data,
                ConfigurationSetting.audioPlayingMqttSiteId.data
            ).collect {
                paramsFlow.value = getParams()
            }
        }

        return paramsFlow
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
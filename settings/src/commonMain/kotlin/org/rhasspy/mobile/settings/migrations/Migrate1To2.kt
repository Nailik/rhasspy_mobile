package org.rhasspy.mobile.settings.migrations

import org.rhasspy.mobile.data.connection.HttpConnectionData
import org.rhasspy.mobile.data.connection.LocalWebserverConnectionData
import org.rhasspy.mobile.data.connection.MqttConnectionData
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.settings.ISetting

class Migrate0To1 : IMigration(0, 1) {

    private enum class DeprecatedSettingsEnum {
        MQTTEnabled,
        MQTTHost,
        MQTTPort,
        MQTTUserName,
        MQTTSSLEnabled,
        MQTTPassword,
        MQTTConnectionTimeout,
        MQTTKeepAliveInterval,
        MQTTRetryInterval,
        MQTTKeyStoreFile,
        SSLVerificationDisabled,
        HttpClientServerEndpointHost,
        HttpClientServerEndpointPort,
        HttpClientTimeout,
        IntentHandlingEndpoint,
        IntentHandlingHassUrl,
        IntentHandlingHassAccessToken,
        HttpServerEnabled,
        HttpServerPort,
        HttpServerSSLEnabled,
        HttpServerSSLKeyStoreFile,
        HttpServerSSLKeyStorePassword,
        HttpServerSSLKeyAlias,
        HttpServerSSLKeyPassword
    }

    private val isMqttEnabled = ISetting(DeprecatedSettingsEnum.MQTTEnabled, false)
    private val mqttHost = ISetting(DeprecatedSettingsEnum.MQTTHost, "")
    private val mqttPort = ISetting(DeprecatedSettingsEnum.MQTTPort, 1883)
    private val mqttUserName = ISetting(DeprecatedSettingsEnum.MQTTUserName, "")
    private val mqttPassword = ISetting(DeprecatedSettingsEnum.MQTTPassword, "")
    private val isMqttSSLEnabled = ISetting(DeprecatedSettingsEnum.MQTTSSLEnabled, false)
    private val mqttConnectionTimeout = ISetting(DeprecatedSettingsEnum.MQTTConnectionTimeout, 5L)
    private val mqttKeepAliveInterval = ISetting(DeprecatedSettingsEnum.MQTTKeepAliveInterval, 30L)
    private val mqttRetryInterval = ISetting(DeprecatedSettingsEnum.MQTTRetryInterval, 10L)
    private val mqttKeyStoreFile = ISetting<String?>(DeprecatedSettingsEnum.MQTTKeyStoreFile, null)

    private val isHttpClientSSLVerificationDisabled = ISetting(DeprecatedSettingsEnum.SSLVerificationDisabled, true)
    private val httpClientServerEndpointHost = ISetting(DeprecatedSettingsEnum.HttpClientServerEndpointHost, "")
    private val httpClientServerEndpointPort = ISetting(DeprecatedSettingsEnum.HttpClientServerEndpointPort, 12101)
    private val httpClientTimeout = ISetting(DeprecatedSettingsEnum.HttpClientTimeout, 30000L)

    private val intentHandlingHttpEndpoint = ISetting(DeprecatedSettingsEnum.IntentHandlingEndpoint, "")
    private val intentHandlingHomeAssistantEndpoint = ISetting(DeprecatedSettingsEnum.IntentHandlingHassUrl, "")
    private val intentHandlingHomeAssistantAccessToken = ISetting(DeprecatedSettingsEnum.IntentHandlingHassAccessToken, "")

    private val isHttpServerEnabled = ISetting(DeprecatedSettingsEnum.HttpServerEnabled, true)
    private val httpServerPort = ISetting(DeprecatedSettingsEnum.HttpServerPort, 12101)
    private val isHttpServerSSLEnabledEnabled = ISetting(DeprecatedSettingsEnum.HttpServerSSLEnabled, false)
    private val httpServerSSLKeyStoreFile = ISetting<String?>(DeprecatedSettingsEnum.HttpServerSSLKeyStoreFile, null)
    private val httpServerSSLKeyStorePassword = ISetting(DeprecatedSettingsEnum.HttpServerSSLKeyStorePassword, "")
    private val httpServerSSLKeyAlias = ISetting(DeprecatedSettingsEnum.HttpServerSSLKeyAlias, "")
    private val httpServerSSLKeyPassword = ISetting(DeprecatedSettingsEnum.HttpServerSSLKeyPassword, "")

    override fun migrate() {
        ConfigurationSetting.rhasspy2Connection.value = HttpConnectionData(
            host = "${httpClientServerEndpointHost.value}:${httpClientServerEndpointPort.value}",
            timeout = httpClientTimeout.value,
            bearerToken = "",
            isSSLVerificationDisabled = isHttpClientSSLVerificationDisabled.value
        )
        ConfigurationSetting.homeAssistantConnection.value = HttpConnectionData(
            host = intentHandlingHomeAssistantEndpoint.value,
            timeout = httpClientTimeout.value,
            bearerToken = intentHandlingHomeAssistantAccessToken.value,
            isSSLVerificationDisabled = isHttpClientSSLVerificationDisabled.value
        )
        ConfigurationSetting.mqttConnection.value = MqttConnectionData(
            isEnabled = isMqttEnabled.value,
            host = "tcp://${mqttHost.value}:${mqttPort.value}",
            userName = mqttUserName.value,
            password = mqttPassword.value,
            isSslEnabled = isMqttSSLEnabled.value,
            connectionTimeout = mqttConnectionTimeout.value.toInt(),
            keepAliveInterval = mqttKeepAliveInterval.value.toInt(),
            retryInterval = mqttRetryInterval.value,
            keystoreFile = mqttKeyStoreFile.value
        )
        ConfigurationSetting.localWebserverConnection.value = LocalWebserverConnectionData(
            isEnabled = isHttpServerEnabled.value,
            port = httpServerPort.value,
            isSSLEnabled = isHttpServerSSLEnabledEnabled.value,
            keyStoreFile = httpServerSSLKeyStoreFile.value,
            keyStorePassword = httpServerSSLKeyStorePassword.value,
            keyAlias = httpServerSSLKeyAlias.value,
            keyPassword = httpServerSSLKeyPassword.value,
        )

        isHttpClientSSLVerificationDisabled.delete()
        httpClientServerEndpointHost.delete()
        httpClientServerEndpointPort.delete()
        httpClientTimeout.delete()

        intentHandlingHttpEndpoint.delete()
        intentHandlingHomeAssistantEndpoint.delete()
        intentHandlingHomeAssistantAccessToken.delete()

        isMqttEnabled.delete()
        mqttHost.delete()
        mqttPort.delete()
        mqttUserName.delete()
        mqttPassword.delete()
        isMqttSSLEnabled.delete()
        mqttConnectionTimeout.delete()
        mqttKeepAliveInterval.delete()
        mqttRetryInterval.delete()
        mqttKeyStoreFile.delete()

        isHttpServerEnabled.delete()
        httpServerPort.delete()
        isHttpServerSSLEnabledEnabled.delete()
        httpServerSSLKeyStoreFile.delete()
        httpServerSSLKeyStorePassword.delete()
        httpServerSSLKeyAlias.delete()
        httpServerSSLKeyPassword.delete()
    }

}
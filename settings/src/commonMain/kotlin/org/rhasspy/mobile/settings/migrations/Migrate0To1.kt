package org.rhasspy.mobile.settings.migrations

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.get
import com.russhwolf.settings.serialization.encodeValue
import com.russhwolf.settings.set
import kotlinx.serialization.ExperimentalSerializationApi
import org.rhasspy.mobile.data.connection.HttpConnectionData
import org.rhasspy.mobile.data.connection.LocalWebserverConnectionData
import org.rhasspy.mobile.data.connection.MqttConnectionData
import org.rhasspy.mobile.data.service.option.*
import org.rhasspy.mobile.data.settings.SettingsEnum

internal class Migrate0To1 : IMigration(0, 1) {

    override fun preMigrate() {
        if (settings[SettingsEnum.AudioPlayingOption.name, ""] == "RemoteHTTP") {
            settings[SettingsEnum.AudioPlayingOption.name] = AudioPlayingOption.Rhasspy2HermesHttp.name
        }
        if (settings[SettingsEnum.AudioPlayingOption.name, ""] == "RemoteMQTT") {
            settings[SettingsEnum.AudioPlayingOption.name] = AudioPlayingOption.Rhasspy2HermesMQTT.name
        }
        if (settings[SettingsEnum.DialogManagementOption.name, ""] == "RemoteMQTT") {
            settings[SettingsEnum.DialogManagementOption.name] = DialogManagementOption.Rhasspy2HermesMQTT.name
        }
        if (settings[SettingsEnum.IntentHandlingOption.name, ""] == "RemoteHTTP") {
            settings[SettingsEnum.IntentHandlingOption.name] = IntentHandlingOption.Rhasspy2HermesHttp.name
        }
        if (settings[SettingsEnum.IntentRecognitionOption.name, ""] == "RemoteHTTP") {
            settings[SettingsEnum.IntentRecognitionOption.name] = IntentRecognitionOption.Rhasspy2HermesHttp.name
        }
        if (settings[SettingsEnum.IntentRecognitionOption.name, ""] == "RemoteMQTT") {
            settings[SettingsEnum.IntentRecognitionOption.name] = IntentRecognitionOption.Rhasspy2HermesMQTT.name
        }
        if (settings[SettingsEnum.SpeechToTextOption.name, ""] == "RemoteMQTT") {
            settings[SettingsEnum.SpeechToTextOption.name] = SpeechToTextOption.Rhasspy2HermesMQTT.name
        }
        if (settings[SettingsEnum.SpeechToTextOption.name, ""] == "RemoteMQTT") {
            settings[SettingsEnum.SpeechToTextOption.name] = SpeechToTextOption.Rhasspy2HermesMQTT.name
        }
        if (settings[SettingsEnum.TextToSpeechOption.name, ""] == "RemoteMQTT") {
            settings[SettingsEnum.TextToSpeechOption.name] = TextToSpeechOption.Rhasspy2HermesMQTT.name
        }
        if (settings[SettingsEnum.TextToSpeechOption.name, ""] == "RemoteMQTT") {
            settings[SettingsEnum.TextToSpeechOption.name] = TextToSpeechOption.Rhasspy2HermesMQTT.name
        }
        if (settings[SettingsEnum.WakeWordOption.name, ""] == "MQTT") {
            settings[SettingsEnum.WakeWordOption.name] = WakeWordOption.Rhasspy2HermesMQTT.name
        }
    }

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

    private val isMqttEnabled = settings[DeprecatedSettingsEnum.MQTTEnabled.name, false]
    private val mqttHost = settings[DeprecatedSettingsEnum.MQTTHost.name, ""]
    private val mqttPort = settings[DeprecatedSettingsEnum.MQTTPort.name, 1883]
    private val mqttUserName = settings[DeprecatedSettingsEnum.MQTTUserName.name, ""]
    private val mqttPassword = settings[DeprecatedSettingsEnum.MQTTPassword.name, ""]
    private val isMqttSSLEnabled = settings[DeprecatedSettingsEnum.MQTTSSLEnabled.name, false]
    private val mqttConnectionTimeout = settings[DeprecatedSettingsEnum.MQTTConnectionTimeout.name, 5L]
    private val mqttKeepAliveInterval = settings[DeprecatedSettingsEnum.MQTTKeepAliveInterval.name, 30L]
    private val mqttRetryInterval = settings[DeprecatedSettingsEnum.MQTTRetryInterval.name, 10L]
    private val mqttKeyStoreFile = settings[DeprecatedSettingsEnum.MQTTKeyStoreFile.name, ""]

    private val isHttpClientSSLVerificationDisabled = settings[DeprecatedSettingsEnum.SSLVerificationDisabled.name, true]
    private val httpClientServerEndpointHost = settings[DeprecatedSettingsEnum.HttpClientServerEndpointHost.name, ""]
    private val httpClientServerEndpointPort = settings[DeprecatedSettingsEnum.HttpClientServerEndpointPort.name, 12101]
    private val httpClientTimeout = settings[DeprecatedSettingsEnum.HttpClientTimeout.name, 30000L]

    private val intentHandlingHomeAssistantEndpoint = settings[DeprecatedSettingsEnum.IntentHandlingHassUrl.name, ""]
    private val intentHandlingHomeAssistantAccessToken = settings[DeprecatedSettingsEnum.IntentHandlingHassAccessToken.name, ""]

    private val isHttpServerEnabled = settings[DeprecatedSettingsEnum.HttpServerEnabled.name, true]
    private val httpServerPort = settings[DeprecatedSettingsEnum.HttpServerPort.name, 12101]
    private val isHttpServerSSLEnabledEnabled = settings[DeprecatedSettingsEnum.HttpServerSSLEnabled.name, false]
    private val httpServerSSLKeyStoreFile = settings[DeprecatedSettingsEnum.HttpServerSSLKeyStoreFile.name, ""]
    private val httpServerSSLKeyStorePassword = settings[DeprecatedSettingsEnum.HttpServerSSLKeyStorePassword.name, ""]
    private val httpServerSSLKeyAlias = settings[DeprecatedSettingsEnum.HttpServerSSLKeyAlias.name, ""]
    private val httpServerSSLKeyPassword = settings[DeprecatedSettingsEnum.HttpServerSSLKeyPassword.name, ""]

    @OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
    override fun migrate() {

        settings.encodeValue(
            HttpConnectionData.serializer(), SettingsEnum.Rhasspy2Connection.name,
            HttpConnectionData(
                host = "${httpClientServerEndpointHost}:${httpClientServerEndpointPort}",
                timeout = httpClientTimeout,
                bearerToken = "",
                isSSLVerificationDisabled = isHttpClientSSLVerificationDisabled
            )
        )

        settings.encodeValue(
            HttpConnectionData.serializer(), SettingsEnum.HomeAssistantConnection.name,
            HttpConnectionData(
                host = intentHandlingHomeAssistantEndpoint,
                timeout = httpClientTimeout,
                bearerToken = intentHandlingHomeAssistantAccessToken,
                isSSLVerificationDisabled = isHttpClientSSLVerificationDisabled
            )
        )

        settings.encodeValue(
            MqttConnectionData.serializer(), SettingsEnum.MqttConnection.name,
            MqttConnectionData(
                isEnabled = isMqttEnabled,
                host = "tcp://${mqttHost}:${mqttPort}",
                userName = mqttUserName,
                password = mqttPassword,
                isSSLEnabled = isMqttSSLEnabled,
                connectionTimeout = mqttConnectionTimeout.toInt(),
                keepAliveInterval = mqttKeepAliveInterval.toInt(),
                retryInterval = mqttRetryInterval,
                keystoreFile = mqttKeyStoreFile.ifEmpty { null }
            )
        )

        settings.encodeValue(
            LocalWebserverConnectionData.serializer(), SettingsEnum.LocalWebserverConnection.name,
            LocalWebserverConnectionData(
                isEnabled = isHttpServerEnabled,
                port = httpServerPort,
                isSSLEnabled = isHttpServerSSLEnabledEnabled,
                keyStoreFile = httpServerSSLKeyStoreFile.ifEmpty { null },
                keyStorePassword = httpServerSSLKeyStorePassword,
                keyAlias = httpServerSSLKeyAlias,
                keyPassword = httpServerSSLKeyPassword,
            )
        )

        settings.remove(DeprecatedSettingsEnum.MQTTEnabled.name)
        settings.remove(DeprecatedSettingsEnum.MQTTHost.name)
        settings.remove(DeprecatedSettingsEnum.MQTTPort.name)
        settings.remove(DeprecatedSettingsEnum.MQTTUserName.name)
        settings.remove(DeprecatedSettingsEnum.MQTTPassword.name)
        settings.remove(DeprecatedSettingsEnum.MQTTSSLEnabled.name)
        settings.remove(DeprecatedSettingsEnum.MQTTConnectionTimeout.name)
        settings.remove(DeprecatedSettingsEnum.MQTTKeepAliveInterval.name)
        settings.remove(DeprecatedSettingsEnum.MQTTRetryInterval.name)
        settings.remove(DeprecatedSettingsEnum.MQTTKeyStoreFile.name)

        settings.remove(DeprecatedSettingsEnum.SSLVerificationDisabled.name)
        settings.remove(DeprecatedSettingsEnum.HttpClientServerEndpointHost.name)
        settings.remove(DeprecatedSettingsEnum.HttpClientServerEndpointPort.name)
        settings.remove(DeprecatedSettingsEnum.HttpClientTimeout.name)

        settings.remove(DeprecatedSettingsEnum.IntentHandlingEndpoint.name)
        settings.remove(DeprecatedSettingsEnum.IntentHandlingHassUrl.name)
        settings.remove(DeprecatedSettingsEnum.IntentHandlingHassAccessToken.name)

        settings.remove(DeprecatedSettingsEnum.HttpServerEnabled.name)
        settings.remove(DeprecatedSettingsEnum.HttpServerPort.name)
        settings.remove(DeprecatedSettingsEnum.HttpServerSSLEnabled.name)
        settings.remove(DeprecatedSettingsEnum.HttpServerSSLKeyStoreFile.name)
        settings.remove(DeprecatedSettingsEnum.HttpServerSSLKeyStorePassword.name)
        settings.remove(DeprecatedSettingsEnum.HttpServerSSLKeyAlias.name)
        settings.remove(DeprecatedSettingsEnum.HttpServerSSLKeyPassword.name)
    }

}
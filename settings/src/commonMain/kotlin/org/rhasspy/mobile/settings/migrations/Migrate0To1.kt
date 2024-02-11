package org.rhasspy.mobile.settings.migrations

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.get
import com.russhwolf.settings.serialization.encodeValue
import com.russhwolf.settings.set
import kotlinx.serialization.ExperimentalSerializationApi
import org.rhasspy.mobile.data.connection.HttpConnectionData
import org.rhasspy.mobile.data.connection.LocalWebserverConnectionData
import org.rhasspy.mobile.data.connection.MqttConnectionData
import org.rhasspy.mobile.data.settings.SettingsEnum
import kotlin.time.DurationUnit
import kotlin.time.toDuration

internal object Migrate0To1 : IMigration(0, 1) {

    private enum class MigrationSettingsEnum {
        AudioPlayingOption,
        DialogManagementOption,
        IntentHandlingOption,
        IntentRecognitionOption,
        SpeechToTextOption,
        TextToSpeechOption,
        WakeWordOption,
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

    override fun preMigrate() {
        if (settings[MigrationSettingsEnum.AudioPlayingOption.name, ""] == "RemoteHTTP") {
            settings[MigrationSettingsEnum.AudioPlayingOption.name] = "Rhasspy2HermesHttp"
        }
        if (settings[MigrationSettingsEnum.AudioPlayingOption.name, ""] == "RemoteMQTT") {
            settings[MigrationSettingsEnum.AudioPlayingOption.name] = "Rhasspy2HermesMQTT"
        }
        if (settings[MigrationSettingsEnum.DialogManagementOption.name, ""] == "RemoteMQTT") {
            settings[MigrationSettingsEnum.DialogManagementOption.name] = "Rhasspy2HermesMQTT"
        }
        if (settings[MigrationSettingsEnum.IntentHandlingOption.name, ""] == "RemoteHTTP") {
            settings[MigrationSettingsEnum.IntentHandlingOption.name] = "Rhasspy2HermesHttp"
        }
        if (settings[MigrationSettingsEnum.IntentRecognitionOption.name, ""] == "RemoteHTTP") {
            settings[MigrationSettingsEnum.IntentRecognitionOption.name] = "Rhasspy2HermesHttp"
        }
        if (settings[MigrationSettingsEnum.IntentRecognitionOption.name, ""] == "RemoteMQTT") {
            settings[MigrationSettingsEnum.IntentRecognitionOption.name] = "Rhasspy2HermesMQTT"
        }
        if (settings[MigrationSettingsEnum.SpeechToTextOption.name, ""] == "RemoteMQTT") {
            settings[MigrationSettingsEnum.SpeechToTextOption.name] = "Rhasspy2HermesMQTT"
        }
        if (settings[MigrationSettingsEnum.SpeechToTextOption.name, ""] == "RemoteHTTP") {
            settings[MigrationSettingsEnum.SpeechToTextOption.name] = "Rhasspy2HermesHttp"
        }
        if (settings[MigrationSettingsEnum.TextToSpeechOption.name, ""] == "RemoteMQTT") {
            settings[MigrationSettingsEnum.TextToSpeechOption.name] = "Rhasspy2HermesMQTT"
        }
        if (settings[MigrationSettingsEnum.TextToSpeechOption.name, ""] == "RemoteHTTP") {
            settings[MigrationSettingsEnum.TextToSpeechOption.name] = "Rhasspy2HermesHttp"
        }
        if (settings[MigrationSettingsEnum.WakeWordOption.name, ""] == "MQTT") {
            settings[MigrationSettingsEnum.WakeWordOption.name] = "Rhasspy2HermesMQTT"
        }
    }

    private val isMqttEnabled = settings[MigrationSettingsEnum.MQTTEnabled.name, false]
    private val mqttHost = settings[MigrationSettingsEnum.MQTTHost.name, ""]
    private val mqttPort = settings[MigrationSettingsEnum.MQTTPort.name, 1883]
    private val mqttUserName = settings[MigrationSettingsEnum.MQTTUserName.name, ""]
    private val mqttPassword = settings[MigrationSettingsEnum.MQTTPassword.name, ""]
    private val isMqttSSLEnabled = settings[MigrationSettingsEnum.MQTTSSLEnabled.name, false]
    private val mqttConnectionTimeout = settings[MigrationSettingsEnum.MQTTConnectionTimeout.name, 5L]
    private val mqttKeepAliveInterval = settings[MigrationSettingsEnum.MQTTKeepAliveInterval.name, 30L]
    private val mqttRetryInterval = settings[MigrationSettingsEnum.MQTTRetryInterval.name, 10L]
    private val mqttKeyStoreFile = settings[MigrationSettingsEnum.MQTTKeyStoreFile.name, ""]

    private val isHttpClientSSLVerificationDisabled = settings[MigrationSettingsEnum.SSLVerificationDisabled.name, true]
    private val httpClientServerEndpointHost = settings[MigrationSettingsEnum.HttpClientServerEndpointHost.name, ""]
    private val httpClientServerEndpointPort = settings[MigrationSettingsEnum.HttpClientServerEndpointPort.name, 12101]
    private val httpClientTimeout = settings[MigrationSettingsEnum.HttpClientTimeout.name, 30000L]

    private val intentHandlingHomeAssistantEndpoint = settings[MigrationSettingsEnum.IntentHandlingHassUrl.name, ""]
    private val intentHandlingHomeAssistantAccessToken = settings[MigrationSettingsEnum.IntentHandlingHassAccessToken.name, ""]

    private val isHttpServerEnabled = settings[MigrationSettingsEnum.HttpServerEnabled.name, true]
    private val httpServerPort = settings[MigrationSettingsEnum.HttpServerPort.name, 12101]
    private val isHttpServerSSLEnabledEnabled = settings[MigrationSettingsEnum.HttpServerSSLEnabled.name, false]
    private val httpServerSSLKeyStoreFile = settings[MigrationSettingsEnum.HttpServerSSLKeyStoreFile.name, ""]
    private val httpServerSSLKeyStorePassword = settings[MigrationSettingsEnum.HttpServerSSLKeyStorePassword.name, ""]
    private val httpServerSSLKeyAlias = settings[MigrationSettingsEnum.HttpServerSSLKeyAlias.name, ""]
    private val httpServerSSLKeyPassword = settings[MigrationSettingsEnum.HttpServerSSLKeyPassword.name, ""]

    @OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
    override fun migrate() {

        settings.encodeValue(
            HttpConnectionData.serializer(), SettingsEnum.Rhasspy2Connection.name,
            HttpConnectionData(
                host = "${httpClientServerEndpointHost}:${httpClientServerEndpointPort}",
                timeout = httpClientTimeout.toDuration(DurationUnit.SECONDS),
                bearerToken = "",
                isSSLVerificationDisabled = isHttpClientSSLVerificationDisabled
            )
        )

        settings.encodeValue(
            HttpConnectionData.serializer(), SettingsEnum.HomeAssistantConnection.name,
            HttpConnectionData(
                host = intentHandlingHomeAssistantEndpoint,
                timeout = httpClientTimeout.toDuration(DurationUnit.SECONDS),
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
                connectionTimeout = mqttConnectionTimeout.toDuration(DurationUnit.SECONDS),
                keepAliveInterval = mqttKeepAliveInterval.toDuration(DurationUnit.SECONDS),
                retryInterval = mqttRetryInterval.toDuration(DurationUnit.SECONDS),
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

        settings.remove(MigrationSettingsEnum.MQTTEnabled.name)
        settings.remove(MigrationSettingsEnum.MQTTHost.name)
        settings.remove(MigrationSettingsEnum.MQTTPort.name)
        settings.remove(MigrationSettingsEnum.MQTTUserName.name)
        settings.remove(MigrationSettingsEnum.MQTTPassword.name)
        settings.remove(MigrationSettingsEnum.MQTTSSLEnabled.name)
        settings.remove(MigrationSettingsEnum.MQTTConnectionTimeout.name)
        settings.remove(MigrationSettingsEnum.MQTTKeepAliveInterval.name)
        settings.remove(MigrationSettingsEnum.MQTTRetryInterval.name)
        settings.remove(MigrationSettingsEnum.MQTTKeyStoreFile.name)

        settings.remove(MigrationSettingsEnum.SSLVerificationDisabled.name)
        settings.remove(MigrationSettingsEnum.HttpClientServerEndpointHost.name)
        settings.remove(MigrationSettingsEnum.HttpClientServerEndpointPort.name)
        settings.remove(MigrationSettingsEnum.HttpClientTimeout.name)

        settings.remove(MigrationSettingsEnum.IntentHandlingEndpoint.name)
        settings.remove(MigrationSettingsEnum.IntentHandlingHassUrl.name)
        settings.remove(MigrationSettingsEnum.IntentHandlingHassAccessToken.name)

        settings.remove(MigrationSettingsEnum.HttpServerEnabled.name)
        settings.remove(MigrationSettingsEnum.HttpServerPort.name)
        settings.remove(MigrationSettingsEnum.HttpServerSSLEnabled.name)
        settings.remove(MigrationSettingsEnum.HttpServerSSLKeyStoreFile.name)
        settings.remove(MigrationSettingsEnum.HttpServerSSLKeyStorePassword.name)
        settings.remove(MigrationSettingsEnum.HttpServerSSLKeyAlias.name)
        settings.remove(MigrationSettingsEnum.HttpServerSSLKeyPassword.name)
    }

}
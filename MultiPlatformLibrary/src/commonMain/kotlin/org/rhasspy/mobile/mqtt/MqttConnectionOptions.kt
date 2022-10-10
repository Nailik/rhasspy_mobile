package org.rhasspy.mobile.mqtt

import co.touchlab.kermit.Logger
import org.rhasspy.mobile.settings.ConfigurationSettings

val logger = Logger.withTag("MqttConnectionOptions")

/** Provides all MQTT connection options. */
data class MqttConnectionOptions(
    /**
     * When set to *true* the session isn't retained. This means no subscriptions or undelivered messages are
     * stored.
     */
    val cleanSession: Boolean,
    val cleanStart: Boolean,
    /** Connection timeout in seconds. */
    val connectionTimeout: Int,
    val retryInterval: Int,
    /** Keep alive interval in seconds. */
    val keepAliveInterval: Int,
    val connUsername: String,
    val connPassword: String
) {

    companion object {
        fun loadFromConfigurationSettings(): MqttConnectionOptions {

            val connectionTimeout = ConfigurationSettings.mqttConnectionTimeout.value.toIntOrNull() ?: kotlin.run {
                logger.w { "using default connectionTimeout 5 because ${ConfigurationSettings.mqttConnectionTimeout.value} is not an int or null" }
                5
            }

            val mqttRetryInterval = ConfigurationSettings.mqttRetryInterval.value.toIntOrNull() ?: kotlin.run {
                logger.w { "using default mqttKeepAliveInterval 10 because ${ConfigurationSettings.mqttRetryInterval.value} is not an int or null" }
                10
            }

            val mqttKeepAliveInterval = ConfigurationSettings.mqttKeepAliveInterval.value.toIntOrNull() ?: kotlin.run {
                logger.w {
                    "using default mqttKeepAliveInterval 30 because ${ConfigurationSettings.mqttKeepAliveInterval.value} is not an int or null"
                }
                30
            }

            return MqttConnectionOptions(
                cleanSession = true,
                cleanStart = false,
                connUsername = ConfigurationSettings.mqttUserName.value,
                connPassword = ConfigurationSettings.mqttPassword.value,
                connectionTimeout = connectionTimeout,
                retryInterval = mqttRetryInterval,
                keepAliveInterval = mqttKeepAliveInterval
            )
        }

        fun loadFromUnsavedConfigurationSettings(): MqttConnectionOptions {
/*
            val connectionTimeout = ConfigurationSettings.mqttConnectionTimeout.unsaved.value.toIntOrNull() ?: kotlin.run {
                logger.w { "using default connectionTimeout 5 because ${ConfigurationSettings.mqttConnectionTimeout.unsaved.value} is not an int or null" }
                5
            }

            val mqttRetryInterval = ConfigurationSettings.mqttRetryInterval.unsaved.value.toIntOrNull() ?: kotlin.run {
                logger.w { "using default mqttKeepAliveInterval 10 because ${ConfigurationSettings.mqttRetryInterval.unsaved.value} is not an int or null" }
                10
            }

            val mqttKeepAliveInterval = ConfigurationSettings.mqttKeepAliveInterval.unsaved.value.toIntOrNull() ?: kotlin.run {
                logger.w {
                    "using default mqttKeepAliveInterval 30 because ${ConfigurationSettings.mqttKeepAliveInterval.unsaved.value} is not an int or null"
                }
                30
            }

            return MqttConnectionOptions(
                cleanSession = true,
                cleanStart = false,
                connUsername = ConfigurationSettings.mqttUserName.unsaved.value,
                connPassword = ConfigurationSettings.mqttPassword.unsaved.value,
                connectionTimeout = connectionTimeout,
                retryInterval = mqttRetryInterval,
                keepAliveInterval = mqttKeepAliveInterval
            )*/
            return loadFromConfigurationSettings()
        }
    }

}

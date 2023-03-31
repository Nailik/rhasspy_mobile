package org.rhasspy.mobile.data.mqtt

import okio.Path

/** Provides all MQTT connection options. */
data class MqttServiceConnectionOptions(
    /**
     * When set to *true* the session isn't retained. This means no subscriptions or undelivered messages are
     * stored.
     */
    val cleanSession: Boolean = false,
    val cleanStart: Boolean = true,
    val isSSLEnabled: Boolean,
    val keyStorePath: Path?,
    /** Connection timeout in seconds. */
    val connectionTimeout: Int,
    /** Keep alive interval in seconds. */
    val keepAliveInterval: Int,
    val connUsername: String,
    val connPassword: String
)
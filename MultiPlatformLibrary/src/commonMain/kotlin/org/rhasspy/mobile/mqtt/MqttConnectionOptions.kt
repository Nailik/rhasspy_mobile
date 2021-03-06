package org.rhasspy.mobile.mqtt

/** Provides all MQTT connection options. */
data class MqttConnectionOptions(
    /**
     * When set to *true* the session isn't retained. This means no subscriptions or undelivered messages are
     * stored.
     */
    val cleanSession: Boolean = true,
    val cleanStart: Boolean = false,
    /** Connection timeout in seconds. */
    val connectionTimeout: Int = 5,
    val retryInterval: Int = 0,
    /** Keep alive interval in seconds. */
    val keepAliveInterval: Int = 60,
    val connUsername: String = "",
    val connPassword: String = ""
)

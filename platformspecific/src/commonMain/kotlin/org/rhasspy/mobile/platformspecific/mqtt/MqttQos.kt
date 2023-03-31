package org.rhasspy.mobile.platformspecific.mqtt

/** Provides all possible MQTT QoS values (from 0 to 2). */
enum class MqttQos(val value: Int) {
    AT_MOST_ONCE(0),
    AT_LEAST_ONCE(1),
    EXACTLY_ONCE(2);

    companion object {
        /**
         * Creates an instance of MqttQos from a [value].
         * @param value The value to use.
         * @return An instance of MqttQos.
         */
        fun createMqttQos(value: Int) = when (value) {
            1 -> AT_LEAST_ONCE
            2 -> EXACTLY_ONCE
            else -> AT_MOST_ONCE
        }
    }
}


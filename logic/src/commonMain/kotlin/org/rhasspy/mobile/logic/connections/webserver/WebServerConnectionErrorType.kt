package org.rhasspy.mobile.logic.connections.webserver

internal enum class WebServerConnectionErrorType(val description: String) {
    WakeOptionInvalid("Invalid value, allowed: \"on\", \"off\""),
    VolumeValueOutOfRange("Volume Out of Range, allowed: 0f...1f"),
    VolumeValueInvalid("Invalid Volume, allowed: 0f...1f"),
    AudioContentTypeWarning("Missing Content Type");

    override fun toString(): String {
        return description
    }
}
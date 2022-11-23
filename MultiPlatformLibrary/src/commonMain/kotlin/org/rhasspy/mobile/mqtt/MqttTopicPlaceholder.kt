package org.rhasspy.mobile.mqtt

enum class MqttTopicPlaceholder(val placeholder: String) {
    SiteId("<siteId>"),
    RequestId("<requestId>"),
    SessionId("<sessionId>"),
    WakeWord("<wakewordId>");

    override fun toString(): String {
        return placeholder
    }
}
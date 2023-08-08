package org.rhasspy.mobile.logic.services.mqtt

enum class MqttParams(val value: String) {
    SessionId("sessionId"),
    SiteId("siteId"),
    ModelId("modelId"),
    Error("error"),
    Input("input"),
    StopOnSilence("stopOnSilence"),
    SendAudioCaptured("sendAudioCaptured"),
    Text("text"),
    Intent("intent"),
    Volume("volume"),
    IntentName("intentName");

    override fun toString(): String {
        return value
    }
}
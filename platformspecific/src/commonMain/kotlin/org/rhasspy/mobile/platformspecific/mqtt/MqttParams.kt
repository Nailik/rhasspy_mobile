package org.rhasspy.mobile.platformspecific.mqtt

enum class MqttParams(val value: String) {
    Id("id"),
    RequestId("id"),
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
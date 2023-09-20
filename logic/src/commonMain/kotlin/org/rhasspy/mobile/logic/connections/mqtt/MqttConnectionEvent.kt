package org.rhasspy.mobile.logic.connections.mqtt

sealed interface MqttConnectionEvent {

    data class StartSession(val sessionId: String?) : MqttConnectionEvent
    data class EndSession(val sessionId: String?) : MqttConnectionEvent
    data class SessionStarted(val sessionId: String?) : MqttConnectionEvent
    data class SessionEnded(val sessionId: String?) : MqttConnectionEvent
    data class HotWordDetected(val hotWord: String) : MqttConnectionEvent
    data class StartListening(val sendAudioCaptured: Boolean) : MqttConnectionEvent
    data class StopListening(val sessionId: String?) : MqttConnectionEvent
    data class AsrTextCaptured(val sessionId: String?, val text: String?) : MqttConnectionEvent
    data class AsrError(val sessionId: String?) : MqttConnectionEvent
    data class IntentRecognitionResult(val sessionId: String?, val intentName: String?, val intent: String) : MqttConnectionEvent
    data class IntentNotRecognized(val sessionId: String?) : MqttConnectionEvent
    data class Say(val sessionId: String?, val text: String, val volume: Float?, val siteId: String) : MqttConnectionEvent
    class PlayBytes(val byteArray: ByteArray) : MqttConnectionEvent
    data object PlayFinished : MqttConnectionEvent

}
package org.rhasspy.mobile.logic.connections.mqtt

sealed interface MqttConnectionEvent {

    data class StartSession(val sessionId: String?) : MqttConnectionEvent
    data class EndSession(val sessionId: String?, val text: String?) : MqttConnectionEvent
    data class SessionStarted(val sessionId: String?) : MqttConnectionEvent
    data class SessionEnded(val sessionId: String?) : MqttConnectionEvent
    data class HotWordDetected(val hotWord: String) : MqttConnectionEvent
    data class StartListening(val sendAudioCaptured: Boolean) : MqttConnectionEvent
    data class StopListening(val sessionId: String?) : MqttConnectionEvent

    sealed interface AsrResult : MqttConnectionEvent {

        val sessionId: String?
        data class AsrTextCaptured(override val sessionId: String?, val text: String?) : AsrResult
        data class AsrError(override val sessionId: String?) : AsrResult

    }

    sealed interface IntentResult: MqttConnectionEvent {

        val sessionId: String?

        data class IntentRecognitionResult(override val sessionId: String?, val intentName: String?, val intent: String) : IntentResult
        data class IntentNotRecognized(override val sessionId: String?) : IntentResult

    }
    data class Say(val sessionId: String?, val text: String, val volume: Float?, val siteId: String) : MqttConnectionEvent

    sealed interface PlayResult: MqttConnectionEvent {
        val id: String

        class PlayBytes(override val id: String, val byteArray: ByteArray) : PlayResult
        data class PlayFinished(override val id: String) : PlayResult

    }

}
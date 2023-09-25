package org.rhasspy.mobile.logic.connections.mqtt

sealed interface MqttConnectionEvent {

    sealed interface SessionEvent {

        val sessionId: String?

    }

    data class StartSession(override val sessionId: String?) : SessionEvent, MqttConnectionEvent
    data class EndSession(override val sessionId: String?, val text: String?) : SessionEvent, MqttConnectionEvent
    data class SessionStarted(override val sessionId: String?) : SessionEvent, MqttConnectionEvent
    data class SessionEnded(override val sessionId: String?) : SessionEvent, MqttConnectionEvent
    data class HotWordDetected(val hotWord: String) : MqttConnectionEvent
    data class StartListening(val sendAudioCaptured: Boolean) : MqttConnectionEvent
    data class StopListening(override val sessionId: String?) : SessionEvent, MqttConnectionEvent

    sealed interface AsrResult : SessionEvent, MqttConnectionEvent {
        data class AsrTextCaptured(override val sessionId: String?, val text: String?) : AsrResult
        data class AsrError(override val sessionId: String?) : AsrResult

    }

    sealed interface IntentResult : SessionEvent, MqttConnectionEvent {

        data class IntentRecognitionResult(override val sessionId: String?, val intentName: String?, val intent: String) : IntentResult
        data class IntentNotRecognized(override val sessionId: String?) : IntentResult

    }

    data class Say(override val sessionId: String?, val text: String, val volume: Float?, val siteId: String) : SessionEvent, MqttConnectionEvent

    sealed interface PlayResult : MqttConnectionEvent {
        val id: String

        class PlayBytes(override val id: String, val byteArray: ByteArray) : PlayResult
        data class PlayFinished(override val id: String) : PlayResult

    }

}
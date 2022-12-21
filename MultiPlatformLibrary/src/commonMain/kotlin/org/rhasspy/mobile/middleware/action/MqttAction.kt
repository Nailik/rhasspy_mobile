package org.rhasspy.mobile.middleware.action

sealed interface MqttAction {
    object StartSession : MqttAction
    class EndSession(val sessionId: String?) : MqttAction
    class SessionStarted(val sessionId: String?) : MqttAction
    class SessionEnded(val sessionId: String?) : MqttAction
    class HotWordToggle(val enabled: Boolean) : MqttAction
    class HotWordDetected(val hotWord: String) : MqttAction
    class StartListening(val sendAudioCaptured: Boolean) : MqttAction
    class StopListening(val sessionId: String?) : MqttAction
    class AsrTextCaptured(val sessionId: String?, val text: String?) : MqttAction
    class AsrError(val sessionId: String?) : MqttAction
    class IntentTranscribed(val sessionId: String?, val text: String?) : MqttAction
    class IntentTranscribedError(val sessionId: String?) : MqttAction
    class IntentRecognitionResult(
        val sessionId: String?,
        val intentName: String?,
        val intent: String
    ) : MqttAction

    class IntentHandlingToggle(val enabled: Boolean) : MqttAction
    class PlayAudio(val byteArray: ByteArray) : MqttAction
    class AudioOutputToggle(val enabled: Boolean) : MqttAction
    class AudioVolumeChange(val volume: Float) : MqttAction
}
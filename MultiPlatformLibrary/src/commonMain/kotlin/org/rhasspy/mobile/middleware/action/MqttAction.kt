package org.rhasspy.mobile.middleware.action

sealed interface MqttAction {
    object StartSession : MqttAction
    class EndSession(sessionId: String?) : MqttAction
    class SessionStarted(sessionId: String?) : MqttAction
    class SessionEnded(sessionId: String?) : MqttAction
    class HotWordToggle(enabled: Boolean) : MqttAction
    class HotWordDetected(hotWord: String) : MqttAction
    class StartListening(sendAudioCaptured: Boolean) : MqttAction
    class StopListening(sessionId: String?) : MqttAction
    class IntentTranscribed(sessionId: String?, text: String?) : MqttAction
    class IntentTranscribedError(sessionId: String?) : MqttAction
    class IntentRecognitionResult(sessionId: String?, intentName: String?, intent: String) : MqttAction
    class IntentHandlingToggle(enabled: Boolean) : MqttAction
    class PlayAudio(byteArray: ByteArray) : MqttAction
    class AudioOutputToggle(enabled: Boolean) : MqttAction
    class AudioVolumeChange(volume: Float) : MqttAction
}
package org.rhasspy.mobile.services.middleware

sealed class MqttEvent {
    object StartSession : MqttEvent()
    class EndSession(sessionId: String?) : MqttEvent()
    class SessionStarted(sessionId: String?) : MqttEvent()
    class SessionEnded(sessionId: String?) : MqttEvent()
    class HotWordToggle(enabled: Boolean) : MqttEvent()
    class HotWordDetected(hotWord: String) : MqttEvent()
    class StartListening(sendAudioCaptured: Boolean) : MqttEvent()
    class StopListening(sessionId: String?) : MqttEvent()
    class IntentTranscribed(sessionId: String?, text: String?) : MqttEvent()
    class IntentTranscribedError(sessionId: String?) : MqttEvent()
    class IntentRecognitionResult(sessionId: String?, intentName: String?, intent: String) : MqttEvent()
    class IntentHandlingToggle(enabled: Boolean) : MqttEvent()
    class PlayAudio(byteArray: ByteArray) : MqttEvent()
    class AudioOutputToggle(enabled: Boolean) : MqttEvent()
    class AudioVolumeChange(volume: Float) : MqttEvent()
}
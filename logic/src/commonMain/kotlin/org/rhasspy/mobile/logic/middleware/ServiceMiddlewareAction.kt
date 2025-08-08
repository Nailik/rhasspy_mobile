package org.rhasspy.mobile.logic.middleware

sealed class ServiceMiddlewareAction {

    data object PlayStopRecording : ServiceMiddlewareAction()

    data class WakeWordError(val description: String) : ServiceMiddlewareAction() {
        override fun toString(): String {
            return "${super.toString()} description: $description"
        }
    }

    class SayText(
        val text: String,
        val volume: Float?,
        val siteId: String,
        val sessionId: String?
    ) : ServiceMiddlewareAction() {
        override fun toString(): String {
            return "${super.toString()} text: $text"
        }
    }

    class Mqtt(val topic: String, val payload: ByteArray) : ServiceMiddlewareAction()

    sealed class AppSettingsServiceMiddlewareAction : ServiceMiddlewareAction() {
        class AudioOutputToggle(val enabled: Boolean, val source: Source) :
            AppSettingsServiceMiddlewareAction()

        class AudioVolumeChange(val volume: Float, val source: Source) :
            AppSettingsServiceMiddlewareAction()

        class HotWordToggle(val enabled: Boolean, val source: Source) :
            AppSettingsServiceMiddlewareAction()

        class IntentHandlingToggle(val enabled: Boolean, val source: Source) :
            AppSettingsServiceMiddlewareAction()
    }

    sealed class DialogServiceMiddlewareAction(val source: Source) : ServiceMiddlewareAction() {

        class WakeWordDetected(source: Source, val wakeWord: String) :
            DialogServiceMiddlewareAction(source) {
            override fun toString(): String {
                return "${super.toString()} wakeWord: $wakeWord"
            }
        }

        class SilenceDetected(source: Source) : DialogServiceMiddlewareAction(source)

        class StartSession(source: Source) : DialogServiceMiddlewareAction(source)

        class EndSession(source: Source) : DialogServiceMiddlewareAction(source)

        class SessionStarted(source: Source) : DialogServiceMiddlewareAction(source)

        class SessionEnded(source: Source) : DialogServiceMiddlewareAction(source)

        class StartListening(source: Source, val sendAudioCaptured: Boolean) :
            DialogServiceMiddlewareAction(source) {
            override fun toString(): String {
                return "${super.toString()} sendAudioCaptured: $sendAudioCaptured"
            }
        }

        class StopListening(source: Source) : DialogServiceMiddlewareAction(source)

        class AsrTextCaptured(source: Source, val text: String?) :
            DialogServiceMiddlewareAction(source) {
            override fun toString(): String {
                return "${super.toString()} text: $text"
            }
        }

        class AsrError(source: Source) : DialogServiceMiddlewareAction(source)
        class AsrTimeoutError(source: Source) : DialogServiceMiddlewareAction(source)

        class IntentRecognitionResult(source: Source, val intentName: String, val intent: String) :
            DialogServiceMiddlewareAction(source) {
            override fun toString(): String {
                return "${super.toString()} intentName: $intentName intent: $intent"
            }
        }

        class IntentRecognitionError(source: Source) : DialogServiceMiddlewareAction(source)
        class IntentRecognitionTimeoutError(source: Source) : DialogServiceMiddlewareAction(source)

        class PlayAudio(source: Source, val byteArray: ByteArray) :
            DialogServiceMiddlewareAction(source) {
            override fun toString(): String {
                return "${super.toString()} size: ${byteArray.size}"
            }
        }

        class StopAudioPlaying(source: Source) : DialogServiceMiddlewareAction(source)

        class PlayFinished(source: Source) : DialogServiceMiddlewareAction(source)

        override fun toString(): String {
            return "${this::class.simpleName ?: super.toString()} from $source"
        }

    }
}
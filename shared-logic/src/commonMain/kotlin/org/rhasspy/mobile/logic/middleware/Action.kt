package org.rhasspy.mobile.logic.middleware

sealed class Action {

    object PlayStopRecording : Action()

    data class WakeWordError(val description: String) : Action()

    sealed class AppSettingsAction : Action() {
        class AudioOutputToggle(val enabled: Boolean) : AppSettingsAction()
        class AudioVolumeChange(val volume: Float) : AppSettingsAction()
        class HotWordToggle(val enabled: Boolean) : AppSettingsAction()
        class IntentHandlingToggle(val enabled: Boolean) : AppSettingsAction()
    }

    sealed class DialogAction(val source: Source) : Action() {

        class WakeWordDetected(source: Source, val wakeWord: String) : DialogAction(source) {
            override fun toString(): String {
                return "${super.toString()} wakeWord: $wakeWord"
            }
        }

        class SilenceDetected(source: Source) : DialogAction(source)

        class StartSession(source: Source) : DialogAction(source)

        class EndSession(source: Source) : DialogAction(source)

        class SessionStarted(source: Source) : DialogAction(source)

        class SessionEnded(source: Source) : DialogAction(source)

        class StartListening(source: Source, val sendAudioCaptured: Boolean) :
            DialogAction(source) {
            override fun toString(): String {
                return "${super.toString()} sendAudioCaptured: $sendAudioCaptured"
            }
        }

        class StopListening(source: Source) : DialogAction(source)

        class AsrTextCaptured(source: Source, val text: String?) : DialogAction(source) {
            override fun toString(): String {
                return "${super.toString()} text: $text"
            }
        }

        class AsrError(source: Source) : DialogAction(source)

        class IntentRecognitionResult(source: Source, val intentName: String, val intent: String) :
            DialogAction(source) {
            override fun toString(): String {
                return "${super.toString()} intentName: $intentName intent: $intent"
            }
        }

        class IntentRecognitionError(source: Source) : DialogAction(source)

        class PlayAudio(source: Source, val byteArray: ByteArray) : DialogAction(source) {
            override fun toString(): String {
                return "${super.toString()} byteArray: ${byteArray.size}"
            }
        }

        class StopAudioPlaying(source: Source) : DialogAction(source)

        class PlayFinished(source: Source) : DialogAction(source)

        override fun toString(): String {
            return "${this::class.simpleName ?: super.toString()} from $source"
        }
    }
}
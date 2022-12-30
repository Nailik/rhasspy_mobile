package org.rhasspy.mobile.middleware

sealed class Action {

    object PlayRecording : Action()

    sealed class AppSettingsAction : Action() {
        class AudioOutputToggle(val enabled: Boolean) : AppSettingsAction()
        class AudioVolumeChange(val volume: Float) : AppSettingsAction()
        class HotWordToggle(val enabled: Boolean) : AppSettingsAction()
        class IntentHandlingToggle(val enabled: Boolean) : AppSettingsAction()
    }

    sealed class DialogAction(val source: Source) : Action() {

        class WakeWordDetected(source: Source, val hotWord: String) : DialogAction(source)

        class SilenceDetected(source: Source) : DialogAction(source)

        class StartSession(source: Source) : DialogAction(source)

        class EndSession(source: Source) : DialogAction(source)

        class SessionStarted(source: Source) : DialogAction(source)

        class SessionEnded(source: Source) : DialogAction(source)

        class StartListening(source: Source, val sendAudioCaptured: Boolean?) : DialogAction(source)

        class StopListening(source: Source) : DialogAction(source)

        class AsrTextCaptured(source: Source, val text: String?) : DialogAction(source)

        class AsrError(source: Source) : DialogAction(source)

        class IntentRecognitionResult(source: Source, val intentName: String?, val intent: String) : DialogAction(source)

        class IntentRecognitionError(source: Source) : DialogAction(source)

        class PlayAudio(source: Source, val byteArray: ByteArray) : DialogAction(source)

        class PlayFinished(source: Source) : DialogAction(source)

        override fun toString(): String {
            return "$this from $source"
        }
    }
}
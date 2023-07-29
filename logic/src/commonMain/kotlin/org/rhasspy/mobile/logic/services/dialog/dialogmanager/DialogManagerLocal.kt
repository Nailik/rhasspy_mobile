package org.rhasspy.mobile.logic.services.dialog.dialogmanager

import org.koin.core.component.KoinComponent
import org.rhasspy.mobile.data.service.option.IntentRecognitionOption
import org.rhasspy.mobile.data.service.option.SpeechToTextOption
import org.rhasspy.mobile.data.service.option.WakeWordOption
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.*
import org.rhasspy.mobile.logic.middleware.Source.Mqtt
import org.rhasspy.mobile.logic.services.dialog.DialogManagerState
import org.rhasspy.mobile.logic.services.dialog.DialogManagerState.*
import org.rhasspy.mobile.logic.services.dialog.IDialogManagerService
import org.rhasspy.mobile.logic.services.dialog.states.*
import org.rhasspy.mobile.settings.ConfigurationSetting

internal class DialogManagerLocal(
    private val dialogManagerService: IDialogManagerService,
    private val idleStateAction: IIdleStateAction,
    private val recordingIntentStateAction: IRecordingIntentStateAction,
    private val audioPlayingStateAction: IAudioPlayingStateAction,
    private val transcribingIntentState: ITranscribingIntentStateAction,
    private val recognizingIntentStateAction: IRecognizingIntentStateAction,
) : IDialogManager, KoinComponent {

    override fun onAction(action: DialogServiceMiddlewareAction) {
        with(dialogManagerService.currentDialogState.value) {
            if (checkIfActionIsAllowed(this, action)) {
                when (this) {
                    is IdleState               -> idleStateAction.onAction(action)
                    is RecordingIntentState    -> recordingIntentStateAction.onAction(action, this)
                    is TranscribingIntentState -> transcribingIntentState.onAction(action, this)
                    is RecognizingIntentState  -> recognizingIntentStateAction.onAction(action, this)
                    is PlayingAudioState       -> audioPlayingStateAction.onAction(action, this)
                }
            }
        }
    }

    private fun checkIfActionIsAllowed(
        state: DialogManagerState,
        action: DialogServiceMiddlewareAction
    ): Boolean {
        if (action.source !is Mqtt) return true
        //session id is not null and doesn't match
        if (state.sessionData?.sessionId != null && action.source.sessionId != state.sessionData?.sessionId) return false

        return when (action) {
            is WakeWordDetected        -> {
                val wakeWordOption = ConfigurationSetting.wakeWordOption.value
                return wakeWordOption == WakeWordOption.MQTT || wakeWordOption == WakeWordOption.Udp
            }

            is AsrError,
            is AsrTextCaptured         ->
                ConfigurationSetting.speechToTextOption.value == SpeechToTextOption.RemoteMQTT

            is IntentRecognitionError,
            is IntentRecognitionResult ->
                ConfigurationSetting.intentRecognitionOption.value == IntentRecognitionOption.RemoteMQTT

            is PlayAudio,
            is StopAudioPlaying        -> true

            else                       -> false
        }

    }

}
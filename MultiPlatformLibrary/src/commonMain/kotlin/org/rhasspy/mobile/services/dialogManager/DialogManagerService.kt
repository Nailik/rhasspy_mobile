package org.rhasspy.mobile.services.dialogManager

import com.benasher44.uuid.uuid4
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.rhasspy.mobile.data.DialogManagementOptions
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.services.hotword.HotWordService
import org.rhasspy.mobile.services.rhasspyactions.RhasspyActionsService
import org.rhasspy.mobile.middleware.Action.DialogAction
import org.rhasspy.mobile.middleware.Source

/**
 * idle
 * startedsession
 *
 */

enum class DialogManagerServiceState {
    Idle,                   //doing nothing, hotword from externally awaited
    AwaitingHotWord,        //recording HotWord
    RecordingIntent,        //recording the intent
    TranscribingIntent,     //transcribe the recorded sound to text
    RecognizingIntent,      //recognize the intent from the recorded text
    HandlingIntent,          //doing intent action
    PlayingAudio
}

class DialogManagerService : IService() {

    val params by inject<DialogManagerServiceParams>()
    val hotWordService by inject<HotWordService>()
    val rhasspyActionsService by inject<RhasspyActionsService>()
    val sessionId: String = uuid4().toString()
    private var scope = CoroutineScope(Dispatchers.Default)

    private val _currentDialogState = MutableStateFlow(DialogManagerServiceState.Idle)
    val currentDialogState = _currentDialogState.readOnly

    override fun onClose() {
        scope.cancel()
    }

    private fun DialogAction.isIgnore(): Boolean {
        //TODO ignore wrong session id
        return when (params.option) {
            DialogManagementOptions.Local -> {
                when (source) {
                    Source.HttpApi -> false
                    Source.Local -> false
                    is Source.Mqtt -> true
                }
            }
            DialogManagementOptions.RemoteMQTT -> {
                when (source) {
                    Source.HttpApi -> true
                    Source.Local -> false
                    is Source.Mqtt -> false
                }
            }
            DialogManagementOptions.Disabled -> true //TODO?
        }
    }

    fun onAction(action: DialogAction) {
        if (action.isIgnore()) return

        when (action) {
            is DialogAction.AsrError -> asrError(action)
            is DialogAction.AsrTextCaptured -> asrTextCaptured(action)
            is DialogAction.EndSession -> endSession(action)
            is DialogAction.HotWordDetected -> hotWordDetected(action)
            is DialogAction.IntentRecognitionResult -> intentRecognitionResult(action)
            is DialogAction.IntentTranscribed -> intentTranscribed(action)
            is DialogAction.IntentTranscribedError -> intentTranscribedError(action)
            is DialogAction.PlayAudio -> playAudio(action)
            is DialogAction.SessionEnded -> sessionEnded(action)
            is DialogAction.SessionStarted -> sessionStarted(action)
            is DialogAction.SilenceDetected -> silenceDetected(action)
            is DialogAction.StartListening -> startListening(action)
            is DialogAction.StartSession -> startSession(action)
            is DialogAction.StopListening -> stopListening(action)
            is DialogAction.StopSession -> stopSession(action)
        }
    }

    private fun asrError(action: DialogAction.AsrError) {
        //TODO
    }

    private fun asrTextCaptured(action: DialogAction.AsrTextCaptured) {

    }

    private fun endSession(action: DialogAction.EndSession) {

    }

    private fun hotWordDetected(action: DialogAction.HotWordDetected) {
        //   if (state != DialogManagerState.IDLE) {
        //      logger.e { "hotWordDetected$hotWord wrong state $state" }
        //     return
        // }
        //Hot word was detected
        scope.launch {
            //tell mqtt
            //TODO mqttService.hotWordDetected(hotWord)
            //disable hot word
            hotWordService.stopDetection()
        }
        //TODO send status to mqtt
        //TODO disable hot word
        //TODO indication HOTWORD (sound, visual, wakeup disblay)
        //TODO start recording voice (after sound)
    }

    private fun intentRecognitionResult(action: DialogAction.IntentRecognitionResult) {

        //intent was recognized from speech (or text)
        //TODO indication RECORDING (sound, visual)
    }

    private fun intentTranscribed(action: DialogAction.IntentTranscribed) {
        //speech was transcribed to text
        //TODO indication THINKING (visual)
        //TODO start intent recognition
    }

    private fun intentTranscribedError(action: DialogAction.IntentTranscribedError) {
        //TODO
    }

    private fun playAudio(action: DialogAction.PlayAudio) {
        //TODO
    }

    private fun sessionEnded(action: DialogAction.SessionEnded) {
        //TODO
    }

    private fun sessionStarted(action: DialogAction.SessionStarted) {

    }

    private fun silenceDetected(action: DialogAction.SilenceDetected) {

        //TODO indication THINKING (visual)
        //TODO stop recording
        //TODO start trans
    }

    private fun startListening(action: DialogAction.StartListening) {
        //start recording voice command
        //TODO disable hot word
        //TODO indication RECORDING (visual)
        //TODO start recording voice (after sound)
    }

    private fun startSession(action: DialogAction.StartSession) {

    }

    private fun stopListening(action: DialogAction.StopListening) {
        //stop recording voice command
        //TODO stop recording
        //TODO tell mqtt asr system to stop listening (if necessary)
        //TODO indication THINKING (visual)
    }

    private fun stopSession(action: DialogAction.StopSession) {

    }

}
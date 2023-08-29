package org.rhasspy.mobile.logic.domains.dialog.dialogmanager.disabled

import co.touchlab.kermit.Logger
import org.rhasspy.mobile.logic.domains.audioplaying.IAudioPlayingService
import org.rhasspy.mobile.logic.domains.dialog.IDialogManagerService
import org.rhasspy.mobile.logic.domains.dialog.dialogmanager.IDialogManager
import org.rhasspy.mobile.logic.domains.intenthandling.IIntentHandlingService
import org.rhasspy.mobile.logic.domains.intentrecognition.IIntentRecognitionService
import org.rhasspy.mobile.logic.domains.speechtotext.ISpeechToTextService
import org.rhasspy.mobile.logic.domains.wakeword.IWakeWordService
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.*
import org.rhasspy.mobile.logic.middleware.Source.Mqtt
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource.Data

class DialogManagerDisabled(
    private val audioPlayingService: IAudioPlayingService,
    private val speechToTextService: ISpeechToTextService,
    private val intentRecognitionService: IIntentRecognitionService,
    private val intentHandlingService: IIntentHandlingService,
    private val dialogManagerService: IDialogManagerService,
    private val wakeWordService: IWakeWordService,
) : IDialogManager {

    private val logger = Logger.withTag("DialogManagerDisabled")

    fun onInit() {
        wakeWordService.startDetection()
    }

    override fun onAction(action: DialogServiceMiddlewareAction) {
        logger.d { "action $action" }
        dialogManagerService.addToHistory(action)
        when (action) {
            is AsrError                      -> Unit
            is AsrTextCaptured               -> intentRecognitionService.recognizeIntent((action.source as? Mqtt?)?.sessionId ?: "", action.text ?: "")
            is EndSession                    -> Unit
            is IntentRecognitionError        -> Unit
            is IntentRecognitionResult       -> intentHandlingService.intentHandling(action.intentName, action.intent)
            is PlayAudio                     -> @Suppress("DEPRECATION") audioPlayingService.playAudio(Data(action.byteArray))
            is PlayFinished                  -> Unit
            is SessionEnded                  -> Unit
            is SessionStarted                -> Unit
            is SilenceDetected               -> speechToTextService.endSpeechToText((action.source as? Mqtt?)?.sessionId ?: "", action.source is Mqtt)
            is StartListening                -> speechToTextService.startSpeechToText((action.source as? Mqtt?)?.sessionId ?: "", action.source is Mqtt)
            is StartSession                  -> Unit
            is StopAudioPlaying              -> audioPlayingService.stopPlayAudio()
            is StopListening                 -> speechToTextService.endSpeechToText((action.source as? Mqtt?)?.sessionId ?: "", action.source is Mqtt)
            is WakeWordDetected              -> Unit
            is AsrTimeoutError               -> Unit
            is IntentRecognitionTimeoutError -> Unit
        }
    }

}
package org.rhasspy.mobile.logic.services.dialog.dialogmanager.disabled

import co.touchlab.kermit.Logger
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.*
import org.rhasspy.mobile.logic.middleware.Source.Mqtt
import org.rhasspy.mobile.logic.services.audioplaying.IAudioPlayingService
import org.rhasspy.mobile.logic.services.dialog.dialogmanager.IDialogManager
import org.rhasspy.mobile.logic.services.intenthandling.IIntentHandlingService
import org.rhasspy.mobile.logic.services.intentrecognition.IIntentRecognitionService
import org.rhasspy.mobile.logic.services.speechtotext.ISpeechToTextService
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource.Data

class DialogManagerDisabled(
    private val audioPlayingService: IAudioPlayingService,
    private val speechToTextService: ISpeechToTextService,
    private val intentRecognitionService: IIntentRecognitionService,
    private val intentHandlingService: IIntentHandlingService,
) : IDialogManager {

    private val logger = Logger.withTag("DialogManagerDisabled")

    override fun onAction(action: DialogServiceMiddlewareAction) {
        logger.d { "action $action" }
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
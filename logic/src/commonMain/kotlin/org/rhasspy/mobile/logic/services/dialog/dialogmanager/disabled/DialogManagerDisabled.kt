package org.rhasspy.mobile.logic.services.dialog.dialogmanager.disabled

import co.touchlab.kermit.Logger
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.AsrError
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.AsrTextCaptured
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.AsrTimeoutError
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.EndSession
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.IntentRecognitionError
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.IntentRecognitionResult
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.IntentRecognitionTimeoutError
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.PlayAudio
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.PlayFinished
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.SessionEnded
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.SessionStarted
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.SilenceDetected
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.StartListening
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.StartSession
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.StopAudioPlaying
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.StopListening
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.WakeWordDetected
import org.rhasspy.mobile.logic.middleware.Source.Mqtt
import org.rhasspy.mobile.logic.services.audioplaying.IAudioPlayingService
import org.rhasspy.mobile.logic.services.dialog.IDialogManagerService
import org.rhasspy.mobile.logic.services.dialog.dialogmanager.IDialogManager
import org.rhasspy.mobile.logic.services.intenthandling.IIntentHandlingService
import org.rhasspy.mobile.logic.services.intentrecognition.IIntentRecognitionService
import org.rhasspy.mobile.logic.services.speechtotext.ISpeechToTextService
import org.rhasspy.mobile.logic.services.wakeword.IWakeWordService
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
            is AsrError -> Unit
            is AsrTextCaptured -> intentRecognitionService.recognizeIntent(
                (action.source as? Mqtt?)?.sessionId ?: "", action.text ?: ""
            )

            is EndSession -> Unit
            is IntentRecognitionError -> Unit
            is IntentRecognitionResult -> intentHandlingService.intentHandling(
                action.intentName,
                action.intent
            )

            is PlayAudio -> @Suppress("DEPRECATION") audioPlayingService.playAudio(Data(action.byteArray))
            is PlayFinished -> Unit
            is SessionEnded -> Unit
            is SessionStarted -> Unit
            is SilenceDetected -> speechToTextService.endSpeechToText(
                (action.source as? Mqtt?)?.sessionId ?: "", action.source is Mqtt
            )

            is StartListening -> speechToTextService.startSpeechToText(
                (action.source as? Mqtt?)?.sessionId ?: "", action.source is Mqtt
            )

            is StartSession -> Unit
            is StopAudioPlaying -> audioPlayingService.stopPlayAudio()
            is StopListening -> speechToTextService.endSpeechToText(
                (action.source as? Mqtt?)?.sessionId ?: "", action.source is Mqtt
            )

            is WakeWordDetected -> Unit
            is AsrTimeoutError -> Unit
            is IntentRecognitionTimeoutError -> Unit
        }
    }

}
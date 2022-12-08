package org.rhasspy.mobile.services.dialogManager

import com.benasher44.uuid.uuid4
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import org.rhasspy.mobile.data.DialogManagementOptions
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.IService
import org.rhasspy.mobile.services.hotword.HotWordService
import org.rhasspy.mobile.services.rhasspyactions.RhasspyActionsService

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

abstract class IDialogManagerService : IService() {

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

    companion object : KoinComponent {
        fun getService(): IDialogManagerService {
            val params = get<DialogManagerServiceParams>()
            return when (params.option) {
                DialogManagementOptions.Local -> DialogManagerLocalService()
                DialogManagementOptions.RemoteMQTT -> DialogManagerMqttService()
                DialogManagementOptions.Disabled -> DialogManagerDisabledService()
            }
        }
    }

    //TODO states and go to next state etc

    //aktionen:

    //

    abstract fun startSessionMqtt()

    abstract fun endSessionMqtt(sessionId: String?)

    abstract fun startedSessionMqtt(sessionId: String?)

    abstract fun sessionEndedMqtt(sessionId: String?)

    abstract fun startListeningMqtt(sessionId: String?, isSendAudioCaptured: Boolean)

    abstract fun stopListeningMqtt(sessionId: String?)

    abstract fun intentTranscribedMqtt(sessionId: String?, text: String?)

    abstract fun hotWordDetectedMqtt(hotWord: String)

    //after recognizeIntent
    abstract fun intentTranscribedErrorMqtt(sessionId: String?)

    //after recognizeIntent
    abstract fun intentNotRecognizedMqtt(sessionId: String?)

    abstract fun intentRecognizedMqtt(sessionId: String?, intentName: String?, intent: String)

    //**********from webserver

    abstract fun listenForCommandWebServer()

    abstract fun startRecordingWebServer()

    abstract fun stopRecordingWebServer()

    //local
    abstract fun hotWordDetectedLocal(hotWord: String)

    abstract fun silenceDetectedLocal()


}

enum class DialogManagerState() {
    IDLE
}
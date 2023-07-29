package org.rhasspy.mobile.logic.middleware

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okio.Path
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.*
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.AppSettingsServiceMiddlewareAction.*
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.*
import org.rhasspy.mobile.logic.middleware.Source.Local
import org.rhasspy.mobile.logic.services.dialog.DialogManagerState.IdleState
import org.rhasspy.mobile.logic.services.dialog.DialogManagerState.RecordingIntentState
import org.rhasspy.mobile.logic.services.dialog.IDialogManagerService
import org.rhasspy.mobile.logic.services.localaudio.ILocalAudioService
import org.rhasspy.mobile.logic.services.mqtt.IMqttService
import org.rhasspy.mobile.logic.services.settings.IAppSettingsService
import org.rhasspy.mobile.logic.services.speechtotext.ISpeechToTextService
import org.rhasspy.mobile.logic.services.texttospeech.ITextToSpeechService
import org.rhasspy.mobile.logic.services.wakeword.IWakeWordService
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource
import org.rhasspy.mobile.platformspecific.combineState
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.AppSetting

interface IServiceMiddleware {

    val isUserActionEnabled: StateFlow<Boolean>
    val isPlayingRecording: StateFlow<Boolean>
    val isPlayingRecordingEnabled: StateFlow<Boolean>

    fun action(serviceMiddlewareAction: ServiceMiddlewareAction)
    fun userSessionClick()
    fun getRecordedFile(): Path

}

/**
 * handles ALL INCOMING events
 */
internal class ServiceMiddleware(
    private val dialogManagerService: IDialogManagerService,
    private val speechToTextService: ISpeechToTextService,
    private val textToSpeechService: ITextToSpeechService,
    private val appSettingsService: IAppSettingsService,
    private val localAudioService: ILocalAudioService,
    private val mqttService: IMqttService,
    private val wakeWordService: IWakeWordService
) : IServiceMiddleware {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var currentJob: Job? = null

    private val _isPlayingRecording = MutableStateFlow(false)
    override val isPlayingRecording = _isPlayingRecording.readOnly
    override val isPlayingRecordingEnabled = combineState(
        _isPlayingRecording,
        dialogManagerService.currentDialogState
    ) { playing, state ->
        playing || (state is IdleState)
    }

    override val isUserActionEnabled = combineState(
        _isPlayingRecording,
        dialogManagerService.currentDialogState
    ) { playingRecording, dialogState ->
        !playingRecording && (dialogState is IdleState || dialogState is RecordingIntentState)
    }
    private var shouldResumeHotWordService = false

    override fun action(serviceMiddlewareAction: ServiceMiddlewareAction) {
        val previousJob = currentJob
        currentJob = coroutineScope.launch {
            previousJob?.join()

            when (serviceMiddlewareAction) {
                is PlayStopRecording                  -> playStopRecordingAction()
                is WakeWordError                      -> mqttService.wakeWordError(serviceMiddlewareAction.description)
                is AppSettingsServiceMiddlewareAction -> appSettingsAction(serviceMiddlewareAction)
                is SayText                            -> textToSpeechService.textToSpeech("", serviceMiddlewareAction.text)
                is DialogServiceMiddlewareAction      -> dialogManagerService.onAction(serviceMiddlewareAction)
                is Mqtt                               -> mqttService.onMessageReceived(serviceMiddlewareAction.topic, serviceMiddlewareAction.payload)
            }
        }
    }

    private fun appSettingsAction(action: AppSettingsServiceMiddlewareAction) {
        when (action) {
            is AudioOutputToggle    -> appSettingsService.audioOutputToggle(action.enabled)
            is AudioVolumeChange    -> appSettingsService.setAudioVolume(action.volume)
            is HotWordToggle        -> appSettingsService.hotWordToggle(action.enabled)
            is IntentHandlingToggle -> appSettingsService.intentHandlingToggle(action.enabled)
        }
    }

    private suspend fun playStopRecordingAction() {
        if (_isPlayingRecording.value) {
            _isPlayingRecording.value = false
            if (shouldResumeHotWordService) {
                action(HotWordToggle(true))
            }
            action(PlayFinished(Local))
        } else {
            if (dialogManagerService.currentDialogState.value is IdleState) {
                _isPlayingRecording.value = true
                shouldResumeHotWordService = AppSetting.isHotWordEnabled.value
                action(HotWordToggle(false))
                //suspend coroutine
                localAudioService.playAudio(AudioSource.File(speechToTextService.speechToTextAudioFile))
                //resumes when play finished
                if (_isPlayingRecording.value) {
                    action(PlayStopRecording)
                }
            }
        }
    }

    override fun userSessionClick() {
        when (dialogManagerService.currentDialogState.value) {
            is IdleState            -> action(WakeWordDetected(Local, "manual"))
            is RecordingIntentState -> action(StopListening(Local))
            else                    -> Unit
        }
    }

    override fun getRecordedFile(): Path = speechToTextService.speechToTextAudioFile

}
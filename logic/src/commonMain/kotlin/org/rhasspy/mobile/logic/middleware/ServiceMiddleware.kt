package org.rhasspy.mobile.logic.middleware

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import okio.Path
import org.rhasspy.mobile.data.service.option.*
import org.rhasspy.mobile.logic.connections.mqtt.IMqttConnection
import org.rhasspy.mobile.logic.domains.dialog.DialogManagerState.IdleState
import org.rhasspy.mobile.logic.domains.dialog.DialogManagerState.SessionState.RecordingIntentState
import org.rhasspy.mobile.logic.domains.dialog.IDialogManagerService
import org.rhasspy.mobile.logic.domains.speechtotext.ISpeechToTextService
import org.rhasspy.mobile.logic.domains.texttospeech.ITextToSpeechService
import org.rhasspy.mobile.logic.local.localaudio.ILocalAudioService
import org.rhasspy.mobile.logic.local.settings.IAppSettingsService
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.*
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.AppSettingsServiceMiddlewareAction.*
import org.rhasspy.mobile.logic.middleware.ServiceMiddlewareAction.DialogServiceMiddlewareAction.*
import org.rhasspy.mobile.logic.middleware.Source.Local
import org.rhasspy.mobile.platformspecific.audioplayer.AudioSource
import org.rhasspy.mobile.platformspecific.combineState
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.settings.ConfigurationSetting

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
    private val mqttService: IMqttConnection
) : IServiceMiddleware {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var currentJob: Job? = null
    private var awaitMqttConnected: Job? = null

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

    override fun action(serviceMiddlewareAction: ServiceMiddlewareAction) {
        val previousJob = currentJob
        currentJob = coroutineScope.launch {
            previousJob?.join()

            when (serviceMiddlewareAction) {
                is PlayStopRecording                  -> playStopRecordingAction()
                is WakeWordError                      -> mqttService.wakeWordError(serviceMiddlewareAction.description)
                is AppSettingsServiceMiddlewareAction -> appSettingsAction(serviceMiddlewareAction)
                is SayText                            ->
                    textToSpeechService.textToSpeech(
                        text = serviceMiddlewareAction.text,
                        volume = serviceMiddlewareAction.volume,
                        siteId = serviceMiddlewareAction.siteId,
                        sessionId = serviceMiddlewareAction.sessionId
                    )

                is DialogServiceMiddlewareAction      -> dialogManagerService.onAction(serviceMiddlewareAction)
                is Mqtt                               -> mqttService.onMessageReceived(serviceMiddlewareAction.topic, serviceMiddlewareAction.payload)
            }
        }
    }

    private fun appSettingsAction(action: AppSettingsServiceMiddlewareAction) {
        when (action) {
            is AudioOutputToggle    -> appSettingsService.audioOutputToggle(action.enabled, action.source)
            is AudioVolumeChange    -> appSettingsService.setAudioVolume(action.volume, action.source)
            is HotWordToggle        -> appSettingsService.hotWordToggle(action.enabled, action.source)
            is IntentHandlingToggle -> appSettingsService.intentHandlingToggle(action.enabled, action.source)
        }
    }

    private fun playStopRecordingAction() {
        if (_isPlayingRecording.value) {
            _isPlayingRecording.value = false
            action(PlayFinished(Local))
        } else {
            if (dialogManagerService.currentDialogState.value is IdleState) {
                _isPlayingRecording.value = true
                //suspend coroutine
                localAudioService.playAudio(AudioSource.File(speechToTextService.speechToTextAudioFile)) {
                    //resumes when play finished
                    if (_isPlayingRecording.value) {
                        action(PlayStopRecording)
                    }
                }

            }
        }
    }

    override fun userSessionClick() {
        when (dialogManagerService.currentDialogState.value) {
            is IdleState            -> {
                if (isAnyServiceUsingMqtt() && !mqttService.isHasStarted.value) {
                    //await for mqtt to be started (connected and subscribed to topics) in the case that any service is using mqtt
                    //this is necessary to ensure all topics are correctly being sent and consumed
                    awaitMqttConnected?.cancel()
                    awaitMqttConnected = coroutineScope.launch {
                        mqttService.isHasStarted.first { it }
                        action(WakeWordDetected(Local, "manual"))
                    }
                } else {
                    action(WakeWordDetected(Local, "manual"))
                }
            }

            is RecordingIntentState -> action(StopListening(Local))
            else                    -> Unit
        }
    }

    override fun getRecordedFile(): Path = speechToTextService.speechToTextAudioFile

    private fun isAnyServiceUsingMqtt(): Boolean {
        return ConfigurationSetting.audioPlayingOption.value == AudioPlayingOption.Rhasspy2HermesMQTT ||
                ConfigurationSetting.dialogManagementOption.value == DialogManagementOption.Rhasspy2HermesMQTT ||
                ConfigurationSetting.intentRecognitionOption.value == IntentRecognitionOption.Rhasspy2HermesMQTT ||
                ConfigurationSetting.speechToTextOption.value == SpeechToTextOption.Rhasspy2HermesMQTT ||
                ConfigurationSetting.textToSpeechOption.value == TextToSpeechOption.Rhasspy2HermesMQTT
    }

}
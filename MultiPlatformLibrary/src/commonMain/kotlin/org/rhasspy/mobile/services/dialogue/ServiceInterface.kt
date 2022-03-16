package org.rhasspy.mobile.services.dialogue

import co.touchlab.kermit.Logger
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.addCloseableObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.launch
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.data.*
import org.rhasspy.mobile.services.*
import org.rhasspy.mobile.services.http.HttpServer
import org.rhasspy.mobile.services.native.AudioPlayer
import org.rhasspy.mobile.services.native.NativeIndication
import org.rhasspy.mobile.services.native.NativeLocalWakeWordService
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.settings.ConfigurationSettings
import org.rhasspy.mobile.viewModels.GlobalData
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object ServiceInterface {
    private val logger = Logger.withTag(this::class.simpleName!!)

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    //toggle on off from mqtt or http service
    private val wakeWordEnabled = MutableLiveData(true)

    //collect audio for mqtt service
    private var collectAudioJob: Job? = null


    init {
        wakeWordEnabled.addObserver {
            if (it) {
                startWakeWordService()
            } else {
                NativeLocalWakeWordService.stop()
            }
        }
    }


    fun onAction(inputAction: DialogueInputAction) {

        coroutineScope.launch {

            when (ConfigurationSettings.dialogueManagementOption.data) {
                DialogueManagementOptions.Local -> {
                    //do the next thing according to previous action
                    DialogueManagementLocal.doAction(inputAction)
                }
                DialogueManagementOptions.RemoteMQTT -> {
                    //listen to mqtt and then decide what to do
                    DialogueManagementMQTT.doAction(inputAction)
                }
                DialogueManagementOptions.Disabled -> {
                    //do nothing at all
                    logger.v { "no dialog management for action ${inputAction.name}" }
                }
            }
        }

    }


    fun wakeWordDetected() {
        if (ConfigurationSettings.isMQTTEnabled.data) {
            MqttService.wakeWordDetected()
        }

        //local -> directly start,
        //mqtt -> wait for mqtt ?

        //   startRecording()
    }


    /**
     * Text to Speak requested
     *
     * HTTP:
     * - calls service to generate audio data
     * - plays audio data afterwards
     *
     * MQTT:
     * - calls default site to speak text
     * - the remote default site has to output it on there audio output
     */
    fun textToSpeak(text: String) {

        logger.d { "textToSpeak $text" }

        coroutineScope.launch {

            when (ConfigurationSettings.textToSpeechOption.data) {
                TextToSpeechOptions.RemoteHTTP -> HttpService.textToSpeech(text)?.also {
                    playAudio(it)
                }
                TextToSpeechOptions.RemoteMQTT -> MqttService.textToSpeak(text)?.also {
                    logger.d { "textToSpeak finished" }
                } ?: run {
                    logger.w { "textToSpeak timeout" }
                }
                TextToSpeechOptions.Disabled -> logger.d { "textToSpeak disabled" }
            }
        }
    }

    /**
     * Intent Recognition requested
     *
     * HTTP:
     * - calls service to recognize intent from text
     * - if IntentHandlingOptions.WithRecognition is set the remote site will also automatically handle the intent
     * - else [intentHandling] will be called with received data
     *
     * MQTT:
     * - calls default site to recognize intent
     * - then [intentHandling] will be called with received data
     */
    fun intentRecognition(text: String) {

        logger.d { "intentRecognition $text" }

        coroutineScope.launch {

            when (ConfigurationSettings.intentRecognitionOption.data) {
                IntentRecognitionOptions.RemoteHTTP -> {
                    val handleDirectly = ConfigurationSettings.intentHandlingOption.data == IntentHandlingOptions.WithRecognition
                    HttpService.intentRecognition(text, handleDirectly)?.also {
                        if (!handleDirectly) {
                            intentHandling(it)
                        }
                    }
                }
                IntentRecognitionOptions.RemoteMQTT -> MqttService.intentRecognition(text)?.also {
                    intentHandling(it.payload.toString())
                } ?: run {
                    logger.w { "intentRecognition timeout" }
                }
                IntentRecognitionOptions.Disabled -> logger.d { "intentRecognition disabled" }
            }
        }
    }

    /**
     * Play Audio (Wav Data)
     *
     * Local:
     * - play audio with volume set
     *
     * HTTP:
     * - calls service to play audio with wav data
     *
     * MQTT:
     * - calls default site to play audio
     */
    fun playAudio(data: ByteArray) {

        logger.d { "playAudio ${data.size}" }

        coroutineScope.launch {

            when (ConfigurationSettings.audioPlayingOption.data) {
                AudioPlayingOptions.Local -> AudioPlayer.playData(data)
                AudioPlayingOptions.RemoteHTTP -> HttpService.playWav(data)
                AudioPlayingOptions.RemoteMQTT -> MqttService.playWav(data)?.also {
                    logger.d { "playAudio finished" }
                } ?: run {
                    logger.w { "playAudio timeout" }
                }
                AudioPlayingOptions.Disabled -> logger.d { "audioPlaying disabled" }
            }

        }
    }


    /**
     * Play Audio (Wav Data)
     *
     * HomeAssistant:
     * TODO
     *
     * HTTP:
     * - calls service to handle intent
     *
     * WithRecognition
     * - should only be used with HTTP text to intent
     * - remote text to intent will also handle it
     */
    private fun intentHandling(intent: String) {
        logger.d { "intentRecognized $intent" }

        coroutineScope.launch {

            when (ConfigurationSettings.intentHandlingOption.data) {
                IntentHandlingOptions.HomeAssistant -> TODO()
                IntentHandlingOptions.RemoteHTTP -> HttpService.intentHandling(intent)
                IntentHandlingOptions.WithRecognition -> logger.e { "intentHandling with recognition was not used" }
                IntentHandlingOptions.Disabled -> logger.d { "intentHandling disabled" }
            }

        }
    }

    /**
     * Speech to Text (Wav Data)
     *
     * HTTP:
     * - calls service to translate speech to text, then handles the intent if dialogue manager is set to local
     *
     * RemoteMQTT
     * - sends
     * - todo let rhasspy determine silence
     */
    private fun speechToText() {
        logger.d { "speechToText Started" }

        coroutineScope.launch {

            when (ConfigurationSettings.speechToTextOption.data) {
                //wait for finish -> then publish all
                SpeechToTextOptions.RemoteHTTP -> {
                    RecordingService.status.addCloseableObserver {
                        if (it) {

                            coroutineScope.launch {
                                HttpService.speechToText(getLatestRecording())?.also { data ->
                                    if (ConfigurationSettings.dialogueManagementOption.data == DialogueManagementOptions.Local) {
                                        intentRecognition(data)
                                    }
                                }
                            }

                        }
                    }
                }
                //publish in junks
                SpeechToTextOptions.RemoteMQTT -> {

                    MqttService.startListening(sessionId!!)

                    collectAudioJob = coroutineScope.launch {
                        RecordingService.sharedFlow.collectIndexed { _, data ->
                            MqttService.audioSessionFrame(sessionId!!, data)
                        }
                    }

                    collectAudioJob?.join()

                    MqttService.stopListening(sessionId!!)?.also {
                        logger.d { "speechToText ${it.payload}" }
                    } ?: run {
                        logger.w { "speechToText timeout" }
                    }

                }
                SpeechToTextOptions.Disabled -> logger.d { "speechToText disabled" }
            }

        }


    }


    /**
     * shows indication and then starts recording
     */
    fun startRecording() {
        logger.d { "startRecording" }

        showIndication()
        RecordingService.startRecording()
    }

    fun stopRecording(uuid: String? = sessionId?.toString()) {
        uuid?.also { id ->
            sessionId?.also {
                if (id == it.toString()) {
                    logger.d { "stopRecording" }

                    stopIndication()
                    RecordingService.stopRecording()

                    if (ConfigurationSettings.isMQTTEnabled.data) {
                        MqttService.sessionEnded(it)
                        MqttService.toggleOffWakeWord()
                    } else {
                        setWakeWordEnabled(true) //TODO external made off
                    }

                    sessionId = null
                }
            } ?: run {
                logger.w { "no session running" }
            }
        } ?: run {
            logger.w { "sessionId missing" }
        }
    }

    fun setVolume(volume: Float) {
        AppSettings.volume.data = volume
    }


    fun textCaptured() {
        stopRecording()
    }

    fun setWakeWordEnabled(enabled: Boolean) {
        logger.d { "setWakeWordEnabled $enabled" }

        wakeWordEnabled.value = enabled
    }

    fun setAudioOutputEnabled(enabled: Boolean) {
        logger.d { "setAudioOutputEnabled $enabled" }

        AudioPlayer.setEnabled(enabled)
    }

    fun showIndication() {
        if (AppSettings.isWakeWordSoundIndication.data) {
            NativeIndication.playAudio(MR.files.etc_wav_beep_hi)
        }

        if (AppSettings.isBackgroundWakeWordDetectionTurnOnDisplay.data) {
            NativeIndication.wakeUpScreen()
        }

        if (AppSettings.isWakeWordLightIndication.data) {
            NativeIndication.showIndication()
        }
    }

    fun stopIndication() {
        logger.d { "stopIndication" }

        NativeIndication.closeIndicationOverOtherApps()
        NativeIndication.releaseWakeUp()
    }


    /**
     * Start services according to settings
     */
    fun startServices() {
        logger.d { "startServices" }

        startWakeWordService()
        HttpServer.start()
        MqttService.start()
    }

    /**
     * Stop services according to settings
     */
    fun stopServices() {
        logger.d { "stopServices" }

        NativeLocalWakeWordService.stop()
        HttpServer.stop()
        MqttService.stop()
    }

    /**
     * Reload services according to settings
     * via start and stop
     */
    fun reloadServices() {
        logger.d { "reloadServices" }

        stopServices()
        startServices()
    }

    fun startWakeWordService() {
        if (ConfigurationSettings.wakeWordOption.data == WakeWordOption.Porcupine &&
            ConfigurationSettings.wakeWordAccessToken.data.isNotEmpty()
        ) {
            NativeLocalWakeWordService.start()
        }
    }

    fun getLatestRecording(): ByteArray {
        return RecordingService.getLatestRecording()
    }

    fun saveChanges() {
        GlobalData.saveAllChanges()
        ForegroundService.action(Action.Reload)
    }

    fun resetChanges() {
        GlobalData.resetChanges()
    }

    fun toggleRecording() {
        if (RecordingService.status.value) {
            stopRecording()
        } else {
            wakeWordDetected()
        }
    }

    fun playRecording() {
        AudioPlayer.playData(getLatestRecording())
    }

}
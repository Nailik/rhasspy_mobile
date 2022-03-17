package org.rhasspy.mobile.services.dialogue

import co.touchlab.kermit.Logger
import com.benasher44.uuid.Uuid
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.addCloseableObserver
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.launch
import org.rhasspy.mobile.data.*
import org.rhasspy.mobile.services.HttpService
import org.rhasspy.mobile.services.MqttService
import org.rhasspy.mobile.services.RecordingService
import org.rhasspy.mobile.services.RecordingService.addWavHeader
import org.rhasspy.mobile.services.native.AudioPlayer
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.settings.ConfigurationSettings

object DialogueManagement {
    private val logger = Logger.withTag(this::class.simpleName!!)

    //toggle on off from mqtt or http service
    private val wakeWordEnabled = MutableLiveData(true)


    /**
     * not null if there is currently a session
     */
    var sessionId: Uuid? = null
        private set


    fun startSession() {

        when(ConfigurationSettings.dialogueManagementOption.data){
            DialogueManagementOptions.Local -> TODO()
            DialogueManagementOptions.RemoteMQTT -> TODO()
        }

        ServiceInterface.coroutineScope.launch {


            //when local dialog management is enabled do the next thing


            //when mqtt is enabled send the mqtt events

            //when mqtt dialogue management receive session started an sessionEnded

            when (ConfigurationSettings.dialogueManagementOption.data) {
                DialogueManagementOptions.Local -> {
                    //do the next thing according to previous action
                    DialogueManagementLocal.doAction(inputAction)
                }
                DialogueManagementOptions.RemoteMQTT -> {
                    //only send action to MQTT broker, hope that he does tell you what to do
                    DialogueManagementMQTT.doAction(inputAction)
                }
            }
        }

    }

    fun sessionStarted()

    fun endSession(sessionId: String) {

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

        ServiceInterface.logger.d { "textToSpeak $text" }

        ServiceInterface.coroutineScope.launch {

            when (ConfigurationSettings.textToSpeechOption.data) {
                TextToSpeechOptions.RemoteHTTP -> HttpService.textToSpeech(text)?.also {
                    playAudio(it)
                }
                TextToSpeechOptions.RemoteMQTT -> MqttService.textToSpeak(text)?.also {
                    ServiceInterface.logger.d { "textToSpeak finished" }
                } ?: run {
                    ServiceInterface.logger.w { "textToSpeak timeout" }
                }
                TextToSpeechOptions.Disabled -> ServiceInterface.logger.d { "textToSpeak disabled" }
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

        ServiceInterface.logger.d { "intentRecognition $text" }

        ServiceInterface.coroutineScope.launch {

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
                    ServiceInterface.logger.w { "intentRecognition timeout" }
                }
                IntentRecognitionOptions.Disabled -> ServiceInterface.logger.d { "intentRecognition disabled" }
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

        ServiceInterface.logger.d { "playAudio ${data.size}" }

        ServiceInterface.coroutineScope.launch {

            when (ConfigurationSettings.audioPlayingOption.data) {
                AudioPlayingOptions.Local -> AudioPlayer.playData(data)
                AudioPlayingOptions.RemoteHTTP -> HttpService.playWav(data)
                AudioPlayingOptions.RemoteMQTT -> MqttService.playWav(data)?.also {
                    ServiceInterface.logger.d { "playAudio finished" }
                } ?: run {
                    ServiceInterface.logger.w { "playAudio timeout" }
                }
                AudioPlayingOptions.Disabled -> ServiceInterface.logger.d { "audioPlaying disabled" }
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
        ServiceInterface.logger.d { "intentRecognized $intent" }

        ServiceInterface.coroutineScope.launch {

            when (ConfigurationSettings.intentHandlingOption.data) {
                IntentHandlingOptions.HomeAssistant -> TODO()
                IntentHandlingOptions.RemoteHTTP -> HttpService.intentHandling(intent)
                IntentHandlingOptions.WithRecognition -> ServiceInterface.logger.e { "intentHandling with recognition was not used" }
                IntentHandlingOptions.Disabled -> ServiceInterface.logger.d { "intentHandling disabled" }
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
        ServiceInterface.logger.d { "speechToText Started" }

        ServiceInterface.coroutineScope.launch {

            when (ConfigurationSettings.speechToTextOption.data) {
                //wait for finish -> then publish all
                SpeechToTextOptions.RemoteHTTP -> {
                    RecordingService.status.addCloseableObserver {
                        if (it) {

                            ServiceInterface.coroutineScope.launch {
                                HttpService.speechToText(ServiceInterface.getLatestRecording())?.also { data ->
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

                    ServiceInterface.collectAudioJob = ServiceInterface.coroutineScope.launch {
                        RecordingService.sharedFlow.collectIndexed { _, byteData ->
                            MqttService.audioSessionFrame(sessionId!!, byteData.addWavHeader())
                        }
                    }

                    ServiceInterface.collectAudioJob?.join()

                    MqttService.stopListening(sessionId!!)?.also {
                        ServiceInterface.logger.d { "speechToText ${it.payload}" }
                    } ?: run {
                        ServiceInterface.logger.w { "speechToText timeout" }
                    }

                }
                SpeechToTextOptions.Disabled -> ServiceInterface.logger.d { "speechToText disabled" }
            }

        }


    }


    /**
     * shows indication and then starts recording
     */
    fun startRecording() {
        ServiceInterface.logger.d { "startRecording" }

        ServiceInterface.showIndication()
        RecordingService.startRecording()
    }

    fun stopRecording(uuid: String? = sessionId?.toString()) {
        uuid?.also { id ->
            sessionId?.also {
                if (id == it.toString()) {
                    ServiceInterface.logger.d { "stopRecording" }

                    ServiceInterface.stopIndication()
                    RecordingService.stopRecording()

                    if (ConfigurationSettings.isMQTTEnabled.data) {
                        MqttService.sessionEnded(it)
                        MqttService.toggleOffWakeWord()
                    } else {
                        ServiceInterface.setWakeWordEnabled(true) //TODO external made off
                    }

                    sessionId = null
                }
            } ?: run {
                ServiceInterface.logger.w { "no session running" }
            }
        } ?: run {
            ServiceInterface.logger.w { "sessionId missing" }
        }
    }


    fun setWakeWordEnabled(enabled: Boolean) {
        ServiceInterface.logger.d { "setWakeWordEnabled $enabled" }

        wakeWordEnabled.value = enabled
        /*

            if (it) {
                startWakeWordService()
            } else {
                NativeLocalWakeWordService.stop()
            }
         */
    }

    fun wakeWordDetected(){

    }

    fun playFinished(){

    }


    fun setAudioOutputEnabled(enabled: Boolean) {
        ServiceInterface.logger.d { "setAudioOutputEnabled $enabled" }

        AudioPlayer.setEnabled(enabled)
    }

    fun setVolume(volume: Float) {
        AppSettings.volume.data = volume
    }

}
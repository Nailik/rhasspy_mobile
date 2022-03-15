package org.rhasspy.mobile.services

import co.touchlab.kermit.Logger
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.data.*
import org.rhasspy.mobile.services.http.HttpServer
import org.rhasspy.mobile.services.native.AudioPlayer
import org.rhasspy.mobile.services.native.NativeIndication
import org.rhasspy.mobile.services.native.NativeLocalWakeWordService
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.settings.ConfigurationSettings
import org.rhasspy.mobile.viewModels.GlobalData

object ServiceInterface {
    private val logger = Logger.withTag(this::class.simpleName!!)

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    //toggle on off from mqtt or http service
    private val listenForWakeEnabled = MutableLiveData(true)

    init {
        listenForWakeEnabled.addObserver {
            if (it) {
                startWakeWordService()
            } else {
                NativeLocalWakeWordService.stop()
            }
        }
    }

    fun wakeWordDetected(){
        startRecording()
    }

    fun textToSpeak(text: String) {

        logger.d { "textToSpeak $text" }

        coroutineScope.launch {

            when (ConfigurationSettings.textToSpeechOption.data) {
                TextToSpeechOptions.RemoteHTTP -> HttpService.textToSpeech(text)
                TextToSpeechOptions.RemoteMQTT -> MqttService.textToSpeak(text)
                TextToSpeechOptions.Disabled -> logger.d { "textToSpeak disabled" }
            }

        }
    }

    fun intentRecognition(text: String) {

        logger.d { "intentRecognition $text" }

        coroutineScope.launch {

            when (ConfigurationSettings.intentRecognitionOption.data) {
                IntentRecognitionOptions.RemoteHTTP -> HttpService.intentRecognition(text)
                IntentRecognitionOptions.RemoteMQTT -> MqttService.intentRecognition(text)
                IntentRecognitionOptions.Disabled -> logger.d { "intentRecognition disabled" }
            }

        }
    }

    fun playAudio(data: ByteArray) {
        logger.d { "playAudio ${data.size}" }

        coroutineScope.launch {

            when (ConfigurationSettings.audioPlayingOption.data) {
                AudioPlayingOptions.Local -> AudioPlayer.playData(data)
                AudioPlayingOptions.RemoteHTTP -> HttpService.playWav(data)
                AudioPlayingOptions.RemoteMQTT -> MqttService.playWav(data)
                AudioPlayingOptions.Disabled -> logger.d { "audioPlaying disabled" }
            }

        }
    }

    fun intentHandling(intent: String) {
        logger.d { "intentRecognized $intent" }

        coroutineScope.launch {

            when (ConfigurationSettings.intentHandlingOption.data) {
                IntentHandlingOptions.HomeAssistant -> TODO()
                IntentHandlingOptions.RemoteHTTP -> HttpService.intentHandling(intent)
                IntentHandlingOptions.Disabled -> logger.d { "intentHandling disabled" }
                IntentHandlingOptions.WithRecognition -> logger.e { "intentHandling with recognition was not used" }
            }

        }
    }

    fun speechToText(data: ByteArray) {
        logger.d { "speechToText ${data.size}" }

        coroutineScope.launch {

            when (ConfigurationSettings.speechToTextOption.data) {
                SpeechToTextOptions.RemoteHTTP -> HttpService.speechToText(data)
                SpeechToTextOptions.RemoteMQTT -> MqttService.speechToText(data)
                SpeechToTextOptions.Disabled -> logger.d { "speechToText disabled" }
            }

        }
    }

    fun startRecording() {
        logger.d { "startRecording" }

        showIndication()
        RecordingService.startRecording()
    }

    fun stopRecording() {
        logger.d { "stopRecording" }

        stopIndication()
        RecordingService.stopRecording()
        speechToText(RecordingService.getLatestRecording())
    }

    fun setVolume(volume: Float) {
        AppSettings.volume.data = volume
    }

    fun setListenForWake(action: Boolean) {
        logger.d { "setListenForWake $action" }

        listenForWakeEnabled.value = action
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
            startRecording()
        }
    }

    fun playRecording() {
        AudioPlayer.playData(getLatestRecording())
    }

}
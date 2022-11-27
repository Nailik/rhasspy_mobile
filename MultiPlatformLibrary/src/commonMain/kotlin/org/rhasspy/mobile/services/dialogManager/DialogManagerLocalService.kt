package org.rhasspy.mobile.services.dialogManager

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.rhasspy.mobile.data.IntentRecognitionOptions
import org.rhasspy.mobile.data.SpeechToTextOptions
import org.rhasspy.mobile.data.WakeWordOption

class DialogManagerLocalService : IDialogManagerService() {
    private val logger = Logger.withTag("DialogManagerLocalService")

    private var scope = CoroutineScope(Dispatchers.Default)

    override fun onClose() {
        scope.cancel()
        super.onClose()
    }

    override fun startSessionMqtt() {
        //ignored
    }

    override fun endSessionMqtt(sessionId: String?) {
        //ignored
    }

    override fun startedSessionMqtt(sessionId: String?) {
        //ignored
    }

    override fun sessionEndedMqtt(sessionId: String?) {
        //ignored
    }

    override fun startListeningMqtt(sessionId: String?, isSendAudioCaptured: Boolean) {
        //ignored
    }

    override fun stopListeningMqtt(sessionId: String?) {
        //ignored
    }

    override fun hotWordDetectedMqtt(hotWord: String) {
        //ignore if option is not mqtt
        if (params.wakeWordOption == WakeWordOption.MQTT) {
            hotWordDetected(hotWord)
        }
    }

    override fun intentTranscribedMqtt(sessionId: String?, text: String?) {
        //ignore if option is not mqtt
        if (params.speechToTextOption == SpeechToTextOptions.RemoteMQTT) {
            textTranscribed(text)
        }
    }


    override fun intentTranscribedErrorMqtt(sessionId: String?) {
        //ignore if option is not mqtt
        if (params.speechToTextOption == SpeechToTextOptions.RemoteMQTT) {
            textTranscribedError()
        }
    }

    override fun intentRecognizedMqtt(sessionId: String?, intentName: String?, intent: String) {
        //ignore if option is not mqtt
        if (params.intentRecognitionOption == IntentRecognitionOptions.RemoteMQTT) {
            intentRecognized(intentName, intent)
        }
    }

    override fun intentNotRecognizedMqtt(sessionId: String?) {
        //ignore if option is not mqtt
        if (params.intentRecognitionOption == IntentRecognitionOptions.RemoteMQTT) {
            intentRecognizedError()
        }
    }

    override fun listenForCommandWebServer() {
        //handle like hot word
        hotWordDetected("Remote")
    }

    override fun startRecordingWebServer() {
        //handle like hot word
        startRecording()
    }

    override fun stopRecordingWebServer() {
        stopRecording()
    }

    override fun hotWordDetectedLocal(hotWord: String) {
        hotWordDetected(hotWord)
    }

    override fun silenceDetectedLocal() {
        //like stop recording
        stopRecording()
    }


    private var state = DialogManagerState.IDLE


    fun hotWordDetected(hotWord: String) {
        if (state != DialogManagerState.IDLE) {
            logger.e { "hotWordDetected$hotWord wrong state $state" }
            return
        }
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

    fun startRecording() {
        //stop recording voice command
        //TODO disable hot word
        //TODO indication RECORDING (visual)
        //TODO start recording voice (after sound)
    }

    fun stopRecording() {
        //stop recording voice command
        //TODO stop recording
        //TODO tell mqtt asr system to stop listening (if necessary)
    }

    fun startTranscribing() {
        //TODO indication THINKING (visual)
        //TODO stop recording
        //TODO start trans
    }

    fun textTranscribed(text: String?) {
        //speech was transcribed to text
        //TODO indication THINKING (visual)
        //TODO start intent recognition
    }

    fun textTranscribedError() {
        sessionError()
    }

    fun startIntentRecognition() {
        //TODO indication THINKING (visual)
    }

    fun intentRecognized(intentName: String?, intent: String) {
        //intent was recognized from speech (or text)
        //TODO indication RECORDING (sound, visual)
    }

    fun intentRecognizedError() {
        sessionError()
    }

    fun sessionError() {
        //TODO indication ERROR (sound)
    }

    fun textNotTranscribed(text: String?) {

    }

    fun sessionEnded() {
        //TODO stop session
        //TODO indication IDLE
    }


}
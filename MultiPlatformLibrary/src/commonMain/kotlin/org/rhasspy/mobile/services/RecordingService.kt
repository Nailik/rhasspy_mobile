package org.rhasspy.mobile.services

import co.touchlab.kermit.Logger
import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.map
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.services.native.NativeIndication
import org.rhasspy.mobile.settings.AppSettings

/**
 * Handles listening to speech
 */
object RecordingService {
    private val logger = Logger.withTag(this::class.simpleName!!)

    private val listening = MutableLiveData(false)

    //represents listening Status for ui
    val status: LiveData<Boolean> = listening.map { it }

    /**
     * should be called when wake word is detected or user wants to speak
     * by clicking ui
     */
    fun startRecording() {
        logger.d { "startRecording" }

        listening.value = true
        indication()
    }

    /**
     * called when service should stop listening
     */
    fun stopRecording() {
        logger.d { "stopListening" }

        listening.value = false
        stopIndication()
    }

    /**
     * called when user presses button and should start or stop recording
     */
    fun toggleRecording() {
        logger.d { "stopListening" }

        if(listening.value){
            stopRecording()
        }else{
            startRecording()
        }
    }

    /**
     * starts wake word indication according to settings
     */
    private fun indication() {
        logger.d { "indication" }

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

    /**
     * stops all indications
     */
    private fun stopIndication() {
        logger.d { "stopIndication" }

        NativeIndication.closeIndicationOverOtherApps()
        NativeIndication.releaseWakeUp()
    }

}
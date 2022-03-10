package org.rhasspy.mobile.services

import co.touchlab.kermit.Logger
import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.map
import dev.icerock.moko.mvvm.livedata.postValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.services.native.NativeIndication
import org.rhasspy.mobile.settings.AppSettings
import kotlin.time.Duration.Companion.seconds

/**
 * Handles listening to speech
 */
object ListeningService {
    private val logger = Logger.withTag(this::class.simpleName!!)

    private val listening = MutableLiveData(false)

    //represents listening Status for ui
    val status: LiveData<Boolean> = listening.map { it }

    /**
     * should be called when wake word is detected or user wants to speak
     * by clicking ui
     */
    fun wakeWordDetected() {
        logger.d { "wakeWordDetected" }

        listening.value = true
        indication()

        //For now after 10 seconds listening is stopped
        CoroutineScope(Dispatchers.Default).launch {
            //reset for now no automatically silence detection
            delay(5.seconds)
            CoroutineScope(Dispatchers.Main).launch {
                stopListening()
            }
        }
    }

    /**
     * called when service should stop listening
     */
    private fun stopListening() {
        logger.d { "stopListening" }

        listening.value = false
        stopIndication()
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
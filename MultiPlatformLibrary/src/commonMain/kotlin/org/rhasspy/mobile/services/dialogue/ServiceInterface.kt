package org.rhasspy.mobile.services.dialogue

import co.touchlab.kermit.Logger
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.data.WakeWordOption
import org.rhasspy.mobile.services.ForegroundService
import org.rhasspy.mobile.services.MqttService
import org.rhasspy.mobile.services.RecordingService
import org.rhasspy.mobile.services.RecordingService.addWavHeader
import org.rhasspy.mobile.services.ServiceAction
import org.rhasspy.mobile.services.http.HttpServer
import org.rhasspy.mobile.services.native.AudioPlayer
import org.rhasspy.mobile.services.native.NativeIndication
import org.rhasspy.mobile.services.native.NativeLocalWakeWordService
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.settings.ConfigurationSettings
import org.rhasspy.mobile.viewModels.GlobalData

object ServiceInterface {
    private val logger = Logger.withTag(this::class.simpleName!!)

    /**
     * Start services according to settings
     */
    fun serviceAction(serviceAction: ServiceAction) {
        logger.d { "serviceAction ${serviceAction.name}" }

        when (serviceAction) {
            ServiceAction.Start -> {
                startLocalWakeWordService()
                HttpServer.start()
                MqttService.start()
            }
            ServiceAction.Stop -> {
                NativeLocalWakeWordService.stop()
                HttpServer.stop()
                MqttService.stop()
            }
            ServiceAction.Reload -> {
                serviceAction(ServiceAction.Stop)
                serviceAction(ServiceAction.Start)
            }
        }
    }

    /**
     * call the native indication and show/hide necessary indications
     */
    fun indication(show: Boolean) {
        logger.d { "toggle indication show: $show" }

        if (show) {
            if (AppSettings.isWakeWordSoundIndication.data) {
                NativeIndication.playAudio(MR.files.etc_wav_beep_hi)
            }

            if (AppSettings.isBackgroundWakeWordDetectionTurnOnDisplay.data) {
                NativeIndication.wakeUpScreen()
            }

            if (AppSettings.isWakeWordLightIndication.data) {
                NativeIndication.showIndication()
            }
        } else {
            NativeIndication.closeIndicationOverOtherApps()
            NativeIndication.releaseWakeUp()
        }
    }


    /**
     * starts the local wakeword Service
     */
    private fun startLocalWakeWordService() {
        if (ConfigurationSettings.wakeWordOption.data == WakeWordOption.Porcupine) {
            if (ConfigurationSettings.wakeWordAccessToken.data.isNotEmpty()) {
                NativeLocalWakeWordService.start()
            } else {
                logger.e { "couldn't start local wake word service, access Token Empty" }
            }
        }
    }

    //################## Actions called from Other Services

    /**
     * returns latest recording as wav file
     */
    fun getLatestRecording(): ByteArray {
        return RecordingService.getLatestRecording()
    }

    fun wakeWordDetected() {
        DialogueManagement.wakeWordDetected()
    }

    fun finishedPlayingAudio() {
        DialogueManagement.playFinished()
    }

    fun silenceDetected() {

    }

    fun audioFrame(byteArray: ByteArray) {
        DialogueManagement.audioFrame(byteArray.addWavHeader())
    }

    //################## Actions called from UI

    /**
     * starts or stops a session, depends if it is currently running
     */
    fun toggleSession() {
        DialogueManagement.sessionId.value?.also {
            DialogueManagement.endSession(it)
        } ?: run {
            DialogueManagement.startSession()
        }
    }

    /**
     * plays last recording
     */
    fun playRecording() {
        AudioPlayer.playData(getLatestRecording())
    }

    /**
     * Saves configuration changes
     */
    fun saveAndApplyChanges() {
        GlobalData.saveAllChanges()
        ForegroundService.action(ServiceAction.Reload)
    }

    /**
     * resets configuration changes
     */
    fun resetChanges() {
        GlobalData.resetChanges()
    }

}
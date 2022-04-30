package org.rhasspy.mobile.services

import kotlinx.coroutines.launch
import org.rhasspy.mobile.services.*
import org.rhasspy.mobile.viewModels.GlobalData

object ServiceInterface {


    /**
     * Start services according to settings
     */
    suspend fun serviceAction(serviceAction: ServiceAction) {
        RhasspyActions.logger.d { "serviceAction ${serviceAction.name}" }

        when (serviceAction) {
            ServiceAction.Start -> {
                UdpService.start()
                RhasspyActions.startHotWord()
                HttpServer.start()
                MqttService.start()
            }
            ServiceAction.Stop -> {
                //reset values
                isSendAudioCaptured = false
                currentlyPlayingRecording = false
                mqttSpeechToTextSessionId = null
                currentRecording.clear()
                isIntentRecognized = false
                currentSessionId = null

                UdpService.stop()
                RhasspyActions.stopHotWord()
                HttpServer.stop()
                MqttService.stop()
            }
            ServiceAction.Reload -> {
                currentlyRestarting = true
                serviceAction(ServiceAction.Stop)
                serviceAction(ServiceAction.Start)
                currentlyRestarting = false
            }
        }
    }




    /**
     * Saves configuration changes
     */
    fun saveAndApplyChanges() {
        RhasspyActions.coroutineScope.launch {
            currentlyRestarting = true
            GlobalData.saveAllChanges()
            ForegroundService.action(ServiceAction.Reload)
        }
    }

    /**
     * resets configuration changes
     */
    fun resetChanges() {
        GlobalData.resetChanges()
    }


}
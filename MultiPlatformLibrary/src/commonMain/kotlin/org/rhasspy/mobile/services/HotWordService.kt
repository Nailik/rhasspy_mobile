package org.rhasspy.mobile.services

import org.rhasspy.mobile.data.WakeWordOption
import org.rhasspy.mobile.services.native.NativeLocalWakeWordService
import org.rhasspy.mobile.settings.ConfigurationSettings

object HotWordService {



    /**
     * start hotWord services
     */
    private fun startHotWord() {
        RhasspyActions.logger.d { "startHotWord" }

        when (ConfigurationSettings.wakeWordOption.data) {
            WakeWordOption.Porcupine -> {
                //when porcupine is used for hotWord then start local service
                if (ConfigurationSettings.wakeWordPorcupineAccessToken.data.isNotEmpty()) {
                    NativeLocalWakeWordService.start()
                } else {
                    val description = "couldn't start local wake word service, access Token Empty"
                    hotWordError(description)
                    RhasspyActions.logger.e { description }
                }
            }
            //when mqtt is used for hotWord, start recording
            WakeWordOption.MQTT -> RecordingService.startRecording()
            WakeWordOption.Disabled -> {}
        }
    }

    /**
     * stop hotWord services
     */
    private fun stopHotWord() {
        RhasspyActions.logger.d { "stopHotWord" }
        //make sure it is stopped
        NativeLocalWakeWordService.stop()
        //stop recorder if not used
        if (currentSessionId == null) {
            //if no running session then it's not necessary to record
            RecordingService.stopRecording()
        }
    }

}
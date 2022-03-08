package org.rhasspy.mobile.services

import dev.icerock.moko.mvvm.livedata.MutableLiveData
import org.rhasspy.mobile.data.WakeWordOption
import org.rhasspy.mobile.settings.ConfigurationSettings

object ForegroundService {

    val listening = MutableLiveData(false)

    fun startServices() {
        if (ConfigurationSettings.wakeWordOption.value == WakeWordOption.Porcupine && ConfigurationSettings.wakeWordAccessToken.value.isNotEmpty()) {
            LocalWakeWordService.start()
        }
        ListeningService.start()
    }

    fun stopServices() {
        LocalWakeWordService.stop()
        ListeningService.stop()
    }

}
package org.rhasspy.mobile.services

import dev.icerock.moko.mvvm.livedata.MutableLiveData
import org.rhasspy.mobile.data.WakeWordOption
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.settings.ConfigurationSettings

object ForegroundService {

    val listening = MutableLiveData(false)

    fun action(action: Action, fromService: Boolean = false) {
        if (AppSettings.isBackgroundWakeWordDetection.data && !fromService) {
            //start service
            NativeService.doAction(action)
        } else {
            when (action) {
                Action.Start -> startServices()
                Action.Stop -> stopServices()
                Action.Reload -> reloadServices()
            }
        }
    }

    private fun startServices() {
        if (ConfigurationSettings.wakeWordOption.data == WakeWordOption.Porcupine &&
            ConfigurationSettings.wakeWordAccessToken.data.isNotEmpty()
        ) {
            LocalWakeWordService.start()
        }
        ListeningService.start()
    }

    private fun stopServices() {
        LocalWakeWordService.stop()
        ListeningService.stop()
    }

    private fun reloadServices() {
        stopServices()
        startServices()
    }
}
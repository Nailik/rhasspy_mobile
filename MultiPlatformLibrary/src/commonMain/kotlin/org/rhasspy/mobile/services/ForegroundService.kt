package org.rhasspy.mobile.services

import dev.icerock.moko.mvvm.livedata.MutableLiveData
import org.rhasspy.mobile.data.WakeWordOption
import org.rhasspy.mobile.settings.AppSettings
import org.rhasspy.mobile.settings.ConfigurationSettings

object ForegroundService {

    val listening = MutableLiveData(false)

    init {
        AppSettings.isBackgroundEnabled.value.addObserver {
            action(Action.Reload)
        }
    }

    fun action(action: Action, fromService: Boolean = false) {
        if (fromService) {
            when (action) {
                Action.Start -> startServices()
                Action.Stop -> stopServices()
                Action.Reload -> reloadServices()
            }
        } else {
            if (!AppSettings.isBackgroundEnabled.data && !NativeService.isRunning) {
                //stop service
                NativeService.stop()
            } else {
                //start or update service
                NativeService.doAction(action)
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
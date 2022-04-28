package org.rhasspy.mobile.viewModels

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.rhasspy.mobile.nativeutils.SettingsUtils
import org.rhasspy.mobile.services.MqttService
import org.rhasspy.mobile.settings.ConfigurationSettings

class ConfigurationScreenViewModel : ViewModel() {

    val isMQTTConnected = MqttService.isConnected

    fun selectPorcupineWakeWordFile() = SettingsUtils.selectPorcupineFile { fileName ->
        fileName?.also {
            ConfigurationSettings.wakeWordPorcupineKeywordOptions.unsavedData =
                ConfigurationSettings.wakeWordPorcupineKeywordOptions.unsavedData.toMutableList()
                    .apply {
                        this.add(it)
                    }.toSet()
            ConfigurationSettings.wakeWordPorcupineKeywordOption.unsavedData =
                ConfigurationSettings.wakeWordPorcupineKeywordOptions.unsavedData.size - 1
        }
    }

}
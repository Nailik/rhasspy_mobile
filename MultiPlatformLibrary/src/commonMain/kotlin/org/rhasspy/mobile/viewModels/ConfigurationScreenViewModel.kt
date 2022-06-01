package org.rhasspy.mobile.viewModels

import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.readOnly
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.rhasspy.mobile.nativeutils.SettingsUtils
import org.rhasspy.mobile.services.MqttService
import org.rhasspy.mobile.services.ServiceInterface
import org.rhasspy.mobile.services.ServiceState
import org.rhasspy.mobile.settings.ConfigurationSettings

class ConfigurationScreenViewModel : ViewModel() {

    val isMQTTConnected = MqttService.isConnected

    private val changeEnabled = MutableLiveData(true)
    val isChangeEnabled = changeEnabled.readOnly()

    init {
        ServiceInterface.currentState.observe {
            changeEnabled.value = it == ServiceState.Running
        }
    }

    fun selectPorcupineWakeWordFile() = SettingsUtils.selectPorcupineFile { fileName ->
        fileName?.also {
            ConfigurationSettings.wakeWordPorcupineKeywordOptions.unsaved.value =
                ConfigurationSettings.wakeWordPorcupineKeywordOptions.unsaved.value.toMutableList()
                    .apply {
                        this.add(it)
                    }.toSet()
            ConfigurationSettings.wakeWordPorcupineKeywordOption.unsaved.value =
                ConfigurationSettings.wakeWordPorcupineKeywordOptions.unsaved.value.size - 1
        }
    }

}
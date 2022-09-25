package org.rhasspy.mobile.viewModels

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.rhasspy.mobile.mqtt.MqttError
import org.rhasspy.mobile.nativeutils.SettingsUtils
import org.rhasspy.mobile.services.MqttService
import org.rhasspy.mobile.services.ServiceInterface
import org.rhasspy.mobile.services.ServiceState
import org.rhasspy.mobile.settings.ConfigurationSettings

class ConfigurationScreenViewModel : ViewModel() {

    val isMQTTConnected = MqttService.isConnected

    private val changeEnabled = MutableStateFlow(true)
    val isChangeEnabled: StateFlow<Boolean> get() = changeEnabled

    private val testingMqttConnection = MutableStateFlow(false)
    val isTestingMqttConnection: StateFlow<Boolean> get()  = testingMqttConnection
    private val testingMqttError = MutableStateFlow<MqttError?>(null)
    val testingMqttErrorUiData: StateFlow<MqttError?> get()  = testingMqttError

    init {
        ServiceInterface.currentState.onEach {
            changeEnabled.value = it == ServiceState.Running
        }

        ConfigurationSettings.mqttHost.unsaved.onEach {
            testingMqttError.value = null
        }

        ConfigurationSettings.mqttPort.unsaved.onEach {
            testingMqttError.value = null
        }

        ConfigurationSettings.mqttUserName.unsaved.onEach {
            testingMqttError.value = null
        }

        ConfigurationSettings.mqttPassword.unsaved.onEach {
            testingMqttError.value = null
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

    fun testMqttConnection() {
        if(!testingMqttConnection.value) {
            //show loading
            testingMqttConnection.value = true
            viewModelScope.launch(Dispatchers.Main) {
                testingMqttError.value = MqttService.testConnection()
                testingMqttConnection.value = false
            }
        }
        //disable editing of mqtt settings
        //show result
    }

}
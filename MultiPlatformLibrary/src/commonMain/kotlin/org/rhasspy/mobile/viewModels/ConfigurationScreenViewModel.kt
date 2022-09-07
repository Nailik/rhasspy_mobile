package org.rhasspy.mobile.viewModels

import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.postValue
import dev.icerock.moko.mvvm.livedata.readOnly
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.rhasspy.mobile.mqtt.MqttError
import org.rhasspy.mobile.nativeutils.SettingsUtils
import org.rhasspy.mobile.services.MqttService
import org.rhasspy.mobile.services.ServiceInterface
import org.rhasspy.mobile.services.ServiceState
import org.rhasspy.mobile.settings.ConfigurationSettings

class ConfigurationScreenViewModel : ViewModel() {

    val isMQTTConnected = MqttService.isConnected

    private val changeEnabled = MutableLiveData(true)
    val isChangeEnabled = changeEnabled.readOnly()

    private val testingMqttConnection = MutableLiveData(false)
    val isTestingMqttConnection = testingMqttConnection.readOnly()
    private val testingMqttError = MutableLiveData<MqttError?>(null)
    val testingMqttErrorUiData = testingMqttError.readOnly()

    init {
        ServiceInterface.currentState.observe {
            changeEnabled.postValue(it == ServiceState.Running)
        }

        ConfigurationSettings.mqttHost.unsaved.observe {
            testingMqttError.value = null
        }

        ConfigurationSettings.mqttPort.unsaved.observe {
            testingMqttError.value = null
        }

        ConfigurationSettings.mqttUserName.unsaved.observe {
            testingMqttError.value = null
        }

        ConfigurationSettings.mqttPassword.unsaved.observe {
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
                testingMqttError.postValue(MqttService.testConnection())
                testingMqttConnection.postValue(false)
            }
        }
        //disable editing of mqtt settings
        //show result
    }

}
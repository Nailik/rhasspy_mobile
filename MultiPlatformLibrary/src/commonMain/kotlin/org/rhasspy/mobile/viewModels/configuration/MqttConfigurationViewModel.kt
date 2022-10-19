package org.rhasspy.mobile.viewModels.configuration

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.combineState
import org.rhasspy.mobile.mapReadonlyState
import org.rhasspy.mobile.mqtt.MqttError
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.MqttService
import org.rhasspy.mobile.settings.ConfigurationSettings

class MqttConfigurationViewModel : ViewModel() {

    //unsaved data
    private val _isMqttEnabled = MutableStateFlow(ConfigurationSettings.isMqttEnabled.value)
    private val _mqttHost = MutableStateFlow(ConfigurationSettings.mqttHost.value)
    private val _mqttPort = MutableStateFlow(ConfigurationSettings.mqttPort.value)
    private val _mqttUserName = MutableStateFlow(ConfigurationSettings.mqttUserName.value)
    private val _mqttPassword = MutableStateFlow(ConfigurationSettings.mqttPassword.value)
    private val _isMqttSSLEnabled = MutableStateFlow(ConfigurationSettings.isMqttSSLEnabled.value)
    private val _mqttConnectionTimeout = MutableStateFlow(ConfigurationSettings.mqttConnectionTimeout.value)
    private val _mqttKeepAliveInterval = MutableStateFlow(ConfigurationSettings.mqttKeepAliveInterval.value)
    private val _mqttRetryInterval = MutableStateFlow(ConfigurationSettings.mqttRetryInterval.value)

    //unsaved ui data
    val isMqttEnabled = _isMqttEnabled.readOnly
    val mqttHost = _mqttHost.readOnly
    val mqttPort = _mqttPort.readOnly
    val mqttUserName = _mqttUserName.readOnly
    val mqttPassword = _mqttPassword.readOnly
    val isMqttSSLEnabled = _isMqttSSLEnabled.readOnly
    val isMqttChooseCertificateVisible = isMqttSSLEnabled
    val mqttConnectionTimeout = _mqttConnectionTimeout.readOnly
    val mqttKeepAliveInterval = _mqttKeepAliveInterval.readOnly
    val mqttRetryInterval = _mqttRetryInterval.readOnly

    //show input field for endpoint
    val isMqttSettingsVisible = _isMqttEnabled.mapReadonlyState { it }

    //Mqtt testing
    private val _isTestingMqttConnection = MutableStateFlow(false)
    private val _testingMqttError = MutableStateFlow<MqttError?>(null)

    val isTestingMqttConnection = _isTestingMqttConnection.readOnly
    val testingMqttError = _testingMqttError.readOnly
    val isMqttTestEnabled = combineState(ConfigurationSettings.mqttHost.data, ConfigurationSettings.mqttPort.data) { host, port ->
        host.isNotEmpty() && port.isNotEmpty()
    }

    //set new intent recognition option
    fun toggleMqttEnabled(enabled: Boolean) {
        _isMqttEnabled.value = enabled
    }

    //set mqtt host
    fun updateMqttHost(host: String) {
        _mqttHost.value = host
    }

    //set mqtt port
    fun updateMqttPort(port: String) {
        _mqttPort.value = port
    }

    //set mqtt username
    fun updateMqttUserName(userName: String) {
        _mqttUserName.value = userName
    }

    //set mqtt password
    fun updateMqttPassword(password: String) {
        _mqttPassword.value = password
    }

    //toggle if mqtt ssl is enabled
    fun toggleMqttSSLEnabled(enabled: Boolean) {
        _isMqttSSLEnabled.value = enabled
    }

    //set connection timeout time
    fun updateMqttConnectionTimeout(connectionTimeout: String) {
        _mqttConnectionTimeout.value = connectionTimeout
    }

    //set keep alive interval time
    fun updateMqttKeepAliveInterval(keepAliveInterval: String) {
        _mqttKeepAliveInterval.value = keepAliveInterval
    }

    //set retry interval time
    fun updateMqttRetryInterval(retryInterval: String) {
        _mqttRetryInterval.value = retryInterval
    }

    /**
     * test if the mqtt connection with current unsaved settings is working
     */
    fun testMqttConnection() {
        if (!_isTestingMqttConnection.value) {
            //show loading
            _isTestingMqttConnection.value = true
            viewModelScope.launch(Dispatchers.Default) {
                _testingMqttError.value = MqttService.testConnection()
                _isTestingMqttConnection.value = false
            }
        }
        //disable editing of mqtt settings
        //show result
    }


    /**
     * save data configuration
     */
    fun save() {
        ConfigurationSettings.isMqttEnabled.data.value = _isMqttEnabled.value
        ConfigurationSettings.mqttHost.data.value = _mqttHost.value
        ConfigurationSettings.mqttPort.data.value = _mqttPort.value
        ConfigurationSettings.mqttUserName.data.value = _mqttUserName.value
        ConfigurationSettings.mqttPassword.data.value = _mqttPassword.value
        ConfigurationSettings.isMqttSSLEnabled.data.value = _isMqttSSLEnabled.value
        ConfigurationSettings.mqttConnectionTimeout.data.value = _mqttConnectionTimeout.value
        ConfigurationSettings.mqttKeepAliveInterval.data.value = _mqttKeepAliveInterval.value
        ConfigurationSettings.mqttRetryInterval.data.value = _mqttRetryInterval.value
    }

    /**
     * test unsaved data configuration
     */
    fun test() {

    }

}
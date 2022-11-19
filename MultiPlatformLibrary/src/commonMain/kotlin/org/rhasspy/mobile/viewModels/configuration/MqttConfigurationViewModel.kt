package org.rhasspy.mobile.viewModels.configuration

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.combineAny
import org.rhasspy.mobile.combineStateNotEquals
import org.rhasspy.mobile.mapReadonlyState
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.state.ServiceState
import org.rhasspy.mobile.settings.ConfigurationSettings

class MqttConfigurationViewModel : ViewModel(), IConfigurationViewModel {

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

    override val isTestingEnabled = _isMqttEnabled.readOnly
    override val testState: StateFlow<List<ServiceState>> = MutableStateFlow(listOf())

    override val hasUnsavedChanges = combineAny(
        combineStateNotEquals(_isMqttEnabled, ConfigurationSettings.isMqttEnabled.data),
        combineStateNotEquals(_mqttHost, ConfigurationSettings.mqttHost.data),
        combineStateNotEquals(_mqttPort, ConfigurationSettings.mqttPort.data),
        combineStateNotEquals(_mqttUserName, ConfigurationSettings.mqttUserName.data),
        combineStateNotEquals(_mqttPassword, ConfigurationSettings.mqttPassword.data),
        combineStateNotEquals(_isMqttSSLEnabled, ConfigurationSettings.isMqttSSLEnabled.data),
        combineStateNotEquals(_mqttConnectionTimeout, ConfigurationSettings.mqttConnectionTimeout.data),
        combineStateNotEquals(_mqttKeepAliveInterval, ConfigurationSettings.mqttKeepAliveInterval.data),
        combineStateNotEquals(_mqttRetryInterval, ConfigurationSettings.mqttRetryInterval.data)
    )

    //show input field for endpoint
    val isMqttSettingsVisible = _isMqttEnabled.mapReadonlyState { it }

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
     * save data configuration
     */
    override fun save() {
        ConfigurationSettings.isMqttEnabled.value = _isMqttEnabled.value
        ConfigurationSettings.mqttHost.value = _mqttHost.value
        ConfigurationSettings.mqttPort.value = _mqttPort.value
        ConfigurationSettings.mqttUserName.value = _mqttUserName.value
        ConfigurationSettings.mqttPassword.value = _mqttPassword.value
        ConfigurationSettings.isMqttSSLEnabled.value = _isMqttSSLEnabled.value
        ConfigurationSettings.mqttConnectionTimeout.value = _mqttConnectionTimeout.value
        ConfigurationSettings.mqttKeepAliveInterval.value = _mqttKeepAliveInterval.value
        ConfigurationSettings.mqttRetryInterval.value = _mqttRetryInterval.value
    }

    /**
     * undo all changes
     */
    override fun discard() {
        _isMqttEnabled.value = ConfigurationSettings.isMqttEnabled.value
        _mqttHost.value = ConfigurationSettings.mqttHost.value
        _mqttPort.value = ConfigurationSettings.mqttPort.value
        _mqttUserName.value = ConfigurationSettings.mqttUserName.value
        _mqttPassword.value = ConfigurationSettings.mqttPassword.value
        _isMqttSSLEnabled.value = ConfigurationSettings.isMqttSSLEnabled.value
        _mqttConnectionTimeout.value = ConfigurationSettings.mqttConnectionTimeout.value
        _mqttKeepAliveInterval.value = ConfigurationSettings.mqttKeepAliveInterval.value
        _mqttRetryInterval.value = ConfigurationSettings.mqttRetryInterval.value
    }


    /**
     * test unsaved data configuration
     */
    override fun test() {
        //TODO default MQTT port
        //check if mqtt connection can be established
    }

    override fun stopTest() {

    }

}
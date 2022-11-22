package org.rhasspy.mobile.viewModels.configuration

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.combineAny
import org.rhasspy.mobile.combineStateNotEquals
import org.rhasspy.mobile.logger.Event
import org.rhasspy.mobile.mapReadonlyState
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.settings.ConfigurationSettings

class MqttConfigurationViewModel : IConfigurationViewModel() {

    //unsaved data
    private val _isMqttEnabled = MutableStateFlow(ConfigurationSettings.isMqttEnabled.value)
    private val _mqttHost = MutableStateFlow(ConfigurationSettings.mqttHost.value)
    private val _mqttPort = MutableStateFlow(ConfigurationSettings.mqttPort.value)
    private val _mqttPortText = MutableStateFlow(ConfigurationSettings.mqttPort.value.toString())
    private val _mqttUserName = MutableStateFlow(ConfigurationSettings.mqttUserName.value)
    private val _mqttPassword = MutableStateFlow(ConfigurationSettings.mqttPassword.value)
    private val _isMqttSSLEnabled = MutableStateFlow(ConfigurationSettings.isMqttSSLEnabled.value)
    private val _mqttConnectionTimeout = MutableStateFlow(ConfigurationSettings.mqttConnectionTimeout.value)
    private val _mqttConnectionTimeoutText = MutableStateFlow(ConfigurationSettings.mqttConnectionTimeout.value.toString())
    private val _mqttKeepAliveInterval = MutableStateFlow(ConfigurationSettings.mqttKeepAliveInterval.value)
    private val _mqttKeepAliveIntervalText = MutableStateFlow(ConfigurationSettings.mqttKeepAliveInterval.value.toString())
    private val _mqttRetryInterval = MutableStateFlow(ConfigurationSettings.mqttRetryInterval.value)
    private val _mqttRetryIntervalText = MutableStateFlow(ConfigurationSettings.mqttRetryInterval.value.toString())

    //unsaved ui data
    val isMqttEnabled = _isMqttEnabled.readOnly
    val mqttHost = _mqttHost.readOnly
    val mqttPortText = _mqttPortText.readOnly
    val mqttUserName = _mqttUserName.readOnly
    val mqttPassword = _mqttPassword.readOnly
    val isMqttSSLEnabled = _isMqttSSLEnabled.readOnly
    val isMqttChooseCertificateVisible = isMqttSSLEnabled
    val mqttConnectionTimeoutText = _mqttConnectionTimeoutText.readOnly
    val mqttKeepAliveIntervalText = _mqttKeepAliveIntervalText.readOnly
    val mqttRetryIntervalText = _mqttRetryIntervalText.readOnly

    override val isTestingEnabled = _isMqttEnabled.readOnly

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
        val text = port.replace("""[-,. ]""".toRegex(), "")
        _mqttPortText.value = text
        _mqttPort.value = text.toIntOrNull() ?: 0
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
        val text = connectionTimeout.replace("""[-,. ]""".toRegex(), "")
        _mqttConnectionTimeoutText.value = text
        _mqttConnectionTimeout.value = text.toIntOrNull() ?: 0
    }

    //set keep alive interval time
    fun updateMqttKeepAliveInterval(keepAliveInterval: String) {
        val text = keepAliveInterval.replace("""[-,. ]""".toRegex(), "")
        _mqttKeepAliveIntervalText.value = text
        _mqttKeepAliveInterval.value = text.toIntOrNull() ?: 0
    }

    //set retry interval time
    fun updateMqttRetryInterval(retryInterval: String) {
        val text = retryInterval.replace("""[-,. ]""".toRegex(), "")
        _mqttRetryIntervalText.value = text
        _mqttRetryInterval.value = text.toLongOrNull() ?: 0
    }

    /**
     * save data configuration
     */
    override fun onSave() {
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
    override fun onTest(): StateFlow<List<Event>> {
        //TODO default MQTT port
        //check if mqtt connection can be established
        TODO()
    }

}
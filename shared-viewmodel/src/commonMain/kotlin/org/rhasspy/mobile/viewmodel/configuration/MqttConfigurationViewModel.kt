package org.rhasspy.mobile.viewmodel.configuration

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.logic.combineAny
import org.rhasspy.mobile.logic.combineStateNotEquals
import org.rhasspy.mobile.logic.fileutils.FolderType
import org.rhasspy.mobile.logic.logger.LogType
import org.rhasspy.mobile.logic.mapReadonlyState
import org.rhasspy.mobile.logic.nativeutils.FileUtils
import org.rhasspy.mobile.logic.openLink
import org.rhasspy.mobile.logic.readOnly
import org.rhasspy.mobile.logic.services.mqtt.MqttService
import org.rhasspy.mobile.logic.services.mqtt.MqttServiceConnectionOptions
import org.rhasspy.mobile.logic.services.mqtt.MqttServiceParams
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.test.MqttConfigurationTest

class MqttConfigurationViewModel : IConfigurationViewModel() {

    override val testRunner by inject<MqttConfigurationTest>()
    override val logType = LogType.MqttService
    override val serviceState get() = get<MqttService>().serviceState

    //unsaved data
    private val _isMqttEnabled = MutableStateFlow(ConfigurationSetting.isMqttEnabled.value)
    private val _mqttHost = MutableStateFlow(ConfigurationSetting.mqttHost.value)
    private val _mqttPort = MutableStateFlow(ConfigurationSetting.mqttPort.value)
    private val _mqttPortText = MutableStateFlow(ConfigurationSetting.mqttPort.value.toString())
    private val _mqttUserName = MutableStateFlow(ConfigurationSetting.mqttUserName.value)
    private val _mqttPassword = MutableStateFlow(ConfigurationSetting.mqttPassword.value)
    private val _isMqttSSLEnabled = MutableStateFlow(ConfigurationSetting.isMqttSSLEnabled.value)
    private val _mqttConnectionTimeout =
        MutableStateFlow(ConfigurationSetting.mqttConnectionTimeout.value)
    private val _mqttConnectionTimeoutText =
        MutableStateFlow(ConfigurationSetting.mqttConnectionTimeout.value.toString())
    private val _mqttKeepAliveInterval =
        MutableStateFlow(ConfigurationSetting.mqttKeepAliveInterval.value)
    private val _mqttKeepAliveIntervalText =
        MutableStateFlow(ConfigurationSetting.mqttKeepAliveInterval.value.toString())
    private val _mqttRetryInterval = MutableStateFlow(ConfigurationSetting.mqttRetryInterval.value)
    private val _mqttRetryIntervalText =
        MutableStateFlow(ConfigurationSetting.mqttRetryInterval.value.toString())
    private val _mqttKeyStoreFile = MutableStateFlow(ConfigurationSetting.mqttKeyStoreFile.value)

    //unsaved ui data
    val isMqttEnabled = _isMqttEnabled.readOnly
    val mqttHost = _mqttHost.readOnly
    val mqttPortText = _mqttPortText.readOnly
    val mqttUserName = _mqttUserName.readOnly
    val mqttPassword = _mqttPassword.readOnly
    val isMqttSSLEnabled = _isMqttSSLEnabled.readOnly

    val isMqttChooseCertificateVisible = isMqttSSLEnabled
    val keyStoreFileText = _mqttKeyStoreFile.readOnly
    val isKeyStoreFileTextVisible = _mqttKeyStoreFile.mapReadonlyState { it.isNotEmpty() }

    val mqttConnectionTimeoutText = _mqttConnectionTimeoutText.readOnly
    val mqttKeepAliveIntervalText = _mqttKeepAliveIntervalText.readOnly
    val mqttRetryIntervalText = _mqttRetryIntervalText.readOnly

    override val isTestingEnabled = _isMqttEnabled.readOnly

    override val hasUnsavedChanges = combineAny(
        combineStateNotEquals(_isMqttEnabled, ConfigurationSetting.isMqttEnabled.data),
        combineStateNotEquals(_mqttHost, ConfigurationSetting.mqttHost.data),
        combineStateNotEquals(_mqttPort, ConfigurationSetting.mqttPort.data),
        combineStateNotEquals(_mqttUserName, ConfigurationSetting.mqttUserName.data),
        combineStateNotEquals(_mqttPassword, ConfigurationSetting.mqttPassword.data),
        combineStateNotEquals(_isMqttSSLEnabled, ConfigurationSetting.isMqttSSLEnabled.data),
        combineStateNotEquals(
            _mqttConnectionTimeout,
            ConfigurationSetting.mqttConnectionTimeout.data
        ),
        combineStateNotEquals(
            _mqttKeepAliveInterval,
            ConfigurationSetting.mqttKeepAliveInterval.data
        ),
        combineStateNotEquals(_mqttRetryInterval, ConfigurationSetting.mqttRetryInterval.data)
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

    //open file chooser to select certificate
    fun selectSSLCertificate() {
        viewModelScope.launch {
            FileUtils.selectFile(FolderType.CertificateFolder.Mqtt)?.also { fileName ->
                _mqttKeyStoreFile.value = fileName
            }
        }
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
     * open wiki page
     */
    fun openMQTTSSLWiki() {
        openLink("https://github.com/Nailik/rhasspy_mobile/wiki/MQTT#enable-ssl")
    }

    /**
     * save data configuration
     */
    override fun onSave() {
        if (ConfigurationSetting.mqttKeyStoreFile.value != _mqttKeyStoreFile.value) {
            FileUtils.removeFile(
                FolderType.CertificateFolder.Mqtt,
                ConfigurationSetting.mqttKeyStoreFile.value
            )
        }

        ConfigurationSetting.isMqttEnabled.value = _isMqttEnabled.value
        ConfigurationSetting.mqttHost.value = _mqttHost.value
        ConfigurationSetting.mqttPort.value = _mqttPort.value
        ConfigurationSetting.mqttUserName.value = _mqttUserName.value
        ConfigurationSetting.mqttPassword.value = _mqttPassword.value
        ConfigurationSetting.isMqttSSLEnabled.value = _isMqttSSLEnabled.value
        ConfigurationSetting.mqttKeyStoreFile.value = _mqttKeyStoreFile.value
        ConfigurationSetting.mqttConnectionTimeout.value = _mqttConnectionTimeout.value
        ConfigurationSetting.mqttKeepAliveInterval.value = _mqttKeepAliveInterval.value
        ConfigurationSetting.mqttRetryInterval.value = _mqttRetryInterval.value
    }

    /**
     * undo all changes
     */
    override fun discard() {
        if (ConfigurationSetting.mqttKeyStoreFile.value != _mqttKeyStoreFile.value) {
            FileUtils.removeFile(FolderType.CertificateFolder.Mqtt, _mqttKeyStoreFile.value)
        }

        _isMqttEnabled.value = ConfigurationSetting.isMqttEnabled.value
        _mqttHost.value = ConfigurationSetting.mqttHost.value
        _mqttPort.value = ConfigurationSetting.mqttPort.value
        _mqttPortText.value = ConfigurationSetting.mqttPort.value.toString()
        _mqttUserName.value = ConfigurationSetting.mqttUserName.value
        _mqttPassword.value = ConfigurationSetting.mqttPassword.value
        _isMqttSSLEnabled.value = ConfigurationSetting.isMqttSSLEnabled.value
        _mqttConnectionTimeout.value = ConfigurationSetting.mqttConnectionTimeout.value
        _mqttKeepAliveInterval.value = ConfigurationSetting.mqttKeepAliveInterval.value
        _mqttRetryInterval.value = ConfigurationSetting.mqttRetryInterval.value
    }


    /**
     * test unsaved data configuration
     */
    override fun initializeTestParams() {
        //initialize test params
        get<MqttServiceParams> {
            parametersOf(
                MqttServiceParams(
                    isMqttEnabled = _isMqttEnabled.value,
                    mqttHost = _mqttHost.value,
                    mqttPort = _mqttPort.value,
                    retryInterval = _mqttRetryInterval.value,
                    mqttServiceConnectionOptions = MqttServiceConnectionOptions(
                        isSSLEnabled = _isMqttSSLEnabled.value,
                        keyStoreFile = _mqttKeyStoreFile.value,
                        connUsername = _mqttUserName.value,
                        connPassword = _mqttPassword.value,
                        connectionTimeout = _mqttConnectionTimeout.value,
                        keepAliveInterval = _mqttKeepAliveInterval.value
                    )
                )
            )
        }
    }

}
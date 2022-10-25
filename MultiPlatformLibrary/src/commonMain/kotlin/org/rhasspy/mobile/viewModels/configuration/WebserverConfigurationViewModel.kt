package org.rhasspy.mobile.viewModels.configuration

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.combineAny
import org.rhasspy.mobile.combineStateNotEquals
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.settings.ConfigurationSettings

class WebserverConfigurationViewModel : ViewModel() {

    //unsaved data
    private val _isHttpServerEnabled = MutableStateFlow(ConfigurationSettings.isHttpServerEnabled.value)
    private val _httpServerPort = MutableStateFlow(ConfigurationSettings.httpServerPort.value)
    private val _isHttpServerSSLEnabled = MutableStateFlow(ConfigurationSettings.isHttpServerSSLEnabled.value)

    //unsaved ui data
    val isHttpServerEnabled = _isHttpServerEnabled.readOnly
    val httpServerPort = _httpServerPort.readOnly
    val isHttpServerSSLEnabled = _isHttpServerSSLEnabled.readOnly
    val isHttpServerSettingsVisible = _isHttpServerEnabled.readOnly
    val isHttpServerSSLCertificateVisible = _isHttpServerSSLEnabled.readOnly

    val hasUnsavedChanges = combineAny(
        combineStateNotEquals(_isHttpServerEnabled, ConfigurationSettings.isHttpServerEnabled.data),
        combineStateNotEquals(_httpServerPort, ConfigurationSettings.httpServerPort.data),
        combineStateNotEquals(_isHttpServerSSLEnabled, ConfigurationSettings.isHttpServerSSLEnabled.data)
    )

    //toggle HTTP Server enabled
    fun toggleHttpServerEnabled(enabled: Boolean) {
        _isHttpServerEnabled.value = enabled
    }

    //edit port
    fun changeHttpServerPort(port: String) {
        _httpServerPort.value = port
    }

    //Toggle http server ssl enabled
    fun toggleHttpServerSSLEnabled(enabled: Boolean) {
        _isHttpServerSSLEnabled.value = enabled
    }

    /**
     * save data configuration
     */
    fun save() {
        ConfigurationSettings.isHttpServerEnabled.value = _isHttpServerEnabled.value
        ConfigurationSettings.httpServerPort.value = _httpServerPort.value
        ConfigurationSettings.isHttpServerSSLEnabled.value = _isHttpServerSSLEnabled.value
    }

    fun discard() {
        _isHttpServerEnabled.value = ConfigurationSettings.isHttpServerEnabled.value
        _httpServerPort.value = ConfigurationSettings.httpServerPort.value
        _isHttpServerSSLEnabled.value = ConfigurationSettings.isHttpServerSSLEnabled.value
    }

    /**
     * test unsaved data configuration
     */
    fun test() {

    }

}
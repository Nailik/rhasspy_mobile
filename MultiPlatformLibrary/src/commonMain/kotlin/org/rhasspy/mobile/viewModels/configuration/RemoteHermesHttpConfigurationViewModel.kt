package org.rhasspy.mobile.viewModels.configuration

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.combineAny
import org.rhasspy.mobile.combineStateNotEquals
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.state.ServiceState
import org.rhasspy.mobile.settings.ConfigurationSettings

class RemoteHermesHttpConfigurationViewModel : ViewModel(), IConfigurationViewModel {

    //unsaved data
    private val _httpServerEndpoint = MutableStateFlow(ConfigurationSettings.httpServerEndpoint.value)
    private val _isHttpSSLVerificationDisabled = MutableStateFlow(ConfigurationSettings.isHttpSSLVerificationDisabled.value)

    //unsaved ui data
    val httpServerEndpoint = _httpServerEndpoint.readOnly
    val isHttpSSLVerificationDisabled = _isHttpSSLVerificationDisabled.readOnly

    override val isTestingEnabled = MutableStateFlow(true)
    override val testState: StateFlow<List<ServiceState>> = MutableStateFlow(listOf())

    override val hasUnsavedChanges = combineAny(
        combineStateNotEquals(_httpServerEndpoint, ConfigurationSettings.httpServerEndpoint.data),
        combineStateNotEquals(_isHttpSSLVerificationDisabled, ConfigurationSettings.isHttpSSLVerificationDisabled.data)
    )

    //set new http server endpoint
    fun updateHttpServerEndpoint(endpoint: String) {
        _httpServerEndpoint.value = endpoint
    }

    //set new intent recognition option
    fun toggleHttpSSLVerificationDisabled(disabled: Boolean) {
        _isHttpSSLVerificationDisabled.value = disabled
    }


    /**
     * save data configuration
     */
    override fun save() {
        ConfigurationSettings.httpServerEndpoint.value = _httpServerEndpoint.value
        ConfigurationSettings.isHttpSSLVerificationDisabled.value = _isHttpSSLVerificationDisabled.value
    }

    /**
     * undo all changes
     */
    override fun discard() {
        _httpServerEndpoint.value = ConfigurationSettings.httpServerEndpoint.value
        _isHttpSSLVerificationDisabled.value = ConfigurationSettings.isHttpSSLVerificationDisabled.value
    }

    /**
     * test unsaved data configuration
     */
    override fun test() {
        //check if connection is possible
        //TODO default and custom port?
    }

    override fun stopTest() {

    }

}
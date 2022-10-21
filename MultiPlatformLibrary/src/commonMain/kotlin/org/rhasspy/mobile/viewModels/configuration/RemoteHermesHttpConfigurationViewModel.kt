package org.rhasspy.mobile.viewModels.configuration

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.settings.ConfigurationSettings

class RemoteHermesHttpConfigurationViewModel : ViewModel() {

    //unsaved data
    private val _isHttpSSLVerificationEnabled = MutableStateFlow(ConfigurationSettings.isHttpSSLVerificationEnabled.value)

    //unsaved ui data
    val isHttpSSLVerificationEnabled = _isHttpSSLVerificationEnabled.readOnly

    //set new intent recognition option
    fun toggleHttpSSLVerificationEnabled(enabled: Boolean) {
        _isHttpSSLVerificationEnabled.value = enabled
    }

    /**
     * save data configuration
     */
    fun save() {
        ConfigurationSettings.isHttpSSLVerificationEnabled.value = _isHttpSSLVerificationEnabled.value
    }

    /**
     * test unsaved data configuration
     */
    fun test() {

    }

}
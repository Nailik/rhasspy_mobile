package org.rhasspy.mobile.viewModels.configuration

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.combineAny
import org.rhasspy.mobile.combineStateNotEquals
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.settings.ConfigurationSettings

class RemoteHermesHttpConfigurationViewModel : ViewModel() {

    //unsaved data
    private val _isHttpSSLVerificationEnabled = MutableStateFlow(ConfigurationSettings.isHttpSSLVerificationEnabled.value)

    //unsaved ui data
    val isHttpSSLVerificationEnabled = _isHttpSSLVerificationEnabled.readOnly

    val hasUnsavedChanges = combineAny(
        combineStateNotEquals(_isHttpSSLVerificationEnabled, ConfigurationSettings.isHttpSSLVerificationEnabled.data)
    )

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

    fun discard() {
        _isHttpSSLVerificationEnabled.value = ConfigurationSettings.isHttpSSLVerificationEnabled.value
    }

    /**
     * test unsaved data configuration
     */
    fun test() {

    }

}
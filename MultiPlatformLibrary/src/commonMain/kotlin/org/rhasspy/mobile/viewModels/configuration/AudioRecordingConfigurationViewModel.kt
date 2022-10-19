package org.rhasspy.mobile.viewModels.configuration

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.settings.ConfigurationSettings

class AudioRecordingConfigurationViewModel : ViewModel() {

    //unsaved data
    private val _isUdpOutputEnabled = MutableStateFlow(ConfigurationSettings.isUdpOutputEnabled.value)
    private val _udpOutputHost = MutableStateFlow(ConfigurationSettings.udpOutputHost.value)
    private val _udpOutputPort = MutableStateFlow(ConfigurationSettings.udpOutputPort.value)

    //unsaved ui data
    val isUdpOutputEnabled = _isUdpOutputEnabled.readOnly
    val udpOutputHost = _udpOutputHost.readOnly
    val udpOutputPort = _udpOutputPort.readOnly

    //show udp host and port settings
    val isOutputSettingsVisible = _isUdpOutputEnabled.readOnly

    //update if udp output is enabled
    fun toggleUdpOutputEnabled(value: Boolean) {
        _isUdpOutputEnabled.value = value
    }

    //edit udp host
    fun changeUdpOutputHost(host: String) {
        _udpOutputHost.value = host
    }

    //edit udp port
    fun changeUdpOutputPort(port: String) {
        _udpOutputPort.value = port
    }

    /**
     * save data configuration
     */
    fun save() {
        ConfigurationSettings.isUdpOutputEnabled.data.value = _isUdpOutputEnabled.value
        ConfigurationSettings.udpOutputHost.data.value = _udpOutputHost.value
        ConfigurationSettings.udpOutputPort.data.value = _udpOutputPort.value
    }

    /**
     * test unsaved data configuration
     */
    fun test() {

    }

}
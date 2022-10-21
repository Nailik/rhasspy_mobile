package org.rhasspy.mobile.viewModels

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.MqttService
import org.rhasspy.mobile.settings.ConfigurationSettings

class ConfigurationScreenViewModel : ViewModel() {

    private val _siteId = MutableStateFlow(ConfigurationSettings.siteId.value)
    val siteId = _siteId.readOnly
    val isHttpServerEnabled = ConfigurationSettings.isHttpServerEnabled.data
    val isHttpSSLVerificationEnabled = ConfigurationSettings.isHttpSSLVerificationEnabled.data
    val isMQTTConnected = MqttService.isConnected
    val isUdpOutputEnabled = ConfigurationSettings.isUdpOutputEnabled.data
    val wakeWordOption = ConfigurationSettings.wakeWordOption.data
    val speechToTextOption = ConfigurationSettings.speechToTextOption.data
    val intentRecognitionOption = ConfigurationSettings.intentRecognitionOption.data
    val textToSpeechOption = ConfigurationSettings.textToSpeechOption.data
    val audioPlayingOption = ConfigurationSettings.audioPlayingOption.data
    val dialogueManagementOption = ConfigurationSettings.dialogueManagementOption.data
    val intentHandlingOption = ConfigurationSettings.intentHandlingOption.data

    fun changeSiteId(siteId: String) {
        _siteId.value = siteId
    }

    init{
        println("init")
    }

}
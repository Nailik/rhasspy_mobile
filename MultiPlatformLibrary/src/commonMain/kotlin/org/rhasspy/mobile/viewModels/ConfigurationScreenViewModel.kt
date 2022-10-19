package org.rhasspy.mobile.viewModels

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.MqttService
import org.rhasspy.mobile.settings.ConfigurationSettings

class ConfigurationScreenViewModel : ViewModel() {

    private val _siteId = ConfigurationSettings.siteId.data
    val siteId = _siteId.readOnly
    val isHttpServerEnabled = ConfigurationSettings.isHttpServerEnabled.data.readOnly
    val isHttpSSLVerificationEnabled = ConfigurationSettings.isHttpSSLVerificationEnabled.data.readOnly
    val isMQTTConnected = MqttService.isConnected
    val isUdpOutputEnabled = ConfigurationSettings.isUdpOutputEnabled.data.readOnly
    val wakeWordOption = ConfigurationSettings.wakeWordOption.data.readOnly
    val speechToTextOption = ConfigurationSettings.speechToTextOption.data.readOnly
    val intentRecognitionOption = ConfigurationSettings.intentRecognitionOption.data.readOnly
    val textToSpeechOption = ConfigurationSettings.textToSpeechOption.data.readOnly
    val audioPlayingOption = ConfigurationSettings.audioPlayingOption.data.readOnly
    val dialogueManagementOption = ConfigurationSettings.dialogueManagementOption.data.readOnly
    val intentHandlingOption = ConfigurationSettings.intentHandlingOption.data.readOnly

    fun changeSiteId(siteId: String) {
        _siteId.value = siteId
    }

}
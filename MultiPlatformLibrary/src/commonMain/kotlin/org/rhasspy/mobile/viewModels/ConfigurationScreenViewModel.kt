package org.rhasspy.mobile.viewModels

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.mqtt.MqttService
import org.rhasspy.mobile.settings.ConfigurationSettings

class ConfigurationScreenViewModel : ViewModel(), KoinComponent {

    val siteId = ConfigurationSettings.siteId.data
    val isHttpServerEnabled = ConfigurationSettings.isHttpServerEnabled.data
    val isHttpSSLVerificationEnabled = ConfigurationSettings.isHttpSSLVerificationDisabled.data
    val isMQTTConnected get() = get<MqttService>().isConnected
    val wakeWordOption = ConfigurationSettings.wakeWordOption.data
    val speechToTextOption = ConfigurationSettings.speechToTextOption.data
    val intentRecognitionOption = ConfigurationSettings.intentRecognitionOption.data
    val textToSpeechOption = ConfigurationSettings.textToSpeechOption.data
    val audioPlayingOption = ConfigurationSettings.audioPlayingOption.data
    val dialogManagementOption = ConfigurationSettings.dialogManagementOption.data
    val intentHandlingOption = ConfigurationSettings.intentHandlingOption.data

    val firstErrorIndex = MutableStateFlow(3).readOnly

    fun changeSiteId(siteId: String) {
        ConfigurationSettings.siteId.value = siteId
    }

    init {
        println("init")
    }

}
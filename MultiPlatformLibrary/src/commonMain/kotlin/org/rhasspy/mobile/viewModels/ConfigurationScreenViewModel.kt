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
    val isHttpServerHasError = MutableStateFlow(false).readOnly //TODO

    val isHttpSSLVerificationEnabled = ConfigurationSettings.isHttpSSLVerificationDisabled.data
    val isHttpClientHasError = MutableStateFlow(false).readOnly //TODO

    val isMQTTConnected get() = get<MqttService>().isConnected
    val isMqttHasError = MutableStateFlow(false).readOnly //TODO

    val wakeWordOption = ConfigurationSettings.wakeWordOption.data
    val isWakeWordServiceHasError = MutableStateFlow(false).readOnly //TODO

    val speechToTextOption = ConfigurationSettings.speechToTextOption.data
    val isSpeechToTextHasError = MutableStateFlow(false).readOnly //TODO

    val intentRecognitionOption = ConfigurationSettings.intentRecognitionOption.data
    val isIntentRecognitionHasError = MutableStateFlow(false).readOnly //TODO

    val textToSpeechOption = ConfigurationSettings.textToSpeechOption.data
    val isTextToSpeechHasError = MutableStateFlow(false).readOnly //TODO

    val audioPlayingOption = ConfigurationSettings.audioPlayingOption.data
    val isAudioPlayingHasError = MutableStateFlow(false).readOnly //TODO

    val dialogManagementOption = ConfigurationSettings.dialogManagementOption.data
    val isDialogManagementHasError = MutableStateFlow(false).readOnly //TODO

    val intentHandlingOption = ConfigurationSettings.intentHandlingOption.data
    val isIntentHandlingHasError = MutableStateFlow(false).readOnly //TODO

    val firstErrorIndex = MutableStateFlow(3).readOnly

    fun changeSiteId(siteId: String) {
        ConfigurationSettings.siteId.value = siteId
    }

    init {
        println("init")
    }

}
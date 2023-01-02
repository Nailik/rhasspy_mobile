package org.rhasspy.mobile.viewmodel.screens

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.middleware.ServiceState
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.mqtt.MqttService
import org.rhasspy.mobile.settings.ConfigurationSetting

class ConfigurationScreenViewModel : ViewModel(), KoinComponent {

    val siteId = ConfigurationSetting.siteId.data

    val isHttpServerEnabled = ConfigurationSetting.isHttpServerEnabled.data
    val isHttpServerHasError = MutableStateFlow(ServiceState.Loading).readOnly //TODO

    val isHttpSSLVerificationEnabled = ConfigurationSetting.isHttpClientSSLVerificationDisabled.data
    val isHttpClientHasError = MutableStateFlow(ServiceState.Error()).readOnly //TODO

    val isMQTTConnected get() = get<MqttService>().isConnected
    val isMqttHasError = MutableStateFlow(ServiceState.Pending).readOnly //TODO

    val wakeWordOption = ConfigurationSetting.wakeWordOption.data
    val isWakeWordServiceHasError = MutableStateFlow(ServiceState.Success()).readOnly //TODO

    val speechToTextOption = ConfigurationSetting.speechToTextOption.data
    val isSpeechToTextHasError = MutableStateFlow(ServiceState.Warning()).readOnly //TODO

    val intentRecognitionOption = ConfigurationSetting.intentRecognitionOption.data
    val isIntentRecognitionHasError = MutableStateFlow(ServiceState.Disabled).readOnly //TODO

    val textToSpeechOption = ConfigurationSetting.textToSpeechOption.data
    val isTextToSpeechHasError = MutableStateFlow(ServiceState.Loading).readOnly //TODO

    val audioPlayingOption = ConfigurationSetting.audioPlayingOption.data
    val isAudioPlayingHasError = MutableStateFlow(ServiceState.Loading).readOnly //TODO

    val dialogManagementOption = ConfigurationSetting.dialogManagementOption.data
    val isDialogManagementHasError = MutableStateFlow(ServiceState.Loading).readOnly //TODO

    val intentHandlingOption = ConfigurationSetting.intentHandlingOption.data
    val isIntentHandlingHasError = MutableStateFlow(ServiceState.Loading).readOnly //TODO

    val firstErrorIndex = MutableStateFlow(3).readOnly

    fun changeSiteId(siteId: String) {
        ConfigurationSetting.siteId.value = siteId
    }

    init {
        println("init")
    }

}
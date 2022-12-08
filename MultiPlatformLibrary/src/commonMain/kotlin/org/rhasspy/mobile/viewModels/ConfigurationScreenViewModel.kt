package org.rhasspy.mobile.viewModels

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.middleware.EventState
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.mqtt.MqttService
import org.rhasspy.mobile.settings.ConfigurationSettings

class ConfigurationScreenViewModel : ViewModel(), KoinComponent {

    val siteId = ConfigurationSettings.siteId.data

    val isHttpServerEnabled = ConfigurationSettings.isHttpServerEnabled.data
    val isHttpServerHasError = MutableStateFlow(EventState.Loading).readOnly //TODO

    val isHttpSSLVerificationEnabled = ConfigurationSettings.isHttpSSLVerificationDisabled.data
    val isHttpClientHasError = MutableStateFlow(EventState.Error()).readOnly //TODO

    val isMQTTConnected get() = get<MqttService>().isConnected
    val isMqttHasError = MutableStateFlow(EventState.Pending).readOnly //TODO

    val wakeWordOption = ConfigurationSettings.wakeWordOption.data
    val isWakeWordServiceHasError = MutableStateFlow(EventState.Success()).readOnly //TODO

    val speechToTextOption = ConfigurationSettings.speechToTextOption.data
    val isSpeechToTextHasError = MutableStateFlow(EventState.Warning()).readOnly //TODO

    val intentRecognitionOption = ConfigurationSettings.intentRecognitionOption.data
    val isIntentRecognitionHasError = MutableStateFlow(EventState.Disabled).readOnly //TODO

    val textToSpeechOption = ConfigurationSettings.textToSpeechOption.data
    val isTextToSpeechHasError = MutableStateFlow(EventState.Loading).readOnly //TODO

    val audioPlayingOption = ConfigurationSettings.audioPlayingOption.data
    val isAudioPlayingHasError = MutableStateFlow(EventState.Loading).readOnly //TODO

    val dialogManagementOption = ConfigurationSettings.dialogManagementOption.data
    val isDialogManagementHasError = MutableStateFlow(EventState.Loading).readOnly //TODO

    val intentHandlingOption = ConfigurationSettings.intentHandlingOption.data
    val isIntentHandlingHasError = MutableStateFlow(EventState.Loading).readOnly //TODO

    val firstErrorIndex = MutableStateFlow(3).readOnly

    fun changeSiteId(siteId: String) {
        ConfigurationSettings.siteId.value = siteId
    }

    init {
        println("init")
    }

}
package org.rhasspy.mobile.viewModels

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.combineState
import org.rhasspy.mobile.data.WakeWordOption
import org.rhasspy.mobile.mqtt.MqttError
import org.rhasspy.mobile.nativeutils.MicrophonePermission
import org.rhasspy.mobile.nativeutils.SettingsUtils
import org.rhasspy.mobile.nativeutils.openLink
import org.rhasspy.mobile.services.MqttService
import org.rhasspy.mobile.settings.ConfigurationSettings
import org.rhasspy.mobile.settings.Element

class ConfigurationScreenViewModel : ViewModel() {

    val siteId = Element(ConfigurationSettings.siteId)
    val audioPlayingOption = Element(ConfigurationSettings.audioPlayingOption)
    val audioPlayingEndpoint = Element(ConfigurationSettings.audioPlayingEndpoint)
    val isUdpOutputEnabled = Element(ConfigurationSettings.isUdpOutputEnabled)
    val udpOutputHost = Element(ConfigurationSettings.udpOutputHost)
    val udpOutputPort = Element(ConfigurationSettings.udpOutputPort)
    val dialogueManagementOption = Element(ConfigurationSettings.dialogueManagementOption)
    val intentHandlingOption = Element(ConfigurationSettings.intentHandlingOption)
    val intentHandlingEndpoint = Element(ConfigurationSettings.intentHandlingEndpoint)
    val intentHandlingHassUrl = Element(ConfigurationSettings.intentHandlingHassUrl)
    val intentHandlingHassAccessToken = Element(ConfigurationSettings.intentHandlingHassAccessToken)
    val isIntentHandlingHassEvent = Element(ConfigurationSettings.isIntentHandlingHassEvent)
    val intentRecognitionOption = Element(ConfigurationSettings.intentRecognitionOption)
    val intentRecognitionEndpoint = Element(ConfigurationSettings.intentRecognitionEndpoint)
    val isMqttEnabled = Element(ConfigurationSettings.isMqttEnabled)
    val mqttHost = Element(ConfigurationSettings.mqttHost)
    val mqttPort = Element(ConfigurationSettings.mqttPort)
    val mqttUserName = Element(ConfigurationSettings.mqttUserName)
    val mqttPassword = Element(ConfigurationSettings.mqttPassword)
    val isMqttSSLEnabled = Element(ConfigurationSettings.isMqttSSLEnabled)
    val mqttConnectionTimeout = Element(ConfigurationSettings.mqttConnectionTimeout)
    val mqttKeepAliveInterval = Element(ConfigurationSettings.mqttKeepAliveInterval)
    val mqttRetryInterval = Element(ConfigurationSettings.mqttRetryInterval)
    val isHttpSSLVerificationEnabled = Element(ConfigurationSettings.isHttpSSLVerificationEnabled)
    val speechToTextOption = Element(ConfigurationSettings.speechToTextOption)
    val speechToTextHttpEndpoint = Element(ConfigurationSettings.speechToTextHttpEndpoint)
    val textToSpeechOption = Element(ConfigurationSettings.textToSpeechOption)
    val textToSpeechEndpoint = Element(ConfigurationSettings.textToSpeechEndpoint)
    val wakeWordPorcupineAccessToken = Element(ConfigurationSettings.wakeWordPorcupineAccessToken)
    val wakeWordPorcupineKeywordOption = Element(ConfigurationSettings.wakeWordPorcupineKeywordOption)
    val wakeWordPorcupineKeywordOptions = Element(ConfigurationSettings.wakeWordPorcupineKeywordOptions)
    val wakeWordPorcupineLanguage = Element(ConfigurationSettings.wakeWordPorcupineLanguage)
    val wakeWordPorcupineKeywordSensitivity = Element(ConfigurationSettings.wakeWordPorcupineKeywordSensitivity)
    val isHttpServerEnabled = Element(ConfigurationSettings.isHttpServerEnabled)
    val httpServerPort = Element(ConfigurationSettings.httpServerPort)
    val isHttpServerSSLEnabled = Element(ConfigurationSettings.isHttpServerSSLEnabled)

    private var wakeWordOptionAwaitingPermission: WakeWordOption? = null
    val wakeWordOption = object : Element<WakeWordOption>(ConfigurationSettings.wakeWordOption) {
        override fun set(value: WakeWordOption) {
            if (value == WakeWordOption.Porcupine || value == WakeWordOption.MQTT) {
                //permission necessary
                if (!MicrophonePermission.granted.value) {
                    if (MicrophonePermission.shouldShowInformationDialog()) {
                        //store wake word option
                        wakeWordOptionAwaitingPermission = value
                        //show info dialog
                        _isWakeWordMicrophonePermissionRequestDialogShown.value = true
                    } else {
                        //store value
                        ConfigurationSettings.wakeWordOption.data.value = value
                        //delete awaiting value
                        wakeWordOptionAwaitingPermission = null
                    }
                }
            }
        }
    }

    private val _isWakeWordMicrophonePermissionRequestDialogShown = MutableStateFlow(false)
    val isWakeWordMicrophonePermissionRequestDialogShown: StateFlow<Boolean> get() = _isWakeWordMicrophonePermissionRequestDialogShown

    fun wakeWordMicrophonePermissionRequestDialogResult(result: Boolean, showSnackbar: suspend CoroutineScope.() -> Unit) {
        //hide dialog
        _isWakeWordMicrophonePermissionRequestDialogShown.value = false

        if (result) {
            //request permission
            MicrophonePermission.requestPermission(false) { granted ->
                if (granted) {
                    wakeWordOptionAwaitingPermission?.also { wakeWordOption.set(it) }
                }
            }
        } else {
            //show snack bar
            viewModelScope.launch { showSnackbar() }
        }
    }

    fun wakeWordMicrophonePermissionRequestSnackbarResult(result: Boolean) {
        if (result) {
            //request permission
            MicrophonePermission.requestPermission(false) { granted ->
                if (granted) {
                    wakeWordOptionAwaitingPermission?.also { wakeWordOption.set(it) }
                }
            }
        }
    }


    fun openPicoVoiceConsole() {
        openLink("https://console.picovoice.ai")
    }

    val isMQTTTestEnabled: StateFlow<Boolean>
        get() = combineState(ConfigurationSettings.mqttHost.data, ConfigurationSettings.mqttPort.data)
        { host, port ->
            host.isNotEmpty() && port.isNotEmpty()
        }


    val isMQTTConnected = MqttService.isConnected

    private val testingMqttConnection = MutableStateFlow(false)
    val isTestingMqttConnection: StateFlow<Boolean> get() = testingMqttConnection
    private val _testingMqttError = MutableStateFlow<MqttError?>(null)
    val testingMqttError: StateFlow<MqttError?> get() = _testingMqttError

    init {
        viewModelScope.launch {
            ConfigurationSettings.mqttHost.data.collect {
                _testingMqttError.value = null
            }
        }

        viewModelScope.launch {
            ConfigurationSettings.mqttPort.data.collect {
                _testingMqttError.value = null
            }
        }

        viewModelScope.launch {
            ConfigurationSettings.mqttUserName.data.collect {
                _testingMqttError.value = null
            }
        }

        viewModelScope.launch {
            ConfigurationSettings.mqttPassword.data.collect {
                _testingMqttError.value = null
            }
        }
    }

    fun selectPorcupineWakeWordFile() = SettingsUtils.selectPorcupineFile { fileName ->
        fileName?.also {
            ConfigurationSettings.wakeWordPorcupineKeywordOptions.data.value =
                ConfigurationSettings.wakeWordPorcupineKeywordOptions.data.value.toMutableList()
                    .apply {
                        this.add(it)
                    }.toSet()
            ConfigurationSettings.wakeWordPorcupineKeywordOption.data.value =
                ConfigurationSettings.wakeWordPorcupineKeywordOptions.data.value.size - 1
        }
    }

    fun testMqttConnection() {
        if(!testingMqttConnection.value) {
            //show loading
            testingMqttConnection.value = true
            viewModelScope.launch(Dispatchers.Main) {
                _testingMqttError.value = MqttService.testConnection()
                testingMqttConnection.value = false
            }
        }
        //disable editing of mqtt settings
        //show result
    }

}
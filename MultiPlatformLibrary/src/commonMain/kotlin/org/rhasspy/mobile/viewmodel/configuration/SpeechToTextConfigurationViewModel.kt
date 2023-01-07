package org.rhasspy.mobile.viewmodel.configuration

import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.*
import org.rhasspy.mobile.logger.LogType
import org.rhasspy.mobile.services.httpclient.HttpClientPath
import org.rhasspy.mobile.services.httpclient.HttpClientServiceParams
import org.rhasspy.mobile.services.mqtt.MqttServiceParams
import org.rhasspy.mobile.services.speechtotext.SpeechToTextService
import org.rhasspy.mobile.services.speechtotext.SpeechToTextServiceParams
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.settings.option.SpeechToTextOption
import org.rhasspy.mobile.viewmodel.configuration.test.SpeechToTextConfigurationTest

class SpeechToTextConfigurationViewModel : IConfigurationViewModel() {

    //for testing
    override val testRunner by inject<SpeechToTextConfigurationTest>()
    override val logType = LogType.SpeechToTextService
    override val serviceState get() = get<SpeechToTextService>().serviceState

    val isRecordingAudio = testRunner.isRecording

    //unsaved data
    private val _speechToTextOption = MutableStateFlow(ConfigurationSetting.speechToTextOption.value)
    private val _isUseCustomSpeechToTextHttpEndpoint = MutableStateFlow(ConfigurationSetting.isUseCustomSpeechToTextHttpEndpoint.value)
    private val _isUseSpeechToTextMqttSilenceDetection = MutableStateFlow(ConfigurationSetting.isUseSpeechToTextMqttSilenceDetection.value)
    private val _speechToTextHttpEndpoint = MutableStateFlow(ConfigurationSetting.speechToTextHttpEndpoint.value)
    private val _speechToTextUdpOutputHost = MutableStateFlow(ConfigurationSetting.wakeWordUdpOutputHost.value)
    private val _speechToTextUdpOutputPort = MutableStateFlow(ConfigurationSetting.wakeWordUdpOutputPort.value)
    private val _speechToTextUdpOutputPortText = MutableStateFlow(ConfigurationSetting.wakeWordUdpOutputPort.value.toString())

    //unsaved ui data
    val speechToTextOption = _speechToTextOption.readOnly
    val speechToTextHttpEndpoint = combineState(
        _isUseCustomSpeechToTextHttpEndpoint,
        _speechToTextHttpEndpoint
    ) { useCustomSpeechToTextHttpEndpoint, speechToTextHttpEndpoint ->
        if (useCustomSpeechToTextHttpEndpoint) {
            speechToTextHttpEndpoint
        } else {
            HttpClientPath.SpeechToText.fromBaseConfiguration()
        }
    }
    val isUseCustomSpeechToTextHttpEndpoint = _isUseCustomSpeechToTextHttpEndpoint.readOnly
    val isUseSpeechToTextMqttSilenceDetection = _isUseSpeechToTextMqttSilenceDetection.readOnly
    val isSpeechToTextHttpEndpointChangeEnabled = isUseCustomSpeechToTextHttpEndpoint
    val speechToTextUdpOutputHost = _speechToTextUdpOutputHost.readOnly
    val speechToTextUdpOutputPortText = _speechToTextUdpOutputPortText.readOnly

    override val isTestingEnabled = _speechToTextOption.mapReadonlyState { it != SpeechToTextOption.Disabled }

    override val hasUnsavedChanges = combineAny(
        combineStateNotEquals(_speechToTextOption, ConfigurationSetting.speechToTextOption.data),
        combineStateNotEquals(_isUseCustomSpeechToTextHttpEndpoint, ConfigurationSetting.isUseCustomSpeechToTextHttpEndpoint.data),
        combineStateNotEquals(_isUseSpeechToTextMqttSilenceDetection, ConfigurationSetting.isUseSpeechToTextMqttSilenceDetection.data),
        combineStateNotEquals(_speechToTextHttpEndpoint, ConfigurationSetting.speechToTextHttpEndpoint.data),
        combineStateNotEquals(_speechToTextUdpOutputHost, ConfigurationSetting.speechToTextUdpOutputHost.data),
        combineStateNotEquals(_speechToTextUdpOutputPort, ConfigurationSetting.speechToTextUdpOutputPort.data)
    )

    //show endpoint settings
    fun isSpeechToTextHttpSettingsVisible(option: SpeechToTextOption): Boolean {
        return option == SpeechToTextOption.RemoteHTTP
    }

    //show mqtt settings
    fun isSpeechToTextMqttSettingsVisible(option: SpeechToTextOption): Boolean {
        return option == SpeechToTextOption.RemoteMQTT
    }

    //show udp settings
    fun isSpeechToTextUdpSettingsVisible(option: SpeechToTextOption): Boolean {
        return option == SpeechToTextOption.Udp
    }

    //all options
    val speechToTextOptions = SpeechToTextOption::values

    //set new speech to text option
    fun selectSpeechToTextOption(option: SpeechToTextOption) {
        _speechToTextOption.value = option
    }

    //toggle if custom endpoint is used
    fun toggleUseCustomHttpEndpoint(enabled: Boolean) {
        _isUseCustomSpeechToTextHttpEndpoint.value = enabled
    }

    //toggle if mqtt silence detection is used
    fun toggleUseSpeechToTextMqttSilenceDetection(enabled: Boolean) {
        _isUseSpeechToTextMqttSilenceDetection.value = enabled
    }

    //set new speech to text http endpoint
    fun updateSpeechToTextHttpEndpoint(endpoint: String) {
        _speechToTextHttpEndpoint.value = endpoint
    }

    //edit udp port
    fun changeUdpOutputPort(port: String) {
        val text = port.replace("""[-,. ]""".toRegex(), "")
        _speechToTextUdpOutputPortText.value = text
        _speechToTextUdpOutputPort.value = text.toIntOrNull() ?: 0
    }

    //edit udp host
    fun changeUdpOutputHost(host: String) {
        _speechToTextUdpOutputHost.value = host
    }

    /**
     * save data configuration
     */
    override fun onSave() {
        ConfigurationSetting.speechToTextOption.value = _speechToTextOption.value
        ConfigurationSetting.isUseCustomSpeechToTextHttpEndpoint.value = _isUseCustomSpeechToTextHttpEndpoint.value
        ConfigurationSetting.isUseSpeechToTextMqttSilenceDetection.value = _isUseSpeechToTextMqttSilenceDetection.value
    }

    /**
     * undo all changes
     */
    override fun discard() {
        _speechToTextOption.value = ConfigurationSetting.speechToTextOption.value
        _isUseCustomSpeechToTextHttpEndpoint.value = ConfigurationSetting.isUseCustomSpeechToTextHttpEndpoint.value
        _isUseSpeechToTextMqttSilenceDetection.value = ConfigurationSetting.isUseSpeechToTextMqttSilenceDetection.value
        _speechToTextHttpEndpoint.value = ConfigurationSetting.speechToTextHttpEndpoint.value
        _speechToTextUdpOutputHost.value = ConfigurationSetting.speechToTextUdpOutputHost.value
        _speechToTextUdpOutputPort.value = ConfigurationSetting.speechToTextUdpOutputPort.value
    }

    override fun initializeTestParams() {
        get<MqttServiceParams> {
            parametersOf(
                MqttServiceParams(
                    isUseSpeechToTextMqttSilenceDetection = _isUseSpeechToTextMqttSilenceDetection.value
                )
            )
        }

        get<SpeechToTextServiceParams> {
            parametersOf(
                SpeechToTextServiceParams(
                    speechToTextOption = _speechToTextOption.value,
                    speechToTextUdpOutputHost = _speechToTextUdpOutputHost.value,
                    speechToTextUdpOutputPort = _speechToTextUdpOutputPort.value
                )
            )
        }

        get<HttpClientServiceParams> {
            parametersOf(
                HttpClientServiceParams(
                    isUseCustomSpeechToTextHttpEndpoint = _isUseCustomSpeechToTextHttpEndpoint.value,
                    speechToTextHttpEndpoint = _speechToTextHttpEndpoint.value
                )
            )
        }
    }

    override fun runTest() = testRunner.toggleRecording()

}
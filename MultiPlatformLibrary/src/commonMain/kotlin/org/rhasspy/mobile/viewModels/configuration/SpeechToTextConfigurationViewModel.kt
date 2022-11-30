package org.rhasspy.mobile.viewModels.configuration

import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.*
import org.rhasspy.mobile.data.SpeechToTextOptions
import org.rhasspy.mobile.services.httpclient.HttpClientPath
import org.rhasspy.mobile.services.httpclient.HttpClientServiceParams
import org.rhasspy.mobile.services.mqtt.MqttServiceParams
import org.rhasspy.mobile.services.rhasspyactions.RhasspyActionsServiceParams
import org.rhasspy.mobile.settings.ConfigurationSettings
import org.rhasspy.mobile.viewModels.configuration.test.SpeechToTextConfigurationTest

class SpeechToTextConfigurationViewModel : IConfigurationViewModel() {

    //for testing
    override val testRunner by inject<SpeechToTextConfigurationTest>()
    val isRecordingAudio = testRunner.isRecording

    //unsaved data
    private val _speechToTextOption = MutableStateFlow(ConfigurationSettings.speechToTextOption.value)
    private val _isUseCustomSpeechToTextHttpEndpoint = MutableStateFlow(ConfigurationSettings.isUseCustomSpeechToTextHttpEndpoint.value)
    private val _isUseSpeechToTextMqttSilenceDetection = MutableStateFlow(ConfigurationSettings.isUseSpeechToTextMqttSilenceDetection.value)
    private val _speechToTextHttpEndpoint = MutableStateFlow(ConfigurationSettings.speechToTextHttpEndpoint.value)

    //unsaved ui data
    val speechToTextOption = _speechToTextOption.readOnly
    val speechToTextHttpEndpoint = combineState(_isUseCustomSpeechToTextHttpEndpoint, _speechToTextHttpEndpoint) { useCustomSpeechToTextHttpEndpoint,
                                                                                                                   speechToTextHttpEndpoint ->
        if (useCustomSpeechToTextHttpEndpoint) {
            speechToTextHttpEndpoint
        } else {
            "${ConfigurationSettings.httpServerEndpoint.value}${HttpClientPath.SpeechToText}"
        }
    }
    val isUseCustomSpeechToTextHttpEndpoint = _isUseCustomSpeechToTextHttpEndpoint.readOnly
    val isUseSpeechToTextMqttSilenceDetection = _isUseSpeechToTextMqttSilenceDetection.readOnly
    val isSpeechToTextHttpEndpointChangeEnabled = isUseCustomSpeechToTextHttpEndpoint

    override val isTestingEnabled = _speechToTextOption.mapReadonlyState { it != SpeechToTextOptions.Disabled }

    override val hasUnsavedChanges = combineAny(
        combineStateNotEquals(_speechToTextOption, ConfigurationSettings.speechToTextOption.data),
        combineStateNotEquals(_isUseCustomSpeechToTextHttpEndpoint, ConfigurationSettings.isUseCustomSpeechToTextHttpEndpoint.data),
        combineStateNotEquals(_isUseSpeechToTextMqttSilenceDetection, ConfigurationSettings.isUseSpeechToTextMqttSilenceDetection.data),
        combineStateNotEquals(_speechToTextHttpEndpoint, ConfigurationSettings.speechToTextHttpEndpoint.data)
    )

    //show endpoint settings
    fun isSpeechToTextHttpSettingsVisible(option: SpeechToTextOptions): Boolean {
        return option == SpeechToTextOptions.RemoteHTTP
    }

    //show mqtt settings
    fun isSpeechToTextMqttSettingsVisible(option: SpeechToTextOptions): Boolean {
        return option == SpeechToTextOptions.RemoteMQTT
    }

    //all options
    val speechToTextOptions = SpeechToTextOptions::values

    //set new speech to text option
    fun selectSpeechToTextOption(option: SpeechToTextOptions) {
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

    /**
     * save data configuration
     */
    override fun onSave() {
        ConfigurationSettings.speechToTextOption.value = _speechToTextOption.value
        ConfigurationSettings.isUseCustomSpeechToTextHttpEndpoint.value = _isUseCustomSpeechToTextHttpEndpoint.value
        ConfigurationSettings.isUseSpeechToTextMqttSilenceDetection.value = _isUseSpeechToTextMqttSilenceDetection.value
        ConfigurationSettings.speechToTextHttpEndpoint.value = _speechToTextHttpEndpoint.value
    }

    /**
     * undo all changes
     */
    override fun discard() {
        _speechToTextOption.value = ConfigurationSettings.speechToTextOption.value
        _isUseCustomSpeechToTextHttpEndpoint.value = ConfigurationSettings.isUseCustomSpeechToTextHttpEndpoint.value
        _isUseSpeechToTextMqttSilenceDetection.value = ConfigurationSettings.isUseSpeechToTextMqttSilenceDetection.value
        _speechToTextHttpEndpoint.value = ConfigurationSettings.speechToTextHttpEndpoint.value
    }

    override fun initializeTestParams() {
        get<MqttServiceParams> {
            parametersOf(
                MqttServiceParams(
                    isUseSpeechToTextMqttSilenceDetection = _isUseSpeechToTextMqttSilenceDetection.value
                )
            )
        }

        get<RhasspyActionsServiceParams> {
            parametersOf(
                RhasspyActionsServiceParams(
                    speechToTextOption = _speechToTextOption.value
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
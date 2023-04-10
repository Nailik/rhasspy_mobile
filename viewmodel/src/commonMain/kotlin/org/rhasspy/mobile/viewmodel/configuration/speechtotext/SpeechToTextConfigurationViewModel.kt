package org.rhasspy.mobile.viewmodel.configuration.speechtotext

import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.data.service.option.SpeechToTextOption
import org.rhasspy.mobile.logic.logger.LogType
import org.rhasspy.mobile.logic.services.httpclient.HttpClientPath
import org.rhasspy.mobile.logic.services.httpclient.HttpClientServiceParams
import org.rhasspy.mobile.logic.services.mqtt.MqttServiceParams
import org.rhasspy.mobile.logic.services.speechtotext.SpeechToTextService
import org.rhasspy.mobile.logic.services.speechtotext.SpeechToTextServiceParams
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.platformspecific.combineState
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewModel

class SpeechToTextConfigurationViewModel : IConfigurationViewModel() {

    //for testing
    override val testRunner by inject<SpeechToTextConfigurationTest>()
    override val logType = LogType.SpeechToTextService
    override val serviceState get() = get<SpeechToTextService>().serviceState

    val isRecordingAudio get() = testRunner.isRecording

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


    //show endpoint settings
    fun isSpeechToTextHttpSettingsVisible(option: SpeechToTextOption): Boolean {
        return option == SpeechToTextOption.RemoteHTTP
    }

    //show mqtt settings
    fun isSpeechToTextMqttSettingsVisible(option: SpeechToTextOption): Boolean {
        return option == SpeechToTextOption.RemoteMQTT
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

    /**
     * save data configuration
     */
    override fun onSave() {
        ConfigurationSetting.speechToTextOption.value = _speechToTextOption.value
        ConfigurationSetting.isUseCustomSpeechToTextHttpEndpoint.value =
            _isUseCustomSpeechToTextHttpEndpoint.value
        ConfigurationSetting.isUseSpeechToTextMqttSilenceDetection.value =
            _isUseSpeechToTextMqttSilenceDetection.value
        ConfigurationSetting.speechToTextHttpEndpoint.value = _speechToTextHttpEndpoint.value
    }

    /**
     * undo all changes
     */
    override fun discard() {
        _speechToTextOption.value = ConfigurationSetting.speechToTextOption.value
        _isUseCustomSpeechToTextHttpEndpoint.value =
            ConfigurationSetting.isUseCustomSpeechToTextHttpEndpoint.value
        _isUseSpeechToTextMqttSilenceDetection.value =
            ConfigurationSetting.isUseSpeechToTextMqttSilenceDetection.value
        _speechToTextHttpEndpoint.value = ConfigurationSetting.speechToTextHttpEndpoint.value
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

    fun toggleRecording() = testRunner.toggleRecording()

}
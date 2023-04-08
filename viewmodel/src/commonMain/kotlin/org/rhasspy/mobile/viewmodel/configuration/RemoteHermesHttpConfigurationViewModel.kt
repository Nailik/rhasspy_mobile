package org.rhasspy.mobile.viewmodel.configuration

import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.data.service.option.IntentRecognitionOption
import org.rhasspy.mobile.data.service.option.SpeechToTextOption
import org.rhasspy.mobile.data.service.option.TextToSpeechOption
import org.rhasspy.mobile.logic.logger.LogType
import org.rhasspy.mobile.logic.services.httpclient.HttpClientService
import org.rhasspy.mobile.logic.services.httpclient.HttpClientServiceParams
import org.rhasspy.mobile.logic.settings.ConfigurationSetting
import org.rhasspy.mobile.platformspecific.combineAny
import org.rhasspy.mobile.platformspecific.combineState
import org.rhasspy.mobile.platformspecific.combineStateNotEquals
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.viewmodel.configuration.event.IConfigurationViewState.IConfigurationEditViewState
import org.rhasspy.mobile.viewmodel.configuration.test.RemoteHermesHttpConfigurationTest

class RemoteHermesHttpConfigurationViewModel : IConfigurationViewModel() {
    override val testRunner by inject<RemoteHermesHttpConfigurationTest>()
    override val logType = LogType.HttpClientService
    override val serviceState get() = get<HttpClientService>().serviceState
    val isRecordingAudio = testRunner.isRecording

    //unsaved data
    private val _httpClientServerEndpointHost =
        MutableStateFlow(ConfigurationSetting.httpClientServerEndpointHost.value)
    private val _httpClientServerEndpointPort =
        MutableStateFlow(ConfigurationSetting.httpClientServerEndpointPort.value)
    private val _httpClientServerEndpointPortText =
        MutableStateFlow(ConfigurationSetting.httpClientServerEndpointPort.value.toString())
    private val _httpClientTimeout = MutableStateFlow(ConfigurationSetting.httpClientTimeout.value)
    private val _httpClientTimeoutText =
        MutableStateFlow(ConfigurationSetting.httpClientTimeout.value.toString())
    private val _isHttpSSLVerificationDisabled =
        MutableStateFlow(ConfigurationSetting.isHttpClientSSLVerificationDisabled.value)

    //unsaved ui data
    val httpClientServerEndpointHost = _httpClientServerEndpointHost.readOnly
    val httpClientServerEndpointPort = _httpClientServerEndpointPortText.readOnly
    val httpClientTimeoutText = _httpClientTimeoutText.readOnly
    val isHttpSSLVerificationDisabled = _isHttpSSLVerificationDisabled.readOnly

    //test
    val isSpeechToTextTestVisible = combineState(
        ConfigurationSetting.speechToTextOption.data,
        ConfigurationSetting.isUseCustomSpeechToTextHttpEndpoint.data
    ) { option, isUseCustomEndpoint ->
        option == SpeechToTextOption.RemoteHTTP && !isUseCustomEndpoint
    }
    val isIntentRecognitionTestVisible = combineState(
        ConfigurationSetting.intentRecognitionOption.data,
        ConfigurationSetting.isUseCustomIntentRecognitionHttpEndpoint.data
    ) { option, isUseCustomEndpoint ->
        option == IntentRecognitionOption.RemoteHTTP && !isUseCustomEndpoint
    }
    val isTextToSpeechTestVisible = combineState(
        ConfigurationSetting.textToSpeechOption.data,
        ConfigurationSetting.isUseCustomSpeechToTextHttpEndpoint.data
    ) { option, isUseCustomEndpoint ->
        option == TextToSpeechOption.RemoteHTTP && !isUseCustomEndpoint
    }

    private val _testIntentRecognitionText = MutableStateFlow("")
    val testIntentRecognitionText = _testIntentRecognitionText.readOnly
    val isIntentRecognitionTestEnabled =
        _testIntentRecognitionText.mapReadonlyState { it.isNotEmpty() }

    private val _testTextToSpeechText = MutableStateFlow("")
    val testTextToSpeechText = _testTextToSpeechText.readOnly
    val isTextToSpeechTestEnabled = _testTextToSpeechText.mapReadonlyState { it.isNotEmpty() }

    private val isTestingEnabled = combineState(
        ConfigurationSetting.speechToTextOption.data,
        ConfigurationSetting.intentRecognitionOption.data,
        ConfigurationSetting.textToSpeechOption.data,
        _httpClientServerEndpointHost
    ) { speechToTextOption, intentRecognitionOption, textToSpeechOption, host ->
        host.isNotBlank() &&
                (speechToTextOption == SpeechToTextOption.RemoteHTTP ||
                        intentRecognitionOption == IntentRecognitionOption.RemoteHTTP ||
                        textToSpeechOption == TextToSpeechOption.RemoteHTTP)
    }

    private val hasUnsavedChanges = combineAny(
        combineStateNotEquals(_httpClientServerEndpointHost, ConfigurationSetting.httpClientServerEndpointHost.data),
        combineStateNotEquals(_httpClientServerEndpointPort, ConfigurationSetting.httpClientServerEndpointPort.data),
        combineStateNotEquals(_httpClientTimeout, ConfigurationSetting.httpClientTimeout.data),
        combineStateNotEquals(_isHttpSSLVerificationDisabled, ConfigurationSetting.isHttpClientSSLVerificationDisabled.data)
    )

    override val configurationEditViewState = combineState(hasUnsavedChanges, isTestingEnabled) { hasUnsavedChanges, isTestingEnabled ->
        IConfigurationEditViewState(
            hasUnsavedChanges = hasUnsavedChanges,
            isTestingEnabled = isTestingEnabled
        )
    }


    //set new http server endpoint host
    fun updateHttpClientServerEndpointHost(endpoint: String) {
        _httpClientServerEndpointHost.value = endpoint
    }

    //set new http server endpoint port
    fun updateHttpClientTimeout(timeout: String) {
        val text = timeout.replace("""[-,. ]""".toRegex(), "")
        _httpClientTimeoutText.value = text
        _httpClientTimeout.value = text.toLongOrNull()
    }

    //set new http server endpoint port
    fun updateHttpClientServerEndpointPort(port: String) {
        val text = port.replace("""[-,. ]""".toRegex(), "")
        _httpClientServerEndpointPortText.value = text
        _httpClientServerEndpointPort.value = text.toIntOrNull() ?: 0
    }

    //set new intent recognition option
    fun toggleHttpSSLVerificationDisabled(disabled: Boolean) {
        _isHttpSSLVerificationDisabled.value = disabled
    }

    //update intent test text
    fun updateTestIntentRecognitionText(text: String) {
        _testIntentRecognitionText.value = text
    }

    //update the test text
    fun updateTestTextToSpeechText(text: String) {
        _testTextToSpeechText.value = text
    }

    /**
     * save data configuration
     */
    override fun onSave() {
        ConfigurationSetting.httpClientServerEndpointHost.value =
            _httpClientServerEndpointHost.value
        ConfigurationSetting.httpClientServerEndpointPort.value =
            _httpClientServerEndpointPort.value
        ConfigurationSetting.isHttpClientSSLVerificationDisabled.value =
            _isHttpSSLVerificationDisabled.value
    }

    /**
     * undo all changes
     */
    override fun discard() {
        _httpClientServerEndpointHost.value =
            ConfigurationSetting.httpClientServerEndpointHost.value
        _httpClientServerEndpointPort.value =
            ConfigurationSetting.httpClientServerEndpointPort.value
        _httpClientServerEndpointPortText.value =
            ConfigurationSetting.httpClientServerEndpointPort.value.toString()
        _isHttpSSLVerificationDisabled.value =
            ConfigurationSetting.isHttpClientSSLVerificationDisabled.value
    }

    override fun initializeTestParams() {
        get<HttpClientServiceParams> {
            parametersOf(
                HttpClientServiceParams(
                    isHttpSSLVerificationDisabled = _isHttpSSLVerificationDisabled.value,
                    httpClientServerEndpointHost = _httpClientServerEndpointHost.value,
                    httpClientServerEndpointPort = _httpClientServerEndpointPort.value
                )
            )
        }
    }

    fun toggleRecording() = testRunner.toggleRecording()

    fun runIntentRecognitionTest() =
        testRunner.startIntentRecognitionTest(_testIntentRecognitionText.value)

    fun runTextToSpeechTest() = testRunner.startTextToSpeechTest(_testTextToSpeechText.value)

}
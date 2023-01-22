package org.rhasspy.mobile.viewmodel.configuration

import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.combineAny
import org.rhasspy.mobile.combineState
import org.rhasspy.mobile.combineStateNotEquals
import org.rhasspy.mobile.logger.LogType
import org.rhasspy.mobile.mapReadonlyState
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.httpclient.HttpClientPath
import org.rhasspy.mobile.services.httpclient.HttpClientServiceParams
import org.rhasspy.mobile.services.texttospeech.TextToSpeechService
import org.rhasspy.mobile.services.texttospeech.TextToSpeechServiceParams
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.settings.option.TextToSpeechOption
import org.rhasspy.mobile.viewmodel.configuration.test.TextToSpeechConfigurationTest

class TextToSpeechConfigurationViewModel : IConfigurationViewModel() {

    override val testRunner by inject<TextToSpeechConfigurationTest>()
    override val logType = LogType.TextToSpeechService
    override val serviceState get() = get<TextToSpeechService>().serviceState

    private val _testTextToSpeechText = MutableStateFlow("")
    val testTextToSpeechText = _testTextToSpeechText.readOnly

    //unsaved data
    private val _textToSpeechOption =
        MutableStateFlow(ConfigurationSetting.textToSpeechOption.value)
    private val _isUseCustomTextToSpeechHttpEndpoint =
        MutableStateFlow(ConfigurationSetting.isUseCustomTextToSpeechHttpEndpoint.value)
    private val _textToSpeechHttpEndpoint =
        MutableStateFlow(ConfigurationSetting.textToSpeechHttpEndpoint.value)

    //unsaved ui data
    val textToSpeechOption = _textToSpeechOption.readOnly
    val textToSpeechHttpEndpoint =
        combineState(
            _isUseCustomTextToSpeechHttpEndpoint,
            _textToSpeechHttpEndpoint
        ) { useCustomTextToSpeechHttpEndpoint,
            speechToTextHttpEndpoint ->
            if (useCustomTextToSpeechHttpEndpoint) {
                speechToTextHttpEndpoint
            } else {
                HttpClientPath.TextToSpeech.fromBaseConfiguration()
            }
        }
    val isUseCustomTextToSpeechHttpEndpoint = _isUseCustomTextToSpeechHttpEndpoint.readOnly
    val isTextToSpeechHttpEndpointChangeEnabled = isUseCustomTextToSpeechHttpEndpoint

    override val isTestingEnabled =
        _textToSpeechOption.mapReadonlyState { it != TextToSpeechOption.Disabled }

    override val hasUnsavedChanges = combineAny(
        combineStateNotEquals(_textToSpeechOption, ConfigurationSetting.textToSpeechOption.data),
        combineStateNotEquals(
            _isUseCustomTextToSpeechHttpEndpoint,
            ConfigurationSetting.isUseCustomTextToSpeechHttpEndpoint.data
        ),
        combineStateNotEquals(
            _textToSpeechHttpEndpoint,
            ConfigurationSetting.textToSpeechHttpEndpoint.data
        )
    )

    //show endpoint settings
    fun isTextToSpeechHttpSettingsVisible(option: TextToSpeechOption): Boolean {
        return option == TextToSpeechOption.RemoteHTTP
    }

    //all options
    val textToSpeechOptions = TextToSpeechOption::values

    //set new text to speech option
    fun selectTextToSpeechOption(option: TextToSpeechOption) {
        _textToSpeechOption.value = option
    }

    //toggle if custom endpoint is used
    fun toggleUseCustomHttpEndpoint(enabled: Boolean) {
        _isUseCustomTextToSpeechHttpEndpoint.value = enabled
    }

    //set new text to speech http endpoint
    fun updateTextToSpeechHttpEndpoint(endpoint: String) {
        _textToSpeechHttpEndpoint.value = endpoint
    }

    //update the test text
    fun updateTestTextToSpeechText(text: String) {
        _testTextToSpeechText.value = text
    }

    /**
     * save data configuration
     */
    override fun onSave() {
        ConfigurationSetting.textToSpeechOption.value = _textToSpeechOption.value
        ConfigurationSetting.isUseCustomTextToSpeechHttpEndpoint.value =
            _isUseCustomTextToSpeechHttpEndpoint.value
        ConfigurationSetting.textToSpeechHttpEndpoint.value = _textToSpeechHttpEndpoint.value
    }

    /**
     * undo all changes
     */
    override fun discard() {
        _textToSpeechOption.value = ConfigurationSetting.textToSpeechOption.value
        _isUseCustomTextToSpeechHttpEndpoint.value =
            ConfigurationSetting.isUseCustomTextToSpeechHttpEndpoint.value
        _textToSpeechHttpEndpoint.value = ConfigurationSetting.textToSpeechHttpEndpoint.value
    }

    override fun initializeTestParams() {
        get<TextToSpeechServiceParams> {
            parametersOf(
                TextToSpeechServiceParams(
                    textToSpeechOption = _textToSpeechOption.value
                )
            )
        }

        get<HttpClientServiceParams> {
            parametersOf(
                HttpClientServiceParams(
                    isUseCustomTextToSpeechHttpEndpoint = _isUseCustomTextToSpeechHttpEndpoint.value,
                    textToSpeechHttpEndpoint = _textToSpeechHttpEndpoint.value
                )
            )
        }
    }

    fun startTextToSpeech() = testRunner.startTextToSpeech(_testTextToSpeechText.value)

}
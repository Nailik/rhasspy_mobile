package org.rhasspy.mobile.viewModels.configuration

import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.*
import org.rhasspy.mobile.data.TextToSpeechOptions
import org.rhasspy.mobile.services.httpclient.HttpClientPath
import org.rhasspy.mobile.services.httpclient.HttpClientServiceParams
import org.rhasspy.mobile.services.rhasspyactions.RhasspyActionsServiceParams
import org.rhasspy.mobile.settings.ConfigurationSettings
import org.rhasspy.mobile.viewModels.configuration.test.TextToSpeechConfigurationTest

class TextToSpeechConfigurationViewModel : IConfigurationViewModel() {

    override val testRunner by inject<TextToSpeechConfigurationTest>()
    override val events = testRunner.events

    //unsaved data
    private val _textToSpeechOption = MutableStateFlow(ConfigurationSettings.textToSpeechOption.value)
    private val _isUseCustomTextToSpeechHttpEndpoint = MutableStateFlow(ConfigurationSettings.isUseCustomTextToSpeechHttpEndpoint.value)
    private val _textToSpeechHttpEndpoint = MutableStateFlow(ConfigurationSettings.textToSpeechHttpEndpoint.value)

    //unsaved ui data
    val textToSpeechOption = _textToSpeechOption.readOnly
    val textToSpeechHttpEndpoint =
        combineState(_isUseCustomTextToSpeechHttpEndpoint, _textToSpeechHttpEndpoint) { useCustomTextToSpeechHttpEndpoint,
                                                                                        speechToTextHttpEndpoint ->
            if (useCustomTextToSpeechHttpEndpoint) {
                speechToTextHttpEndpoint
            } else {
                "${ConfigurationSettings.httpServerEndpoint.value}${HttpClientPath.TextToSpeech}"
            }
        }
    val isUseCustomTextToSpeechHttpEndpoint = _isUseCustomTextToSpeechHttpEndpoint.readOnly
    val isTextToSpeechHttpEndpointChangeEnabled = isUseCustomTextToSpeechHttpEndpoint

    override val isTestingEnabled = _textToSpeechOption.mapReadonlyState { it != TextToSpeechOptions.Disabled }

    override val hasUnsavedChanges = combineAny(
        combineStateNotEquals(_textToSpeechOption, ConfigurationSettings.textToSpeechOption.data),
        combineStateNotEquals(_isUseCustomTextToSpeechHttpEndpoint, ConfigurationSettings.isUseCustomTextToSpeechHttpEndpoint.data),
        combineStateNotEquals(_textToSpeechHttpEndpoint, ConfigurationSettings.textToSpeechHttpEndpoint.data)
    )

    //show endpoint settings
    fun isTextToSpeechHttpSettingsVisible(option: TextToSpeechOptions): Boolean {
        return option == TextToSpeechOptions.RemoteHTTP
    }

    //all options
    val textToSpeechOptions = TextToSpeechOptions::values

    //set new text to speech option
    fun selectTextToSpeechOption(option: TextToSpeechOptions) {
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

    /**
     * save data configuration
     */
    override fun onSave() {
        ConfigurationSettings.textToSpeechOption.value = _textToSpeechOption.value
        ConfigurationSettings.isUseCustomTextToSpeechHttpEndpoint.value = _isUseCustomTextToSpeechHttpEndpoint.value
        ConfigurationSettings.textToSpeechHttpEndpoint.value = _textToSpeechHttpEndpoint.value
    }

    /**
     * undo all changes
     */
    override fun discard() {
        _textToSpeechOption.value = ConfigurationSettings.textToSpeechOption.value
        _isUseCustomTextToSpeechHttpEndpoint.value = ConfigurationSettings.isUseCustomTextToSpeechHttpEndpoint.value
        _textToSpeechHttpEndpoint.value = ConfigurationSettings.textToSpeechHttpEndpoint.value
    }

    override fun initializeTestParams() {
        get<RhasspyActionsServiceParams> {
            parametersOf(
                RhasspyActionsServiceParams(
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

    override fun runTest() = testRunner.startTest()

}
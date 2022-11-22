package org.rhasspy.mobile.viewModels.configuration

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.*
import org.rhasspy.mobile.data.TextToSpeechOptions
import org.rhasspy.mobile.logger.Event
import org.rhasspy.mobile.services.httpclient.HttpClientPath
import org.rhasspy.mobile.settings.ConfigurationSettings

class TextToSpeechConfigurationViewModel : IConfigurationViewModel() {

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

    /**
     * test unsaved data configuration
     */
    override fun onTest(): StateFlow<List<Event>> {
        //TODO only when enabled
        //textfield to input test
        //button to send text
        //play audio (on music)
        //warning when output is silent
        TODO()
    }

}
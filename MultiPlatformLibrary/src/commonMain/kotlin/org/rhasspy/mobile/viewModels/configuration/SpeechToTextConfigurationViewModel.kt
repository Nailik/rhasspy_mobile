package org.rhasspy.mobile.viewModels.configuration

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.*
import org.rhasspy.mobile.data.SpeechToTextOptions
import org.rhasspy.mobile.settings.ConfigurationSettings

class SpeechToTextConfigurationViewModel : ViewModel() {

    //unsaved data
    private val _speechToTextOption = MutableStateFlow(ConfigurationSettings.speechToTextOption.value)
    private val _isUseCustomSpeechToTextHttpEndpoint = MutableStateFlow(ConfigurationSettings.isUseCustomSpeechToTextHttpEndpoint.value)
    private val _speechToTextHttpEndpoint = MutableStateFlow(ConfigurationSettings.speechToTextHttpEndpoint.value)

    //unsaved ui data
    val speechToTextOption = _speechToTextOption.readOnly
    val speechToTextHttpEndpoint = combineState(_isUseCustomSpeechToTextHttpEndpoint, _speechToTextHttpEndpoint) { useCustomSpeechToTextHttpEndpoint,
                                                                                                                   speechToTextHttpEndpoint ->
        if (useCustomSpeechToTextHttpEndpoint) {
            speechToTextHttpEndpoint
        } else {
            "${ConfigurationSettings.httpServerEndpoint.value}//api/speech-to-text"
        }
    }
    val isUseCustomSpeechToTextHttpEndpoint = _isUseCustomSpeechToTextHttpEndpoint.readOnly
    val isSpeechToTextHttpEndpointChangeEnabled = isUseCustomSpeechToTextHttpEndpoint

    val hasUnsavedChanges = combineAny(
        combineStateNotEquals(_speechToTextOption, ConfigurationSettings.speechToTextOption.data),
        combineStateNotEquals(_isUseCustomSpeechToTextHttpEndpoint, ConfigurationSettings.isUseCustomSpeechToTextHttpEndpoint.data),
        combineStateNotEquals(_speechToTextHttpEndpoint, ConfigurationSettings.speechToTextHttpEndpoint.data)
    )

    //show endpoint settings
    val isSpeechToTextHttpSettingsVisible = _speechToTextOption.mapReadonlyState { it == SpeechToTextOptions.RemoteHTTP }

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

    //set new speech to text http endpoint
    fun updateSpeechToTextHttpEndpoint(endpoint: String) {
        _speechToTextHttpEndpoint.value = endpoint
    }

    /**
     * save data configuration
     */
    fun save() {
        ConfigurationSettings.speechToTextOption.value = _speechToTextOption.value
        ConfigurationSettings.isUseCustomSpeechToTextHttpEndpoint.value = _isUseCustomSpeechToTextHttpEndpoint.value
        ConfigurationSettings.speechToTextHttpEndpoint.value = _speechToTextHttpEndpoint.value
    }

    /**
     * undo all changes
     */
    fun discard() {
        _speechToTextOption.value = ConfigurationSettings.speechToTextOption.value
        _isUseCustomSpeechToTextHttpEndpoint.value = ConfigurationSettings.isUseCustomSpeechToTextHttpEndpoint.value
        _speechToTextHttpEndpoint.value = ConfigurationSettings.speechToTextHttpEndpoint.value
    }

    /**
     * test unsaved data configuration
     */
    fun test() {

    }

}
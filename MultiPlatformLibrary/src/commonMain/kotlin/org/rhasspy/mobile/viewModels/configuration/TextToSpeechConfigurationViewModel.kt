package org.rhasspy.mobile.viewModels.configuration

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.combineAny
import org.rhasspy.mobile.combineStateNotEquals
import org.rhasspy.mobile.data.TextToSpeechOptions
import org.rhasspy.mobile.mapReadonlyState
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.settings.ConfigurationSettings

class TextToSpeechConfigurationViewModel : ViewModel() {

    //unsaved data
    private val _textToSpeechOption = MutableStateFlow(ConfigurationSettings.textToSpeechOption.value)
    private val _textToSpeechHttpEndpoint = MutableStateFlow(ConfigurationSettings.textToSpeechEndpoint.value)

    //unsaved ui data
    val textToSpeechOption = _textToSpeechOption.readOnly
    val textToSpeechHttpEndpoint = _textToSpeechHttpEndpoint.readOnly
    val textToSpeechHttpEndpointVisible = _textToSpeechOption.mapReadonlyState { it == TextToSpeechOptions.RemoteHTTP }

    val hasUnsavedChanges = combineAny(
        combineStateNotEquals(_textToSpeechOption, ConfigurationSettings.textToSpeechOption.data),
        combineStateNotEquals(_textToSpeechHttpEndpoint, ConfigurationSettings.textToSpeechEndpoint.data)
    )

    //all options
    val textToSpeechOptions = TextToSpeechOptions::values

    //set new text to speech option
    fun selectTextToSpeechOption(option: TextToSpeechOptions) {
        _textToSpeechOption.value = option
    }

    //set new text to speech http endpoint
    fun updateTextToSpeechHttpEndpoint(endpoint: String) {
        _textToSpeechHttpEndpoint.value = endpoint
    }

    /**
     * save data configuration
     */
    fun save() {
        ConfigurationSettings.textToSpeechOption.value = _textToSpeechOption.value
        ConfigurationSettings.textToSpeechEndpoint.value = _textToSpeechHttpEndpoint.value
    }

    fun discard() {
        _textToSpeechOption.value = ConfigurationSettings.textToSpeechOption.value
        _textToSpeechHttpEndpoint.value = ConfigurationSettings.textToSpeechEndpoint.value
    }

    /**
     * test unsaved data configuration
     */
    fun test() {

    }

}
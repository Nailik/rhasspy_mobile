package org.rhasspy.mobile.viewModels.configuration

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.data.SpeechToTextOptions
import org.rhasspy.mobile.mapReadonlyState
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.settings.ConfigurationSettings

class SpeechToTextConfigurationViewModel : ViewModel() {

    //unsaved data
    private val _speechToTextOption = MutableStateFlow(ConfigurationSettings.speechToTextOption.value)
    private val _speechToTextHttpEndpoint = MutableStateFlow(ConfigurationSettings.speechToTextHttpEndpoint.value)

    //unsaved ui data
    val speechToTextOption = _speechToTextOption.readOnly
    val speechToTextHttpEndpoint = _speechToTextHttpEndpoint.readOnly
    val speechToTextHttpEndpointVisible = _speechToTextOption.mapReadonlyState { it == SpeechToTextOptions.RemoteHTTP }

    //all options
    val speechToTextOptions = SpeechToTextOptions::values

    //set new speech to text option
    fun selectSpeechToTextOption(option: SpeechToTextOptions) {
        _speechToTextOption.value = option
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
        ConfigurationSettings.speechToTextHttpEndpoint.value = _speechToTextHttpEndpoint.value
    }

    /**
     * test unsaved data configuration
     */
    fun test() {

    }

}
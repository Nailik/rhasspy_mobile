package org.rhasspy.mobile.viewModels.configuration

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.*
import org.rhasspy.mobile.data.AudioOutputOptions
import org.rhasspy.mobile.data.AudioPlayingOptions
import org.rhasspy.mobile.logger.Event
import org.rhasspy.mobile.services.httpclient.HttpClientPath
import org.rhasspy.mobile.settings.ConfigurationSettings

/**
 * ViewModel for Audio Playing Configuration
 *
 * Current Option
 * Endpoint value
 * if Endpoint option should be shown
 * all Options as list
 */
class AudioPlayingConfigurationViewModel : IConfigurationViewModel() {

    //unsaved data
    private val _audioPlayingOption = MutableStateFlow(ConfigurationSettings.audioPlayingOption.value)
    private val _audioOutputOption = MutableStateFlow(ConfigurationSettings.audioOutputOption.value)
    private val _isUseCustomAudioPlayingHttpEndpoint = MutableStateFlow(ConfigurationSettings.isUseCustomAudioPlayingHttpEndpoint.value)
    private val _audioPlayingHttpEndpoint = MutableStateFlow(ConfigurationSettings.audioPlayingHttpEndpoint.value)

    //unsaved ui data
    val audioPlayingOption = _audioPlayingOption.readOnly
    val audioOutputOption = _audioOutputOption.readOnly
    val audioPlayingHttpEndpoint = combineState(_isUseCustomAudioPlayingHttpEndpoint, _audioPlayingHttpEndpoint) { useCustomAudioPlayingHttpEndpoint,
                                                                                                                   audioPlayingHttpEndpoint ->
        if (useCustomAudioPlayingHttpEndpoint) {
            audioPlayingHttpEndpoint
        } else {
            "${ConfigurationSettings.httpServerEndpoint.value}${HttpClientPath.PlayWav}"
        }
    }
    val isUseCustomAudioPlayingHttpEndpoint = _isUseCustomAudioPlayingHttpEndpoint.readOnly
    val isAudioPlayingHttpEndpointChangeEnabled = isUseCustomAudioPlayingHttpEndpoint

    override val isTestingEnabled = _audioPlayingOption.mapReadonlyState { it != AudioPlayingOptions.Disabled }

    //if there are unsaved changes
    override val hasUnsavedChanges = combineAny(
        combineStateNotEquals(_audioPlayingOption, ConfigurationSettings.audioPlayingOption.data),
        combineStateNotEquals(_audioOutputOption, ConfigurationSettings.audioOutputOption.data),
        combineStateNotEquals(_isUseCustomAudioPlayingHttpEndpoint, ConfigurationSettings.isUseCustomAudioPlayingHttpEndpoint.data),
        combineStateNotEquals(_audioPlayingHttpEndpoint, ConfigurationSettings.audioPlayingHttpEndpoint.data)
    )

    //all options
    val audioPlayingOptionsList = AudioPlayingOptions::values
    val audioOutputOptionsList = AudioOutputOptions::values

    //set new audio playing option
    fun selectAudioPlayingOption(option: AudioPlayingOptions) {
        _audioPlayingOption.value = option
    }

    //set new audio output option
    fun selectAudioOutputOption(option: AudioOutputOptions) {
        _audioOutputOption.value = option
    }

    //toggle if custom endpoint is used
    fun toggleUseCustomHttpEndpoint(enabled: Boolean) {
        _isUseCustomAudioPlayingHttpEndpoint.value = enabled
    }

    //edit endpoint
    fun changeAudioPlayingHttpEndpoint(endpoint: String) {
        _audioPlayingHttpEndpoint.value = endpoint
    }

    //show audio playing local settings
    fun isAudioPlayingLocalSettingsVisible(option: AudioPlayingOptions): Boolean {
        return option == AudioPlayingOptions.Local
    }

    //show audio playing http endpoint settings
    fun isAudioPlayingHttpEndpointSettingsVisible(option: AudioPlayingOptions): Boolean {
        return option == AudioPlayingOptions.RemoteHTTP
    }


    /**
     * save data configuration
     */
    override fun onSave() {
        ConfigurationSettings.audioPlayingOption.value = _audioPlayingOption.value
        ConfigurationSettings.audioOutputOption.value = _audioOutputOption.value
        ConfigurationSettings.isUseCustomAudioPlayingHttpEndpoint.value = _isUseCustomAudioPlayingHttpEndpoint.value
        ConfigurationSettings.audioPlayingHttpEndpoint.value = _audioPlayingHttpEndpoint.value
    }

    /**
     * undo all changes
     */
    override fun discard() {
        _audioPlayingOption.value = ConfigurationSettings.audioPlayingOption.value
        _audioOutputOption.value = ConfigurationSettings.audioOutputOption.value
        _isUseCustomAudioPlayingHttpEndpoint.value = ConfigurationSettings.isUseCustomAudioPlayingHttpEndpoint.value
        _audioPlayingHttpEndpoint.value = ConfigurationSettings.audioPlayingHttpEndpoint.value
    }

    /**
     * test unsaved data configuration
     */
    override fun onTest(): StateFlow<List<Event>> {
        //TODO only when enabled
        //record audio button
        //play audio button
        TODO()
    }

}
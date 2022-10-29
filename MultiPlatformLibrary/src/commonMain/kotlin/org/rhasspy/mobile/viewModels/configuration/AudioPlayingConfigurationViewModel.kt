package org.rhasspy.mobile.viewModels.configuration

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.*
import org.rhasspy.mobile.data.AudioPlayingOptions
import org.rhasspy.mobile.settings.ConfigurationSettings

/**
 * ViewModel for Audio Playing Configuration
 *
 * Current Option
 * Endpoint value
 * if Endpoint option should be shown
 * all Options as list
 */
class AudioPlayingConfigurationViewModel : ViewModel() {

    //unsaved data
    private val _audioPlayingOption = MutableStateFlow(ConfigurationSettings.audioPlayingOption.value)
    private val _isUseCustomAudioPlayingHttpEndpoint = MutableStateFlow(ConfigurationSettings.isUseCustomAudioPlayingHttpEndpoint.value)
    private val _audioPlayingHttpEndpoint = MutableStateFlow(ConfigurationSettings.audioPlayingHttpEndpoint.value)

    //unsaved ui data
    val audioPlayingOption = _audioPlayingOption.readOnly
    val audioPlayingHttpEndpoint = combineState(_isUseCustomAudioPlayingHttpEndpoint, _audioPlayingHttpEndpoint) { useCustomAudioPlayingHttpEndpoint,
                                                                                                                   audioPlayingHttpEndpoint ->
        if (useCustomAudioPlayingHttpEndpoint) {
            audioPlayingHttpEndpoint
        } else {
            "${ConfigurationSettings.httpServerEndpoint.value}//api/play-wav"
        }
    }
    val isUseCustomAudioPlayingHttpEndpoint = _isUseCustomAudioPlayingHttpEndpoint.readOnly
    val isAudioPlayingHttpEndpointChangeEnabled = isUseCustomAudioPlayingHttpEndpoint

    //if there are unsaved changes
    val hasUnsavedChanges = combineAny(
        combineStateNotEquals(_audioPlayingOption, ConfigurationSettings.audioPlayingOption.data),
        combineStateNotEquals(_isUseCustomAudioPlayingHttpEndpoint, ConfigurationSettings.isUseCustomAudioPlayingHttpEndpoint.data),
        combineStateNotEquals(_audioPlayingHttpEndpoint, ConfigurationSettings.audioPlayingHttpEndpoint.data)
    )

    //show input field for endpoint
    val isAudioPlayingHttpEndpointSettingsVisible = _audioPlayingOption.mapReadonlyState { it == AudioPlayingOptions.RemoteHTTP }

    //all options
    val audioPlayingOptionsList = AudioPlayingOptions::values

    //set new audio playing option
    fun selectAudioPlayingOption(option: AudioPlayingOptions) {
        _audioPlayingOption.value = option
    }

    //toggle if custom endpoint is used
    fun toggleUseCustomHttpEndpoint(enabled: Boolean) {
        _isUseCustomAudioPlayingHttpEndpoint.value = enabled
    }

    //edit endpoint
    fun changeAudioPlayingHttpEndpoint(endpoint: String) {
        _audioPlayingHttpEndpoint.value = endpoint
    }

    /**
     * save data configuration
     */
    fun save() {
        ConfigurationSettings.audioPlayingOption.value = _audioPlayingOption.value
        ConfigurationSettings.isUseCustomAudioPlayingHttpEndpoint.value = _isUseCustomAudioPlayingHttpEndpoint.value
        ConfigurationSettings.audioPlayingHttpEndpoint.value = _audioPlayingHttpEndpoint.value
    }

    fun discard() {
        _audioPlayingOption.value = ConfigurationSettings.audioPlayingOption.value
        _isUseCustomAudioPlayingHttpEndpoint.value = ConfigurationSettings.isUseCustomAudioPlayingHttpEndpoint.value
        _audioPlayingHttpEndpoint.value = ConfigurationSettings.audioPlayingHttpEndpoint.value
    }

    /**
     * test unsaved data configuration
     */
    fun test() {

    }


}
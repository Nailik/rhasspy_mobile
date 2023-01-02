package org.rhasspy.mobile.viewmodel.configuration

import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.*
import org.rhasspy.mobile.services.httpclient.HttpClientPath
import org.rhasspy.mobile.services.httpclient.HttpClientServiceParams
import org.rhasspy.mobile.services.localaudio.LocalAudioServiceParams
import org.rhasspy.mobile.services.rhasspyactions.RhasspyActionsServiceParams
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.settings.option.AudioOutputOption
import org.rhasspy.mobile.settings.option.AudioPlayingOption
import org.rhasspy.mobile.viewmodel.configuration.test.AudioPlayingConfigurationTest

/**
 * ViewModel for Audio Playing Configuration
 *
 * Current Option
 * Endpoint value
 * if Endpoint option should be shown
 * all Options as list
 */
class AudioPlayingConfigurationViewModel : IConfigurationViewModel() {

    override val testRunner by inject<AudioPlayingConfigurationTest>()

    //unsaved data
    private val _audioPlayingOption =
        MutableStateFlow(ConfigurationSetting.audioPlayingOption.value)
    private val _audioOutputOption = MutableStateFlow(ConfigurationSetting.audioOutputOption.value)
    private val _isUseCustomAudioPlayingHttpEndpoint =
        MutableStateFlow(ConfigurationSetting.isUseCustomAudioPlayingHttpEndpoint.value)
    private val _audioPlayingHttpEndpoint =
        MutableStateFlow(ConfigurationSetting.audioPlayingHttpEndpoint.value)

    //unsaved ui data
    val audioPlayingOption = _audioPlayingOption.readOnly
    val audioOutputOption = _audioOutputOption.readOnly
    val audioPlayingHttpEndpoint = combineState(
        _isUseCustomAudioPlayingHttpEndpoint,
        _audioPlayingHttpEndpoint
    ) { useCustomAudioPlayingHttpEndpoint,
        audioPlayingHttpEndpoint ->
        if (useCustomAudioPlayingHttpEndpoint) {
            audioPlayingHttpEndpoint
        } else {
            HttpClientPath.PlayWav.fromBaseConfiguration()
        }
    }
    val isUseCustomAudioPlayingHttpEndpoint = _isUseCustomAudioPlayingHttpEndpoint.readOnly
    val isAudioPlayingHttpEndpointChangeEnabled = isUseCustomAudioPlayingHttpEndpoint

    override val isTestingEnabled =
        _audioPlayingOption.mapReadonlyState { it != AudioPlayingOption.Disabled }

    //if there are unsaved changes
    override val hasUnsavedChanges = combineAny(
        combineStateNotEquals(_audioPlayingOption, ConfigurationSetting.audioPlayingOption.data),
        combineStateNotEquals(_audioOutputOption, ConfigurationSetting.audioOutputOption.data),
        combineStateNotEquals(
            _isUseCustomAudioPlayingHttpEndpoint,
            ConfigurationSetting.isUseCustomAudioPlayingHttpEndpoint.data
        ),
        combineStateNotEquals(
            _audioPlayingHttpEndpoint,
            ConfigurationSetting.audioPlayingHttpEndpoint.data
        )
    )

    //all options
    val audioPlayingOptionList = AudioPlayingOption::values
    val audioOutputOptionList = AudioOutputOption::values

    //set new audio playing option
    fun selectAudioPlayingOption(option: AudioPlayingOption) {
        _audioPlayingOption.value = option
    }

    //set new audio output option
    fun selectAudioOutputOption(option: AudioOutputOption) {
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
    fun isAudioPlayingLocalSettingsVisible(option: AudioPlayingOption): Boolean {
        return option == AudioPlayingOption.Local
    }

    //show audio playing http endpoint settings
    fun isAudioPlayingHttpEndpointSettingsVisible(option: AudioPlayingOption): Boolean {
        return option == AudioPlayingOption.RemoteHTTP
    }


    /**
     * save data configuration
     */
    override fun onSave() {
        ConfigurationSetting.audioPlayingOption.value = _audioPlayingOption.value
        ConfigurationSetting.audioOutputOption.value = _audioOutputOption.value
        ConfigurationSetting.isUseCustomAudioPlayingHttpEndpoint.value =
            _isUseCustomAudioPlayingHttpEndpoint.value
        ConfigurationSetting.audioPlayingHttpEndpoint.value = _audioPlayingHttpEndpoint.value
    }

    /**
     * undo all changes
     */
    override fun discard() {
        _audioPlayingOption.value = ConfigurationSetting.audioPlayingOption.value
        _audioOutputOption.value = ConfigurationSetting.audioOutputOption.value
        _isUseCustomAudioPlayingHttpEndpoint.value =
            ConfigurationSetting.isUseCustomAudioPlayingHttpEndpoint.value
        _audioPlayingHttpEndpoint.value = ConfigurationSetting.audioPlayingHttpEndpoint.value
    }

    override fun initializeTestParams() {
        get<RhasspyActionsServiceParams> {
            parametersOf(
                RhasspyActionsServiceParams(
                    audioPlayingOption = _audioPlayingOption.value
                )
            )
        }

        get<HttpClientServiceParams> {
            parametersOf(
                HttpClientServiceParams(
                    isUseCustomAudioPlayingEndpoint = _isUseCustomAudioPlayingHttpEndpoint.value,
                    audioPlayingHttpEndpoint = _audioPlayingHttpEndpoint.value
                )
            )
        }

        get<LocalAudioServiceParams> {
            parametersOf(
                LocalAudioServiceParams(
                    audioOutputOption = _audioOutputOption.value
                )
            )
        }
    }

    override fun runTest() = testRunner.startTest()

}
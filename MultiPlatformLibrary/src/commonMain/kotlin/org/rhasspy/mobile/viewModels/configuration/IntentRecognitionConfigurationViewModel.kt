package org.rhasspy.mobile.viewModels.configuration

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.*
import org.rhasspy.mobile.data.IntentRecognitionOptions
import org.rhasspy.mobile.data.SpeechToTextOptions
import org.rhasspy.mobile.services.httpclient.data.HttpClientPath
import org.rhasspy.mobile.services.state.ServiceState
import org.rhasspy.mobile.settings.ConfigurationSettings

class IntentRecognitionConfigurationViewModel : ViewModel(), IConfigurationViewModel {

    //unsaved data
    private val _intentRecognitionOption = MutableStateFlow(ConfigurationSettings.intentRecognitionOption.value)
    private val _isUseCustomIntentRecognitionHttpEndpoint = MutableStateFlow(ConfigurationSettings.isUseCustomIntentRecognitionHttpEndpoint.value)
    private val _intentRecognitionHttpEndpoint = MutableStateFlow(ConfigurationSettings.intentRecognitionHttpEndpoint.value)

    //unsaved ui data
    val intentRecognitionOption = _intentRecognitionOption.readOnly
    val intentRecognitionHttpEndpoint =
        combineState(_isUseCustomIntentRecognitionHttpEndpoint, _intentRecognitionHttpEndpoint) { useCustomIntentRecognitionHttpEndpoint,
                                                                                                  intentRecognitionHttpEndpoint ->
            if (useCustomIntentRecognitionHttpEndpoint) {
                intentRecognitionHttpEndpoint
            } else {
                "${ConfigurationSettings.httpServerEndpoint.value}${HttpClientPath.TextToIntent}"
            }
        }
    val isUseCustomIntentRecognitionHttpEndpoint = _isUseCustomIntentRecognitionHttpEndpoint.readOnly
    val isIntentRecognitionHttpEndpointChangeEnabled = isUseCustomIntentRecognitionHttpEndpoint

    override val isTestingEnabled = _intentRecognitionOption.mapReadonlyState { it != IntentRecognitionOptions.Disabled }
    override val testState: StateFlow<List<ServiceState>> = MutableStateFlow(listOf())

    override val hasUnsavedChanges = combineAny(
        combineStateNotEquals(_intentRecognitionOption, ConfigurationSettings.intentRecognitionOption.data),
        combineStateNotEquals(_isUseCustomIntentRecognitionHttpEndpoint, ConfigurationSettings.isUseCustomIntentRecognitionHttpEndpoint.data),
        combineStateNotEquals(_intentRecognitionHttpEndpoint, ConfigurationSettings.intentRecognitionHttpEndpoint.data)
    )

    //show endpoint settings
    fun isIntentRecognitionHttpSettingsVisible(option: IntentRecognitionOptions): Boolean {
        return option == IntentRecognitionOptions.RemoteHTTP
    }

    //all options
    val intentRecognitionOptionsList = IntentRecognitionOptions::values

    //set new intent recognition option
    fun selectIntentRecognitionOption(option: IntentRecognitionOptions) {
        _intentRecognitionOption.value = option
    }

    //toggle if custom endpoint is used
    fun toggleUseCustomHttpEndpoint(enabled: Boolean) {
        _isUseCustomIntentRecognitionHttpEndpoint.value = enabled
    }

    //set new intent recognition option
    fun changeIntentRecognitionHttpEndpoint(endpoint: String) {
        _intentRecognitionHttpEndpoint.value = endpoint
    }

    /**
     * save data configuration
     */
    override fun save() {
        ConfigurationSettings.intentRecognitionOption.value = _intentRecognitionOption.value
        ConfigurationSettings.isUseCustomIntentRecognitionHttpEndpoint.value = _isUseCustomIntentRecognitionHttpEndpoint.value
        ConfigurationSettings.intentRecognitionHttpEndpoint.value = _intentRecognitionHttpEndpoint.value
    }

    /**
     * undo all changes
     */
    override fun discard() {
        _intentRecognitionOption.value = ConfigurationSettings.intentRecognitionOption.value
        _isUseCustomIntentRecognitionHttpEndpoint.value = ConfigurationSettings.isUseCustomIntentRecognitionHttpEndpoint.value
        _intentRecognitionHttpEndpoint.value = ConfigurationSettings.intentRecognitionHttpEndpoint.value
    }

    /**
     * test unsaved data configuration
     */
    override fun test() {
        //TODO only when enabled
        //textfield to input test
        //button to send intent
        //information if was recognized
    }

    override fun stopTest() {

    }

}
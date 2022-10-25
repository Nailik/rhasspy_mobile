package org.rhasspy.mobile.viewModels.configuration

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.rhasspy.mobile.combineAny
import org.rhasspy.mobile.combineStateNotEquals
import org.rhasspy.mobile.data.IntentRecognitionOptions
import org.rhasspy.mobile.mapReadonlyState
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.settings.ConfigurationSettings

class IntentRecognitionConfigurationViewModel : ViewModel() {

    //unsaved data
    private val _intentRecognitionOption = MutableStateFlow(ConfigurationSettings.intentRecognitionOption.value)
    private val _intentRecognitionEndpoint = MutableStateFlow(ConfigurationSettings.intentRecognitionEndpoint.value)

    //unsaved ui data
    val intentRecognitionOption = _intentRecognitionOption.readOnly
    val intentRecognitionEndpoint = _intentRecognitionEndpoint.readOnly

    val hasUnsavedChanges = combineAny(
        combineStateNotEquals(_intentRecognitionOption, ConfigurationSettings.intentRecognitionOption.data),
        combineStateNotEquals(_intentRecognitionEndpoint, ConfigurationSettings.intentRecognitionEndpoint.data)
    )

    //show input field for endpoint
    val isRemoteHttpEndpointVisible = _intentRecognitionOption.mapReadonlyState { it == IntentRecognitionOptions.RemoteHTTP }

    //all options
    val intentRecognitionOptionsList = IntentRecognitionOptions::values

    //set new intent recognition option
    fun selectIntentRecognitionOption(option: IntentRecognitionOptions) {
        _intentRecognitionOption.value = option
    }

    //set new intent recognition option
    fun changeIntentRecognitionHttpEndpoint(endpoint: String) {
        _intentRecognitionEndpoint.value = endpoint
    }

    /**
     * save data configuration
     */
    fun save() {
        ConfigurationSettings.intentRecognitionOption.value = _intentRecognitionOption.value
        ConfigurationSettings.intentRecognitionEndpoint.value = _intentRecognitionEndpoint.value
    }

    fun discard() {
        _intentRecognitionOption.value = ConfigurationSettings.intentRecognitionOption.value
        _intentRecognitionEndpoint.value = ConfigurationSettings.intentRecognitionEndpoint.value
    }

    /**
     * test unsaved data configuration
     */
    fun test() {

    }

}
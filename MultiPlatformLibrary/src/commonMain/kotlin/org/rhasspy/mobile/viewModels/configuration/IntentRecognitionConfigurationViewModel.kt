package org.rhasspy.mobile.viewModels.configuration

import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.*
import org.rhasspy.mobile.data.IntentRecognitionOptions
import org.rhasspy.mobile.services.httpclient.HttpClientPath
import org.rhasspy.mobile.services.httpclient.HttpClientServiceParams
import org.rhasspy.mobile.services.rhasspyactions.RhasspyActionsServiceParams
import org.rhasspy.mobile.settings.ConfigurationSettings
import org.rhasspy.mobile.viewModels.configuration.test.IntentRecognitionConfigurationTest

class IntentRecognitionConfigurationViewModel : IConfigurationViewModel() {

    //test data
    override val testRunner by inject<IntentRecognitionConfigurationTest>()
    private val _testIntentRecognitionText = MutableStateFlow("")
    val testIntentRecognitionText = _testIntentRecognitionText.readOnly

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

    fun updateTestIntentRecognitionText(text: String) {
        _testIntentRecognitionText.value = text
    }

    /**
     * save data configuration
     */
    override fun onSave() {
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

    override fun initializeTestParams() {
        get<RhasspyActionsServiceParams> {
            parametersOf(
                RhasspyActionsServiceParams(
                    intentRecognitionOption = _intentRecognitionOption.value
                )
            )
        }

        get<HttpClientServiceParams> {
            parametersOf(
                HttpClientServiceParams(
                    isUseCustomTextToSpeechHttpEndpoint = _isUseCustomIntentRecognitionHttpEndpoint.value,
                    intentRecognitionHttpEndpoint = _intentRecognitionHttpEndpoint.value,
                )
            )
        }
    }

    override fun runTest() = testRunner.runTest(_testIntentRecognitionText.value)

}
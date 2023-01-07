package org.rhasspy.mobile.viewmodel.configuration

import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.combineAny
import org.rhasspy.mobile.combineStateNotEquals
import org.rhasspy.mobile.logger.LogType
import org.rhasspy.mobile.mapReadonlyState
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.homeassistant.HomeAssistantServiceParams
import org.rhasspy.mobile.services.httpclient.HttpClientServiceParams
import org.rhasspy.mobile.services.intenthandling.IntentHandlingService
import org.rhasspy.mobile.services.intenthandling.IntentHandlingServiceParams
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.settings.option.HomeAssistantIntentHandlingOption
import org.rhasspy.mobile.settings.option.IntentHandlingOption
import org.rhasspy.mobile.viewmodel.configuration.test.IntentHandlingConfigurationTest

class IntentHandlingConfigurationViewModel : IConfigurationViewModel() {

    override val testRunner by inject<IntentHandlingConfigurationTest>()
    override val logType = LogType.IntentHandlingService
    override val serviceState get() = get<IntentHandlingService>().serviceState

    private val _testIntentNameText = MutableStateFlow("")
    val testIntentNameText = _testIntentNameText.readOnly
    private val _testIntentText = MutableStateFlow("")
    val testIntentText = _testIntentText.readOnly

    //unsaved data
    private val _intentHandlingOption = MutableStateFlow(ConfigurationSetting.intentHandlingOption.value)
    private val _intentHandlingHttpEndpoint = MutableStateFlow(ConfigurationSetting.intentHandlingHttpEndpoint.value)
    private val _intentHandlingHassEndpoint = MutableStateFlow(ConfigurationSetting.intentHandlingHassEndpoint.value)
    private val _intentHandlingHassAccessToken = MutableStateFlow(ConfigurationSetting.intentHandlingHassAccessToken.value)
    private val _intentHandlingHomeAssistantOption = MutableStateFlow(ConfigurationSetting.intentHandlingHomeAssistantOption.value)

    //unsaved ui data
    val intentHandlingOption = _intentHandlingOption.readOnly
    val intentHandlingHttpEndpoint = _intentHandlingHttpEndpoint.readOnly
    val intentHandlingHassEndpoint = _intentHandlingHassEndpoint.readOnly
    val intentHandlingHassAccessToken = _intentHandlingHassAccessToken.readOnly
    val isIntentHandlingHassEvent = _intentHandlingHomeAssistantOption.mapReadonlyState { it == HomeAssistantIntentHandlingOption.Event }
    val isIntentHandlingHassIntent = _intentHandlingHomeAssistantOption.mapReadonlyState { it == HomeAssistantIntentHandlingOption.Intent }

    override val isTestingEnabled = _intentHandlingOption.mapReadonlyState { it != IntentHandlingOption.Disabled && it != IntentHandlingOption.WithRecognition }

    override val hasUnsavedChanges = combineAny(
        combineStateNotEquals(_intentHandlingOption, ConfigurationSetting.intentHandlingOption.data),
        combineStateNotEquals(_intentHandlingHttpEndpoint, ConfigurationSetting.intentHandlingHttpEndpoint.data),
        combineStateNotEquals(_intentHandlingHassEndpoint, ConfigurationSetting.intentHandlingHassEndpoint.data),
        combineStateNotEquals(_intentHandlingHassAccessToken, ConfigurationSetting.intentHandlingHassAccessToken.data),
        combineStateNotEquals(_intentHandlingHomeAssistantOption, ConfigurationSetting.intentHandlingHomeAssistantOption.data)
    )

    //show input field for endpoint
    fun isRemoteHttpSettingsVisible(option: IntentHandlingOption): Boolean {
        return option == IntentHandlingOption.RemoteHTTP
    }

    //show fields for home assistant settings
    fun isHomeAssistantSettingsVisible(option: IntentHandlingOption): Boolean {
        return option == IntentHandlingOption.HomeAssistant
    }

    //all options
    val intentHandlingOptionList = IntentHandlingOption::values

    //set new intent handling option
    fun selectIntentHandlingOption(option: IntentHandlingOption) {
        _intentHandlingOption.value = option
    }

    //edit endpoint
    fun changeIntentHandlingHttpEndpoint(endpoint: String) {
        _intentHandlingHttpEndpoint.value = endpoint
    }

    //edit endpoint
    fun changeIntentHandlingHassEndpoint(endpoint: String) {
        _intentHandlingHassEndpoint.value = endpoint
    }

    //edit endpoint
    fun changeIntentHandlingHassAccessToken(token: String) {
        _intentHandlingHassAccessToken.value = token
    }

    //choose hass intent handling as event
    fun selectIntentHandlingHassEvent() {
        _intentHandlingHomeAssistantOption.value = HomeAssistantIntentHandlingOption.Event
    }

    //choose hass intent handling as intent
    fun selectIntentHandlingHassIntent() {
        _intentHandlingHomeAssistantOption.value = HomeAssistantIntentHandlingOption.Intent
    }

    fun updateTestIntentNameText(text: String) {
        _testIntentNameText.value = text
    }

    fun updateTestIntentText(text: String) {
        _testIntentText.value = text
    }

    /**
     * save data configuration
     */
    override fun onSave() {
        ConfigurationSetting.intentHandlingOption.value = _intentHandlingOption.value
        ConfigurationSetting.intentHandlingHttpEndpoint.value = _intentHandlingHttpEndpoint.value
        ConfigurationSetting.intentHandlingHassEndpoint.value = _intentHandlingHassEndpoint.value
        ConfigurationSetting.intentHandlingHassAccessToken.value = _intentHandlingHassAccessToken.value
        ConfigurationSetting.intentHandlingHomeAssistantOption.value = _intentHandlingHomeAssistantOption.value
    }

    /**
     * undo all changes
     */
    override fun discard() {
        _intentHandlingOption.value = ConfigurationSetting.intentHandlingOption.value
        _intentHandlingHttpEndpoint.value = ConfigurationSetting.intentHandlingHttpEndpoint.value
        _intentHandlingHassEndpoint.value = ConfigurationSetting.intentHandlingHassEndpoint.value
        _intentHandlingHassAccessToken.value = ConfigurationSetting.intentHandlingHassAccessToken.value
        _intentHandlingHomeAssistantOption.value = ConfigurationSetting.intentHandlingHomeAssistantOption.value
    }

    override fun initializeTestParams() {
        get<IntentHandlingServiceParams> {
            parametersOf(
                IntentHandlingServiceParams(
                    intentHandlingOption = _intentHandlingOption.value
                )
            )
        }

        get<HttpClientServiceParams> {
            parametersOf(
                HttpClientServiceParams(
                    intentHandlingHttpEndpoint = _intentHandlingHttpEndpoint.value,
                    intentHandlingHassEndpoint = _intentHandlingHassEndpoint.value,
                    intentHandlingHassAccessToken = _intentHandlingHassAccessToken.value,
                    intentHandlingOption = _intentHandlingOption.value
                )
            )
        }

        get<HomeAssistantServiceParams> {
            parametersOf(
                HomeAssistantServiceParams(
                    intentHandlingHomeAssistantOption = _intentHandlingHomeAssistantOption.value
                )
            )
        }
    }

    fun testIntentHandling() = testRunner.handleIntent(_testIntentNameText.value, _testIntentText.value)

}
package org.rhasspy.mobile.viewmodel.configuration.intenthandling

import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.data.service.option.HomeAssistantIntentHandlingOption
import org.rhasspy.mobile.data.service.option.IntentHandlingOption
import org.rhasspy.mobile.logic.logger.LogType
import org.rhasspy.mobile.logic.services.homeassistant.HomeAssistantServiceParams
import org.rhasspy.mobile.logic.services.httpclient.HttpClientServiceParams
import org.rhasspy.mobile.logic.services.intenthandling.IntentHandlingService
import org.rhasspy.mobile.logic.services.intenthandling.IntentHandlingServiceParams
import org.rhasspy.mobile.platformspecific.readOnly
import org.rhasspy.mobile.viewmodel.configuration.IConfigurationViewModel

class IntentHandlingConfigurationViewModel : IConfigurationViewModel() {

    override val testRunner by inject<IntentHandlingConfigurationTest>()
    override val logType = LogType.IntentHandlingService
    override val serviceState get() = get<IntentHandlingService>().serviceState

    private val _testIntentNameText = MutableStateFlow("")
    val testIntentNameText = _testIntentNameText.readOnly
    private val _testIntentText = MutableStateFlow("")
    val testIntentText = _testIntentText.readOnly


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

    fun testIntentHandling() =
        testRunner.handleIntent(_testIntentNameText.value, _testIntentText.value)

}
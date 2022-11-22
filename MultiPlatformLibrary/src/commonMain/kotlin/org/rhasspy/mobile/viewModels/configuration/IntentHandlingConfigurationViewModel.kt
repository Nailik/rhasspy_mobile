package org.rhasspy.mobile.viewModels.configuration

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.combineAny
import org.rhasspy.mobile.combineStateNotEquals
import org.rhasspy.mobile.data.HomeAssistantIntentHandlingOptions
import org.rhasspy.mobile.data.IntentHandlingOptions
import org.rhasspy.mobile.logger.Event
import org.rhasspy.mobile.mapReadonlyState
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.settings.ConfigurationSettings

class IntentHandlingConfigurationViewModel : IConfigurationViewModel() {

    //unsaved data
    private val _intentHandlingOption = MutableStateFlow(ConfigurationSettings.intentHandlingOption.value)
    private val _intentHandlingHttpEndpoint = MutableStateFlow(ConfigurationSettings.intentHandlingHttpEndpoint.value)
    private val _intentHandlingHassEndpoint = MutableStateFlow(ConfigurationSettings.intentHandlingHassEndpoint.value)
    private val _intentHandlingHassAccessToken = MutableStateFlow(ConfigurationSettings.intentHandlingHassAccessToken.value)
    private val _intentHandlingHomeAssistantOption = MutableStateFlow(ConfigurationSettings.intentHandlingHomeAssistantOption.value)

    //unsaved ui data
    val intentHandlingOption = _intentHandlingOption.readOnly
    val intentHandlingHttpEndpoint = _intentHandlingHttpEndpoint.readOnly
    val intentHandlingHassEndpoint = _intentHandlingHassEndpoint.readOnly
    val intentHandlingHassAccessToken = _intentHandlingHassAccessToken.readOnly
    val isIntentHandlingHassEvent = _intentHandlingHomeAssistantOption.mapReadonlyState { it == HomeAssistantIntentHandlingOptions.Event }
    val isIntentHandlingHassIntent = _intentHandlingHomeAssistantOption.mapReadonlyState { it == HomeAssistantIntentHandlingOptions.Intent }

    override val isTestingEnabled = _intentHandlingOption.mapReadonlyState { it != IntentHandlingOptions.Disabled }

    override val hasUnsavedChanges = combineAny(
        combineStateNotEquals(_intentHandlingOption, ConfigurationSettings.intentHandlingOption.data),
        combineStateNotEquals(_intentHandlingHttpEndpoint, ConfigurationSettings.intentHandlingHttpEndpoint.data),
        combineStateNotEquals(_intentHandlingHassEndpoint, ConfigurationSettings.intentHandlingHassEndpoint.data),
        combineStateNotEquals(_intentHandlingHassAccessToken, ConfigurationSettings.intentHandlingHassAccessToken.data),
        combineStateNotEquals(_intentHandlingHomeAssistantOption, ConfigurationSettings.intentHandlingHomeAssistantOption.data)
    )

    //show input field for endpoint
    fun isRemoteHttpSettingsVisible(option: IntentHandlingOptions): Boolean {
        return option == IntentHandlingOptions.RemoteHTTP
    }

    //show fields for home assistant settings
    fun isHomeAssistantSettingsVisible(option: IntentHandlingOptions): Boolean {
        return option == IntentHandlingOptions.HomeAssistant
    }

    //all options
    val intentHandlingOptionsList = IntentHandlingOptions::values

    //set new intent handling option
    fun selectIntentHandlingOption(option: IntentHandlingOptions) {
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
        _intentHandlingHomeAssistantOption.value = HomeAssistantIntentHandlingOptions.Event
    }

    //choose hass intent handling as intent
    fun selectIntentHandlingHassIntent() {
        _intentHandlingHomeAssistantOption.value = HomeAssistantIntentHandlingOptions.Intent
    }

    /**
     * save data configuration
     */
    override fun onSave() {
        ConfigurationSettings.intentHandlingOption.value = _intentHandlingOption.value
        ConfigurationSettings.intentHandlingHttpEndpoint.value = _intentHandlingHttpEndpoint.value
        ConfigurationSettings.intentHandlingHassEndpoint.value = _intentHandlingHassEndpoint.value
        ConfigurationSettings.intentHandlingHassAccessToken.value = _intentHandlingHassAccessToken.value
        ConfigurationSettings.intentHandlingHomeAssistantOption.value = _intentHandlingHomeAssistantOption.value
    }

    /**
     * undo all changes
     */
    override fun discard() {
        _intentHandlingOption.value = ConfigurationSettings.intentHandlingOption.value
        _intentHandlingHttpEndpoint.value = ConfigurationSettings.intentHandlingHttpEndpoint.value
        _intentHandlingHassEndpoint.value = ConfigurationSettings.intentHandlingHassEndpoint.value
        _intentHandlingHassAccessToken.value = ConfigurationSettings.intentHandlingHassAccessToken.value
        _intentHandlingHomeAssistantOption.value = ConfigurationSettings.intentHandlingHomeAssistantOption.value
    }

    /**
     * test unsaved data configuration
     */
    override fun onTest(): StateFlow<List<Event>> {
        //TODO only when enabled
        //textfield for intent name and button
        //alternative? - textfield for text string to recognize intent and then handle
        TODO()
    }

}
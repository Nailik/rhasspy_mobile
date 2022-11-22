package org.rhasspy.mobile.viewModels.configuration

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.rhasspy.mobile.combineAny
import org.rhasspy.mobile.combineStateNotEquals
import org.rhasspy.mobile.logger.Event
import org.rhasspy.mobile.logger.EventLogger
import org.rhasspy.mobile.logger.EventTag
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.httpclient.HttpClientPath
import org.rhasspy.mobile.services.httpclient.HttpClientService
import org.rhasspy.mobile.services.httpclient.HttpClientServiceParams
import org.rhasspy.mobile.settings.ConfigurationSettings

class RemoteHermesHttpConfigurationViewModel : IConfigurationViewModel() {

    //unsaved data
    private val _httpServerEndpoint = MutableStateFlow(ConfigurationSettings.httpServerEndpoint.value)
    private val _isHttpSSLVerificationDisabled = MutableStateFlow(ConfigurationSettings.isHttpSSLVerificationDisabled.value)

    //unsaved ui data
    val httpServerEndpoint = _httpServerEndpoint.readOnly
    val isHttpSSLVerificationDisabled = _isHttpSSLVerificationDisabled.readOnly

    override val isTestingEnabled = MutableStateFlow(true)

    override val hasUnsavedChanges = combineAny(
        combineStateNotEquals(_httpServerEndpoint, ConfigurationSettings.httpServerEndpoint.data),
        combineStateNotEquals(_isHttpSSLVerificationDisabled, ConfigurationSettings.isHttpSSLVerificationDisabled.data)
    )

    //set new http server endpoint
    fun updateHttpServerEndpoint(endpoint: String) {
        _httpServerEndpoint.value = endpoint
    }

    //set new intent recognition option
    fun toggleHttpSSLVerificationDisabled(disabled: Boolean) {
        _isHttpSSLVerificationDisabled.value = disabled
    }


    /**
     * save data configuration
     */
    override fun onSave() {
        ConfigurationSettings.httpServerEndpoint.value = _httpServerEndpoint.value
        ConfigurationSettings.isHttpSSLVerificationDisabled.value = _isHttpSSLVerificationDisabled.value
    }

    /**
     * undo all changes
     */
    override fun discard() {
        _httpServerEndpoint.value = ConfigurationSettings.httpServerEndpoint.value
        _isHttpSSLVerificationDisabled.value = ConfigurationSettings.isHttpSSLVerificationDisabled.value
    }


    /**
     * test unsaved data configuration
     */
    override fun onTest(): StateFlow<List<Event>> {
        //initialize test params
        get<HttpClientServiceParams> {
            parametersOf(
                HttpClientServiceParams(
                    isHttpSSLVerificationDisabled = _isHttpSSLVerificationDisabled.value,
                    httpServerEndpoint = _httpServerEndpoint.value,
                    isUseCustomSpeechToTextHttpEndpoint = false,
                    isUseCustomIntentRecognitionHttpEndpoint = false,
                    isUseCustomTextToSpeechHttpEndpoint = false
                )
            )
        }
        //get logger
        val eventLogger by inject<EventLogger>(named(EventTag.HttpClientService.name))
        return eventLogger.events
    }

    override suspend fun runTest() {
        val client = get<HttpClientService>()
        client.speechToText(emptyList())
        client.recognizeIntent("text")
        client.textToSpeech("text")
        super.runTest()
    }

}
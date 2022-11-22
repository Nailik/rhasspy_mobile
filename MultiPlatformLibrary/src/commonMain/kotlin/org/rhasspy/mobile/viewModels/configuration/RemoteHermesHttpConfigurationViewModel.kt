package org.rhasspy.mobile.viewModels.configuration

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.combineAny
import org.rhasspy.mobile.combineStateNotEquals
import org.rhasspy.mobile.logger.Event
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.settings.ConfigurationSettings

//TODO add all other endpoints to this list
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
        /*
        httpClientServiceTest = get {
            parametersOf(
                HttpClientLink(
                    isHttpSSLVerificationDisabled = ConfigurationSettings.isHttpServerSSLEnabled.value,
                    speechToTextHttpEndpoint = if (ConfigurationSettings.isUseCustomSpeechToTextHttpEndpoint.value) {
                        _httpServerEndpoint.value
                    } else {
                        "${ConfigurationSettings.httpServerEndpoint.value}${HttpClientPath.SpeechToText}"
                    },
                    intentRecognitionHttpEndpoint = if (ConfigurationSettings.isUseCustomIntentRecognitionHttpEndpoint.value) {
                        _httpServerEndpoint.value
                    } else {
                        "${ConfigurationSettings.httpServerEndpoint.value}${HttpClientPath.TextToIntent}"
                    },
                    isHandleIntentDirectly = ConfigurationSettings.intentHandlingOption.value == IntentHandlingOptions.WithRecognition,
                    textToSpeechHttpEndpoint = if (ConfigurationSettings.isUseCustomSpeechToTextHttpEndpoint.value) {
                        _httpServerEndpoint.value
                    } else {
                        "${ConfigurationSettings.httpServerEndpoint.value}${HttpClientPath.TextToSpeech}"
                    },
                    audioPlayingHttpEndpoint = ConfigurationSettings.audioPlayingHttpEndpoint.value,
                    intentHandlingHttpEndpoint = ConfigurationSettings.intentHandlingHttpEndpoint.value,
                    intentHandlingHassEndpoint = ConfigurationSettings.intentHandlingHassEndpoint.value,
                    intentHandlingHassAccessToken = ConfigurationSettings.intentHandlingHassAccessToken.value,
                )
            )
        }

        //run tests
        CoroutineScope(Dispatchers.Default).launch {
            httpClientServiceTest.currentState.collect { currentState ->
                val list = _testState.value.toMutableList()
                list.firstOrNull { it.stateType == currentState.stateType }?.also {
                    list.add(list.indexOf(it), it.copy(state = currentState.state, description = currentState.description))
                    list.remove(it)
                } ?: run {
                    list.add(currentState)
                }
                _testState.value = list
            }
        }

        httpClientServiceTest.start()

         */
        TODO()
    }

}
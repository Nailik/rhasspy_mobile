package org.rhasspy.mobile.viewModels.configuration

import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.combineAny
import org.rhasspy.mobile.combineStateNotEquals
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.httpclient.HttpClientServiceParams
import org.rhasspy.mobile.settings.ConfigurationSettings
import org.rhasspy.mobile.viewModels.configuration.test.RemoteHermesHttpConfigurationTest

class RemoteHermesHttpConfigurationViewModel : IConfigurationViewModel() {

    override val testRunner by inject<RemoteHermesHttpConfigurationTest>()
    override val events = testRunner.events

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

    override fun initializeTestParams() {
        get<HttpClientServiceParams> {
            parametersOf(
                HttpClientServiceParams(
                    isHttpSSLVerificationDisabled = _isHttpSSLVerificationDisabled.value,
                    httpServerEndpoint = _httpServerEndpoint.value
                )
            )
        }
    }

    override fun runTest() = testRunner.startTest()

}
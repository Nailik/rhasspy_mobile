package org.rhasspy.mobile.viewmodel.configuration

import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.rhasspy.mobile.combineAny
import org.rhasspy.mobile.combineStateNotEquals
import org.rhasspy.mobile.mapReadonlyState
import org.rhasspy.mobile.readOnly
import org.rhasspy.mobile.services.httpclient.HttpClientServiceParams
import org.rhasspy.mobile.settings.ConfigurationSettings
import org.rhasspy.mobile.viewmodel.configuration.test.RemoteHermesHttpConfigurationTest

class RemoteHermesHttpConfigurationViewModel : IConfigurationViewModel() {

    override val testRunner by inject<RemoteHermesHttpConfigurationTest>()

    //unsaved data
    private val _httpServerEndpointHost =
        MutableStateFlow(ConfigurationSettings.httpClientServerEndpointHost.value)
    private val _httpServerEndpointPort =
        MutableStateFlow(ConfigurationSettings.httpClientServerEndpointPort.value)
    private val _httpServerEndpointPortText =
        MutableStateFlow(ConfigurationSettings.httpClientServerEndpointPort.value.toString())
    private val _isHttpSSLVerificationDisabled =
        MutableStateFlow(ConfigurationSettings.isHttpClientSSLVerificationDisabled.value)

    //unsaved ui data
    val httpServerEndpointHost = _httpServerEndpointHost.readOnly
    val httpServerEndpointPort = _httpServerEndpointPortText.readOnly
    val isHttpSSLVerificationDisabled = _isHttpSSLVerificationDisabled.readOnly

    override val isTestingEnabled = _httpServerEndpointHost.mapReadonlyState { it.isNotBlank() }

    override val hasUnsavedChanges = combineAny(
        combineStateNotEquals(
            _httpServerEndpointHost,
            ConfigurationSettings.httpClientServerEndpointHost.data
        ),
        combineStateNotEquals(
            _httpServerEndpointPort,
            ConfigurationSettings.httpClientServerEndpointPort.data
        ),
        combineStateNotEquals(
            _isHttpSSLVerificationDisabled,
            ConfigurationSettings.isHttpClientSSLVerificationDisabled.data
        )
    )

    //set new http server endpoint host
    fun updateHttpServerEndpointHost(endpoint: String) {
        _httpServerEndpointHost.value = endpoint
    }

    //set new http server endpoint port
    fun updateHttpServerEndpointPort(port: String) {
        val text = port.replace("""[-,. ]""".toRegex(), "")
        _httpServerEndpointPortText.value = text
        _httpServerEndpointPort.value = text.toIntOrNull() ?: 0
    }

    //set new intent recognition option
    fun toggleHttpSSLVerificationDisabled(disabled: Boolean) {
        _isHttpSSLVerificationDisabled.value = disabled
    }

    /**
     * save data configuration
     */
    override fun onSave() {
        ConfigurationSettings.httpClientServerEndpointHost.value = _httpServerEndpointHost.value
        ConfigurationSettings.httpClientServerEndpointPort.value = _httpServerEndpointPort.value
        ConfigurationSettings.isHttpClientSSLVerificationDisabled.value =
            _isHttpSSLVerificationDisabled.value
    }

    /**
     * undo all changes
     */
    override fun discard() {
        _httpServerEndpointHost.value = ConfigurationSettings.httpClientServerEndpointHost.value
        _httpServerEndpointPort.value = ConfigurationSettings.httpClientServerEndpointPort.value
        _isHttpSSLVerificationDisabled.value =
            ConfigurationSettings.isHttpClientSSLVerificationDisabled.value
    }

    override fun initializeTestParams() {
        get<HttpClientServiceParams> {
            parametersOf(
                HttpClientServiceParams(
                    isHttpSSLVerificationDisabled = _isHttpSSLVerificationDisabled.value,
                    httpServerEndpointHost = _httpServerEndpointHost.value,
                    httpServerEndpointPort = _httpServerEndpointPort.value
                )
            )
        }
    }

    override fun runTest() = testRunner.startTest()

}
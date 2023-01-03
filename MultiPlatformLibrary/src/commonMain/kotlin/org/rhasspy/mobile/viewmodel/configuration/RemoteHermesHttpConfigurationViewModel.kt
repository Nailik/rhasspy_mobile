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
import org.rhasspy.mobile.services.httpclient.HttpClientService
import org.rhasspy.mobile.services.httpclient.HttpClientServiceParams
import org.rhasspy.mobile.settings.ConfigurationSetting
import org.rhasspy.mobile.viewmodel.configuration.test.RemoteHermesHttpConfigurationTest

class RemoteHermesHttpConfigurationViewModel : IConfigurationViewModel() {
    override val testRunner by inject<RemoteHermesHttpConfigurationTest>()
    override val logType = LogType.HttpClientService

    override val serviceState = get<HttpClientService>().serviceState

    //unsaved data
    private val _httpClientServerEndpointHost = MutableStateFlow(ConfigurationSetting.httpClientServerEndpointHost.value)
    private val _httpClientServerEndpointPort = MutableStateFlow(ConfigurationSetting.httpClientServerEndpointPort.value)
    private val _httpClientServerEndpointPortText = MutableStateFlow(ConfigurationSetting.httpClientServerEndpointPort.value.toString())
    private val _httpClientTimeout = MutableStateFlow(ConfigurationSetting.httpClientTimeout.value)
    private val _httpClientTimeoutText = MutableStateFlow(ConfigurationSetting.httpClientTimeout.value.toString())
    private val _isHttpSSLVerificationDisabled = MutableStateFlow(ConfigurationSetting.isHttpClientSSLVerificationDisabled.value)

    //unsaved ui data
    val httpClientServerEndpointHost = _httpClientServerEndpointHost.readOnly
    val httpClientServerEndpointPort = _httpClientServerEndpointPortText.readOnly
    val httpClientTimeoutText = _httpClientTimeoutText.readOnly
    val isHttpSSLVerificationDisabled = _isHttpSSLVerificationDisabled.readOnly

    override val isTestingEnabled = _httpClientServerEndpointHost.mapReadonlyState { it.isNotBlank() }

    override val hasUnsavedChanges = combineAny(
        combineStateNotEquals(_httpClientServerEndpointHost, ConfigurationSetting.httpClientServerEndpointHost.data),
        combineStateNotEquals(_httpClientServerEndpointPort, ConfigurationSetting.httpClientServerEndpointPort.data),
        combineStateNotEquals(_httpClientTimeout, ConfigurationSetting.httpClientTimeout.data),
        combineStateNotEquals(_isHttpSSLVerificationDisabled, ConfigurationSetting.isHttpClientSSLVerificationDisabled.data)
    )

    //set new http server endpoint host
    fun updateHttpClientServerEndpointHost(endpoint: String) {
        _httpClientServerEndpointHost.value = endpoint
    }

    //set new http server endpoint port
    fun updateHttpClientTimeout(timeout: String) {
        val text = timeout.replace("""[-,. ]""".toRegex(), "")
        _httpClientTimeoutText.value = text
        _httpClientTimeout.value = text.toLongOrNull()
    }

    //set new http server endpoint port
    fun updateHttpClientServerEndpointPort(port: String) {
        val text = port.replace("""[-,. ]""".toRegex(), "")
        _httpClientServerEndpointPortText.value = text
        _httpClientServerEndpointPort.value = text.toIntOrNull() ?: 0
    }

    //set new intent recognition option
    fun toggleHttpSSLVerificationDisabled(disabled: Boolean) {
        _isHttpSSLVerificationDisabled.value = disabled
    }

    /**
     * save data configuration
     */
    override fun onSave() {
        ConfigurationSetting.httpClientServerEndpointHost.value = _httpClientServerEndpointHost.value
        ConfigurationSetting.httpClientServerEndpointPort.value = _httpClientServerEndpointPort.value
        ConfigurationSetting.isHttpClientSSLVerificationDisabled.value = _isHttpSSLVerificationDisabled.value
    }

    /**
     * undo all changes
     */
    override fun discard() {
        _httpClientServerEndpointHost.value = ConfigurationSetting.httpClientServerEndpointHost.value
        _httpClientServerEndpointPort.value = ConfigurationSetting.httpClientServerEndpointPort.value
        _isHttpSSLVerificationDisabled.value = ConfigurationSetting.isHttpClientSSLVerificationDisabled.value
    }

    override fun initializeTestParams() {
        get<HttpClientServiceParams> {
            parametersOf(
                HttpClientServiceParams(
                    isHttpSSLVerificationDisabled = _isHttpSSLVerificationDisabled.value,
                    httpClientServerEndpointHost = _httpClientServerEndpointHost.value,
                    httpClientServerEndpointPort = _httpClientServerEndpointPort.value
                )
            )
        }
    }

    override fun runTest() = testRunner.startTest()

}
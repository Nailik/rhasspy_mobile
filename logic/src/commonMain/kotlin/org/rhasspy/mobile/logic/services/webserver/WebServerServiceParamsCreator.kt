package org.rhasspy.mobile.logic.services.webserver

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.settings.ConfigurationSetting

class WebServerServiceParamsCreator {

    private val updaterScope = CoroutineScope(Dispatchers.IO)
    private val paramsFlow = MutableStateFlow(getParams())

    operator fun invoke(): StateFlow<WebServerServiceParams> {
        updaterScope.launch {
            combineStateFlow(
                ConfigurationSetting.isHttpServerEnabled.data,
                ConfigurationSetting.httpServerPort.data,
                ConfigurationSetting.isHttpServerSSLEnabledEnabled.data,
                ConfigurationSetting.httpServerSSLKeyStoreFile.data,
                ConfigurationSetting.httpServerSSLKeyStorePassword.data,
                ConfigurationSetting.httpServerSSLKeyAlias.data,
                ConfigurationSetting.httpServerSSLKeyPassword.data
            ).collect {
                paramsFlow.value = getParams()
            }
        }

        return paramsFlow
    }

    private fun getParams(): WebServerServiceParams {
        return WebServerServiceParams(
            isHttpServerEnabled = ConfigurationSetting.isHttpServerEnabled.value,
            httpServerPort = ConfigurationSetting.httpServerPort.value,
            isHttpServerSSLEnabled = ConfigurationSetting.isHttpServerSSLEnabledEnabled.value,
            httpServerSSLKeyStoreFile = ConfigurationSetting.httpServerSSLKeyStoreFile.value,
            httpServerSSLKeyStorePassword = ConfigurationSetting.httpServerSSLKeyStorePassword.value,
            httpServerSSLKeyAlias = ConfigurationSetting.httpServerSSLKeyAlias.value,
            httpServerSSLKeyPassword = ConfigurationSetting.httpServerSSLKeyPassword.value
        )
    }

}
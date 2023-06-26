package org.rhasspy.mobile.logic.services.webserver

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.combineStateFlow
import org.rhasspy.mobile.platformspecific.mapReadonlyState
import org.rhasspy.mobile.settings.ConfigurationSetting

class WebServerServiceParamsCreator {

    operator fun invoke(): StateFlow<WebServerServiceParams> {

        return combineStateFlow(
            ConfigurationSetting.isHttpServerEnabled.data,
            ConfigurationSetting.httpServerPort.data,
            ConfigurationSetting.isHttpServerSSLEnabledEnabled.data,
            ConfigurationSetting.httpServerSSLKeyStoreFile.data,
            ConfigurationSetting.httpServerSSLKeyStorePassword.data,
            ConfigurationSetting.httpServerSSLKeyAlias.data,
            ConfigurationSetting.httpServerSSLKeyPassword.data
        ).mapReadonlyState {
            getParams()
        }

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
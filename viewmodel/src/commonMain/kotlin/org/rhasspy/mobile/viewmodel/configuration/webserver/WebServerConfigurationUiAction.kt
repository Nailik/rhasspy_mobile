package org.rhasspy.mobile.viewmodel.configuration.webserver

import okio.Path

sealed interface WebServerConfigurationUiAction {

    sealed interface Change : WebServerConfigurationUiAction {
        data class SetHttpServerEnabled(val value: Boolean) : Change
        data class UpdateHttpServerPort(val value: String) : Change
        data class SetHttpServerSSLEnabled(val value: Boolean) : Change
        data class UpdateHttpSSLKeyStorePassword(val value: String) : Change
        data class UpdateHttpSSLKeyAlias(val value: String) : Change
        data class UpdateHttpSSLKeyPassword(val value: String) : Change
        data class SetHttpServerSSLKeyStoreFile(val value: Path) : Change
    }

    sealed interface Navigate : WebServerConfigurationUiAction {
        object OpenWebServerSSLWiki : Navigate
        object SelectSSLCertificate : Navigate
    }

}
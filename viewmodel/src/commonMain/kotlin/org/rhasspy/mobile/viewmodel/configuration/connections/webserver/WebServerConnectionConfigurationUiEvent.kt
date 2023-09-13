package org.rhasspy.mobile.viewmodel.configuration.connections.webserver

import okio.Path

sealed interface WebServerConnectionConfigurationUiEvent {

    sealed interface Change : WebServerConnectionConfigurationUiEvent {

        data class SetHttpServerEnabled(val value: Boolean) : Change
        data class UpdateHttpServerPort(val value: String) : Change
        data class SetHttpServerSSLEnabled(val value: Boolean) : Change
        data class UpdateHttpSSLKeyStorePassword(val value: String) : Change
        data class UpdateHttpSSLKeyAlias(val value: String) : Change
        data class UpdateHttpSSLKeyPassword(val value: String) : Change
        data class SetHttpServerSSLKeyStoreFile(val value: Path) : Change

    }

    sealed interface Action : WebServerConnectionConfigurationUiEvent {

        data object OpenWebServerSSLWiki : Action
        data object SelectSSLCertificate : Action
        data object BackClick : Action

    }

}
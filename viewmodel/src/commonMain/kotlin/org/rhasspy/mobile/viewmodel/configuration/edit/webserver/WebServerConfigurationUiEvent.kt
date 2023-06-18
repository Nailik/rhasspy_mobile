package org.rhasspy.mobile.viewmodel.configuration.edit.webserver

import okio.Path
import org.rhasspy.mobile.viewmodel.KViewModelUiEvent
import org.rhasspy.mobile.viewmodel.ScreenViewState

sealed interface WebServerConfigurationUiEvent {

    sealed interface Change : WebServerConfigurationUiEvent {

        data class SetHttpServerEnabled(val value: Boolean) : Change
        data class UpdateHttpServerPort(val value: String) : Change
        data class SetHttpServerSSLEnabled(val value: Boolean) : Change
        data class UpdateHttpSSLKeyStorePassword(val value: String) : Change
        data class UpdateHttpSSLKeyAlias(val value: String) : Change
        data class UpdateHttpSSLKeyPassword(val value: String) : Change
        data class SetHttpServerSSLKeyStoreFile(val value: Path) : Change

    }

    sealed interface Action : WebServerConfigurationUiEvent {

        object OpenWebServerSSLWiki : Action
        object SelectSSLCertificate : Action
        object BackClick : Action

    }


    sealed interface SnackBar : WebServerConfigurationUiEvent {

        object Consumed : SnackBar

    }

}
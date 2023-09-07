package org.rhasspy.mobile.viewmodel.configuration.connections.mqtt

import okio.Path

sealed interface MqttConnectionConfigurationUiEvent {

    sealed interface Change : MqttConnectionConfigurationUiEvent {

        data class SetMqttEnabled(val enabled: Boolean) : Change
        data class UpdateMqttHost(val host: String) : Change
        data class UpdateMqttUserName(val userName: String) : Change
        data class UpdateMqttPassword(val password: String) : Change
        data class SetMqttSSLEnabled(val enabled: Boolean) : Change
        data class UpdateMqttConnectionTimeout(val timeout: String) : Change
        data class UpdateMqttKeepAliveInterval(val keepAliveInterval: String) : Change
        data class UpdateMqttRetryInterval(val retryInterval: String) : Change
        data class UpdateMqttKeyStoreFile(val file: Path) : Change

    }

    sealed interface Action : MqttConnectionConfigurationUiEvent {

        data object OpenMqttSSLWiki : Action
        data object SelectSSLCertificate : Action
        data object BackClick : Action

    }

}
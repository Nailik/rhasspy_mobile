package org.rhasspy.mobile.viewmodel.configuration.edit.mqtt

import okio.Path

sealed interface MqttConfigurationUiEvent {

    sealed interface Change : MqttConfigurationUiEvent {

        data class SetMqttEnabled(val enabled: Boolean) : Change
        data class UpdateMqttHost(val host: String) : Change
        data class UpdateMqttPort(val port: String) : Change
        data class UpdateMqttUserName(val userName: String) : Change
        data class UpdateMqttPassword(val password: String) : Change
        data class SetMqttSSLEnabled(val enabled: Boolean) : Change
        data class UpdateMqttConnectionTimeout(val timeout: String) : Change
        data class UpdateMqttKeepAliveInterval(val keepAliveInterval: String) : Change
        data class UpdateMqttRetryInterval(val retryInterval: String) : Change
        data class UpdateMqttKeyStoreFile(val file: Path) : Change

    }

    sealed interface Action : MqttConfigurationUiEvent {

        object OpenMqttSSLWiki : Action
        object SelectSSLCertificate : Action
        object BackClick : Action

    }

}
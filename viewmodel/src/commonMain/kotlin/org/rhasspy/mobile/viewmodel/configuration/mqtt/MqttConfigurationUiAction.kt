package org.rhasspy.mobile.viewmodel.configuration.mqtt

import okio.Path

sealed interface MqttConfigurationUiAction {

    sealed interface Change : MqttConfigurationUiAction {
        data class SetMqttEnabled(val enabled: Boolean) : Change
        data class UpdateMqttHost(val value: String) : Change
        data class UpdateMqttPort(val value: String) : Change
        data class UpdateMqttUserName(val value: String) : Change
        data class UpdateMqttPassword(val value: String) : Change
        data class SetMqttSSLEnabled(val enabled: Boolean) : Change
        data class UpdateMqttConnectionTimeout(val value: String) : Change
        data class UpdateMqttKeepAliveInterval(val value: String) : Change
        data class UpdateMqttRetryInterval(val value: String) : Change
        data class UpdateMqttKeyStoreFile(val value: Path) : Change
    }

    sealed interface Navigate : MqttConfigurationUiAction {
        object OpenMqttSSLWiki : Navigate
        object SelectSSLCertificate : Navigate
    }

}
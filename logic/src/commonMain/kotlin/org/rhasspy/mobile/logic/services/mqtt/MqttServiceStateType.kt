package org.rhasspy.mobile.logic.services.mqtt

import org.rhasspy.mobile.MR
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.platformspecific.mqtt.MqttStatus

enum class MqttServiceStateType(val serviceState: ServiceState) {
    SUCCESS(ServiceState.Success),
    UNACCEPTABLE_PROTOCOL(ServiceState.Error(MR.strings.unacceptable_protocol)),
    IDENTIFIER_REJECTED(ServiceState.Error(MR.strings.identifier_rejected)),
    SERVER_UNAVAILABLE(ServiceState.Error(MR.strings.server_unavailable)),
    INVALID_CREDENTIALS(ServiceState.Error(MR.strings.invalid_credentials)),
    NOT_AUTHORIZED(ServiceState.Error(MR.strings.not_authorized)),
    ALREADY_CONNECTED(ServiceState.Error(MR.strings.already_connected)),
    MSG_DELIVERY_FAILED(ServiceState.Error(MR.strings.msg_delivery_failed)),
    MSG_PERSISTENCE_FAILED(ServiceState.Error(MR.strings.msg_persistence_failed)),
    SUBSCRIBE_FAILED(ServiceState.Error(MR.strings.subscribe_failed)),
    UNKNOWN(ServiceState.Exception()),
    TopicSubscriptionFailed(ServiceState.Error(MR.strings.topic_subscription_failed));

    companion object {
        fun fromMqttStatus(mqttStatus: MqttStatus): MqttServiceStateType {
            return when (mqttStatus) {
                MqttStatus.SUCCESS -> SUCCESS
                MqttStatus.UNACCEPTABLE_PROTOCOL -> UNACCEPTABLE_PROTOCOL
                MqttStatus.IDENTIFIER_REJECTED -> IDENTIFIER_REJECTED
                MqttStatus.SERVER_UNAVAILABLE -> SERVER_UNAVAILABLE
                MqttStatus.INVALID_CREDENTIALS -> INVALID_CREDENTIALS
                MqttStatus.NOT_AUTHORIZED -> NOT_AUTHORIZED
                MqttStatus.ALREADY_CONNECTED -> ALREADY_CONNECTED
                MqttStatus.MSG_DELIVERY_FAILED -> MSG_DELIVERY_FAILED
                MqttStatus.MSG_PERSISTENCE_FAILED -> MSG_PERSISTENCE_FAILED
                MqttStatus.SUBSCRIBE_FAILED -> SUBSCRIBE_FAILED
                MqttStatus.UNKNOWN -> UNKNOWN
            }
        }
    }
}
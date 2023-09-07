package org.rhasspy.mobile.logic.connections.mqtt

import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.ServiceState
import org.rhasspy.mobile.platformspecific.mqtt.MqttStatus
import org.rhasspy.mobile.resources.MR

enum class MqttConnectionStateType(val serviceState: ServiceState) {
    SUCCESS(ServiceState.Success),
    UNACCEPTABLE_PROTOCOL(ServiceState.Error(MR.strings.unacceptable_protocol.stable)),
    IDENTIFIER_REJECTED(ServiceState.Error(MR.strings.identifier_rejected.stable)),
    SERVER_UNAVAILABLE(ServiceState.Error(MR.strings.server_unavailable.stable)),
    INVALID_CREDENTIALS(ServiceState.Error(MR.strings.invalid_credentials.stable)),
    NOT_AUTHORIZED(ServiceState.Error(MR.strings.not_authorized.stable)),
    ALREADY_CONNECTED(ServiceState.Error(MR.strings.already_connected.stable)),
    MSG_DELIVERY_FAILED(ServiceState.Error(MR.strings.msg_delivery_failed.stable)),
    MSG_PERSISTENCE_FAILED(ServiceState.Error(MR.strings.msg_persistence_failed.stable)),
    SUBSCRIBE_FAILED(ServiceState.Error(MR.strings.subscribe_failed.stable)),
    UNKNOWN(ServiceState.Exception()),
    TopicSubscriptionFailed(ServiceState.Error(MR.strings.topic_subscription_failed.stable));

    companion object {
        fun fromMqttStatus(mqttStatus: MqttStatus): MqttConnectionStateType {
            return when (mqttStatus) {
                MqttStatus.SUCCESS                -> SUCCESS
                MqttStatus.UNACCEPTABLE_PROTOCOL  -> UNACCEPTABLE_PROTOCOL
                MqttStatus.IDENTIFIER_REJECTED    -> IDENTIFIER_REJECTED
                MqttStatus.SERVER_UNAVAILABLE     -> SERVER_UNAVAILABLE
                MqttStatus.INVALID_CREDENTIALS    -> INVALID_CREDENTIALS
                MqttStatus.NOT_AUTHORIZED         -> NOT_AUTHORIZED
                MqttStatus.ALREADY_CONNECTED      -> ALREADY_CONNECTED
                MqttStatus.MSG_DELIVERY_FAILED    -> MSG_DELIVERY_FAILED
                MqttStatus.MSG_PERSISTENCE_FAILED -> MSG_PERSISTENCE_FAILED
                MqttStatus.SUBSCRIBE_FAILED       -> SUBSCRIBE_FAILED
                MqttStatus.UNKNOWN                -> UNKNOWN
            }
        }
    }
}
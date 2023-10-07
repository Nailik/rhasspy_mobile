package org.rhasspy.mobile.logic.connections.mqtt

import org.rhasspy.mobile.data.resource.stable
import org.rhasspy.mobile.data.service.ConnectionState
import org.rhasspy.mobile.platformspecific.mqtt.MqttStatus
import org.rhasspy.mobile.resources.MR

internal enum class MqttConnectionStateType(val connectionState: ConnectionState) {

    SUCCESS(ConnectionState.Success),
    UNACCEPTABLE_PROTOCOL(ConnectionState.ErrorState(MR.strings.unacceptable_protocol.stable)),
    IDENTIFIER_REJECTED(ConnectionState.ErrorState(MR.strings.identifier_rejected.stable)),
    SERVER_UNAVAILABLE(ConnectionState.ErrorState(MR.strings.server_unavailable.stable)),
    INVALID_CREDENTIALS(ConnectionState.ErrorState(MR.strings.invalid_credentials.stable)),
    NOT_AUTHORIZED(ConnectionState.ErrorState(MR.strings.not_authorized.stable)),
    ALREADY_CONNECTED(ConnectionState.ErrorState(MR.strings.already_connected.stable)),
    MSG_DELIVERY_FAILED(ConnectionState.ErrorState(MR.strings.msg_delivery_failed.stable)),
    MSG_PERSISTENCE_FAILED(ConnectionState.ErrorState(MR.strings.msg_persistence_failed.stable)),
    SUBSCRIBE_FAILED(ConnectionState.ErrorState(MR.strings.subscribe_failed.stable)),
    UNKNOWN(ConnectionState.ErrorState(MR.strings.unknown_error.stable)),
    TopicSubscriptionFailed(ConnectionState.ErrorState(MR.strings.topic_subscription_failed.stable));

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
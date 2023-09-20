package org.rhasspy.mobile.logic.connections.mqtt

import kotlinx.coroutines.flow.StateFlow
import org.rhasspy.mobile.platformspecific.combineState
import org.rhasspy.mobile.settings.ConfigurationSetting

internal class MqttConnectionParamsCreator {

    operator fun invoke(): StateFlow<MqttConnectionParams> {

        return combineState(
            ConfigurationSetting.siteId.data,
            ConfigurationSetting.mqttConnection.data,
        ) { siteId, mqttConnectionParams ->
            MqttConnectionParams(
                siteId = siteId,
                mqttConnectionData = mqttConnectionParams,
            )
        }

    }

}
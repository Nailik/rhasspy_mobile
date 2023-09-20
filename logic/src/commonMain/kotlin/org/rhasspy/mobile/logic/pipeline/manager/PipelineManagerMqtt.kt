package org.rhasspy.mobile.logic.pipeline.manager

import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent
import org.rhasspy.mobile.logic.connections.webserver.WebServerConnectionEvent
import org.rhasspy.mobile.logic.pipeline.PipelineEvent

class PipelineManagerMqtt : PipelineManager {

    override fun initialize() {
        TODO("Not yet implemented")
    }

    override fun <T> onEvent(event: T) where T : PipelineEvent, T : MqttConnectionEvent, T : WebServerConnectionEvent {
        TODO("Not yet implemented")
    }

}
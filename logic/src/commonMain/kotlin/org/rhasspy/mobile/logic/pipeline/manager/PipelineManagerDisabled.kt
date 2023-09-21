package org.rhasspy.mobile.logic.pipeline.manager

import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent
import org.rhasspy.mobile.logic.connections.webserver.WebServerConnectionEvent
import org.rhasspy.mobile.logic.domains.mic.IMicDomain
import org.rhasspy.mobile.logic.local.audiofocus.IAudioFocus
import org.rhasspy.mobile.logic.local.indication.IIndication
import org.rhasspy.mobile.logic.pipeline.PipelineEvent

class PipelineManagerDisabled(
    private val indication: IIndication,
    private val audioFocus: IAudioFocus,
    private val micDomain: IMicDomain,
) : PipelineManager(
    indication = indication,
    audioFocus = audioFocus,
    micDomain = micDomain,
) {
    override fun initialize() {
        //TODO("Not yet implemented")
    }

    override fun onEvent(event: PipelineEvent) {
        //TODO("Not yet implemented")
    }

    override fun onEvent(event: MqttConnectionEvent) {
        //TODO("Not yet implemented")
    }

    override fun onEvent(event: WebServerConnectionEvent) {
        //TODO("Not yet implemented")
    }
}
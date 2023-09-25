package org.rhasspy.mobile.logic.pipeline.impl

import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.rhasspy.mobile.logic.connections.mqtt.IMqttConnection
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnection
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.*
import org.rhasspy.mobile.logic.pipeline.IntentResult.NotRecognized
import org.rhasspy.mobile.logic.pipeline.PipelineResult
import org.rhasspy.mobile.logic.pipeline.PipelineResult.End
import org.rhasspy.mobile.logic.pipeline.TranscriptResult
import org.rhasspy.mobile.logic.pipeline.TranscriptResult.TranscriptError

class PipelineMqtt(
    private val mqttConnection: IMqttConnection
) : IPipeline {

    //started by HotWordDetected or StartSession, maybe SessionStarted??
    override suspend fun runPipeline(sessionId: String): PipelineResult {

        mqttConnection.incomingMessages.map {
            when(it) {
                is AsrResult.AsrError                   -> TranscriptError
                is AsrResult.AsrTextCaptured            -> TODO()
                is EndSession                           -> End //TODO even necessary to receive end?
                is HotWordDetected                      -> TODO()
                is IntentResult.IntentNotRecognized     -> NotRecognized
                is IntentResult.IntentRecognitionResult -> TODO()
                is PlayResult.PlayBytes                 -> TODO()
                is PlayResult.PlayFinished              -> TODO()
                is Say                                  -> TODO()
                is SessionEnded                         -> {
                    if(it.sessionId == sessionId) {
                        End
                    }
                }
                is SessionStarted                       -> TODO()
                is StartListening                       -> TODO()
                is StartSession                         -> TODO()
                is StopListening                        -> TODO()
            }
        }.filterIsInstance<PipelineResult>()
            .first()

    }

}
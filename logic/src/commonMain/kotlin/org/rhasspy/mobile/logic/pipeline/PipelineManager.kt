package org.rhasspy.mobile.logic.pipeline

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.rhasspy.mobile.data.pipeline.PipelineData
import org.rhasspy.mobile.data.service.option.DialogManagementOption
import org.rhasspy.mobile.logic.connections.mqtt.IMqttConnection
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.HotWordDetected
import org.rhasspy.mobile.logic.connections.mqtt.MqttConnectionEvent.SessionStarted
import org.rhasspy.mobile.logic.connections.user.IUserConnection
import org.rhasspy.mobile.logic.connections.user.UserConnectionEvent.StartStopRhasspy
import org.rhasspy.mobile.logic.connections.webserver.IWebServerConnection
import org.rhasspy.mobile.logic.connections.webserver.WebServerConnectionEvent.StartSession
import org.rhasspy.mobile.logic.domains.mic.IMicDomain
import org.rhasspy.mobile.logic.domains.wake.IWakeDomain
import org.rhasspy.mobile.logic.domains.wake.WakeEvent.Detection
import org.rhasspy.mobile.logic.local.indication.IIndication
import org.rhasspy.mobile.logic.pipeline.HandleResult.HandleDisabled
import org.rhasspy.mobile.logic.pipeline.HandleResult.NotHandled
import org.rhasspy.mobile.logic.pipeline.IntentResult.IntentDisabled
import org.rhasspy.mobile.logic.pipeline.IntentResult.NotRecognized
import org.rhasspy.mobile.logic.pipeline.PipelineResult.End
import org.rhasspy.mobile.logic.pipeline.SndResult.*
import org.rhasspy.mobile.logic.pipeline.TranscriptResult.*
import org.rhasspy.mobile.logic.pipeline.TtsResult.NotSynthesized
import org.rhasspy.mobile.logic.pipeline.TtsResult.TtsDisabled
import org.rhasspy.mobile.settings.ConfigurationSetting

interface IPipelineManager {


}

class PipelineManager(
    private val mqttConnection: IMqttConnection,
    private val webServerConnection: IWebServerConnection,
    private val userConnection: IUserConnection,
    private val indication: IIndication,
) : IPipelineManager, KoinComponent {

    private val params: PipelineData = ConfigurationSetting.pipelineData.value

    private val scope = CoroutineScope(Dispatchers.IO)

    private var pipelineJob: Job? = null

    init {
        scope.launch {
            ConfigurationSetting.pipelineData.data.collect {
                //todo restart
            }
        }
    }

    //TODO allow:
    //Mqtt: PlayBytes
    //WebServer: WebServerPlayWav, WebServerSay
    //Local: PlayRecording


    //TODO observe configuration -> Reset pipeline (close all domains) (no more save)
    //TODO reload wake domain on changes all other domains on the fly? -> reload button and on page leave

    private fun awaitPipelineStart() {

        val wakeDomain = get<IWakeDomain>()
        val micDomain = get<IMicDomain>()
        wakeDomain.awaitDetection(micDomain.audioStream)

        scope.launch {
            val startEvent = merge(
                //Mqtt: SessionStarted
                mqttConnection.incomingMessages
                    .filterIsInstance<SessionStarted>()
                    .map {
                        StartEvent(
                            sessionId = it.sessionId,
                            wakeWord = "Rhasspy2HermesMqtt_SessionStarted",
                        )
                    },
                //Mqtt: HotWordDetected
                mqttConnection.incomingMessages
                    .filterIsInstance<HotWordDetected>()
                    .map {
                        StartEvent(
                            sessionId = null,
                            wakeWord = it.hotWord,
                        )
                    },
                //User: User Button Click
                userConnection.incomingMessages
                    .filterIsInstance<StartStopRhasspy>()
                    .map {
                        StartEvent(
                            sessionId = null,
                            wakeWord = "ManualUser",
                        )
                    },
                //Webserver: startRecording, listenForCommand
                webServerConnection.incomingMessages
                    .filterIsInstance<StartSession>()
                    .map {
                        StartEvent(
                            sessionId = null,
                            wakeWord = "Rhasspy2HermesHttp",
                        )
                    },
                //Local: Local WakeWord
                wakeDomain.wakeEvents
                    .filterIsInstance<Detection>()
                    .map {
                        StartEvent(
                            sessionId = null,
                            wakeWord = it.name,
                        )
                    }
            ).first()

            //run pipeline with sessionId
            runPipeline(startEvent)
        }

    }

    private fun runPipeline(startEvent: StartEvent) {
        pipelineJob = scope.launch {
            when (get<IPipeline>().runPipeline(startEvent)) {
                is End,
                is HandleDisabled,
                is IntentDisabled,
                is PlayDisabled,
                is Played,
                is TtsDisabled,
                is TranscriptDisabled -> {
                    //Success: indication idle
                    indication.onIdle()
                }

                is NotHandled,
                is NotPlayed,
                is NotRecognized,
                is NotSynthesized,
                is TranscriptError,
                is TranscriptTimeout  -> {
                    //Error: indication error
                    indication.onIdle()
                    indication.onError()
                }
            }
            awaitPipelineStart()
        }
        //TODO dispose all domains?
    }

    private fun getPipeline(): IPipeline {
        when (params.option) {
            DialogManagementOption.Local -> TODO()
            DialogManagementOption.Rhasspy2HermesMQTT -> TODO()
            DialogManagementOption.Disabled -> TODO()
        }
    }

}